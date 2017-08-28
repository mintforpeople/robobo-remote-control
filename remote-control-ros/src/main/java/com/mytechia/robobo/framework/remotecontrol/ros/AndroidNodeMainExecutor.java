package com.mytechia.robobo.framework.remotecontrol.ros;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import org.ros.RosCore;
import org.ros.address.InetAddressFactory;
import org.ros.concurrent.ListenerGroup;
import org.ros.concurrent.SignalRunnable;
import org.ros.exception.RosRuntimeException;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeListener;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;

import static android.content.Context.POWER_SERVICE;
import static android.content.Context.WIFI_SERVICE;


/**
 * Created by julio on 4/08/17.
 */

public class AndroidNodeMainExecutor implements NodeMainExecutor {

    private static final String TAG = "NodeMainExecutorService";

    private final NodeMainExecutor nodeMainExecutor;

    private final ListenerGroup<NodeMainExecutorServiceListener> listeners;

    private final PowerManager.WakeLock wakeLock;

    private final WifiManager.WifiLock wifiLock;

    private final boolean publicMaster;

    private Integer rosPort= 11311;

    private  String rosHostname;

    private  URI masterUri;

    private RosCore rosCore;




    public AndroidNodeMainExecutor(Context context, String masterUri, String rosHostName) throws URISyntaxException{

        super();

        this.nodeMainExecutor = DefaultNodeMainExecutor.newDefault();

        this.listeners = new ListenerGroup<NodeMainExecutorServiceListener>(nodeMainExecutor.getScheduledExecutorService());

        PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService(POWER_SERVICE);

        this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        this.wakeLock.acquire();

        int wifiLockType = WifiManager.WIFI_MODE_FULL;

        try {
            wifiLockType = WifiManager.class.getField("WIFI_MODE_FULL_HIGH_PERF").getInt(null);
        } catch (Exception e) {
            this.shutdown();
            // We must be running on a pre-Honeycomb device.
            Log.w(TAG, "Unable to acquire high performance wifi lock.", e);
        }

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

        this.wifiLock = wifiManager.createWifiLock(wifiLockType, TAG);

        wifiLock.acquire();


        try {
            this.masterUri = new URI(masterUri);
        } catch (URISyntaxException ex) {
            this.shutdown();
            throw ex;
        }

        if (rosHostname != null) {
            this.rosHostname = rosHostName;
        }

        String localHostAddress = InetAddressFactory.newNonLoopback().getHostAddress();

        this.publicMaster = (this.masterUri.getHost().equals("localhost")
                            || this.masterUri.getHost().equals("127.0.0.1")
                            || this.masterUri.getHost().equals(localHostAddress));


        if (publicMaster) {
            this.rosPort = this.masterUri.getPort();
            this.startMasterNode(false);
        }

    }



    private void startMasterNode(boolean isPrivate) {

        Log.d(TAG, String.format("Starting public master ros node [private=%s]", isPrivate));

        AsyncTask<Boolean, Void, URI> asyncTaskStartRosMaster = new AsyncTask<Boolean, Void, URI>() {
            @Override
            protected URI doInBackground(Boolean[] params) {

                boolean isPrivate= params[0];

                if (isPrivate) {
                    AndroidNodeMainExecutor.this.rosCore = RosCore.newPrivate();
                } else if (rosHostname != null) {
                    AndroidNodeMainExecutor.this.rosCore = RosCore.newPublic(rosHostname, rosPort);
                } else {
                    AndroidNodeMainExecutor.this.rosCore = RosCore.newPublic(rosPort);
                }
                AndroidNodeMainExecutor.this.rosCore.start();

                try {
                    AndroidNodeMainExecutor.this.rosCore.awaitStart();
                } catch (Exception e) {
                    throw new RosRuntimeException(e);
                }

                AndroidNodeMainExecutor.this.masterUri = rosCore.getUri();

                return AndroidNodeMainExecutor.this.masterUri;
            }
        };

        asyncTaskStartRosMaster.execute(isPrivate);

        try {
            asyncTaskStartRosMaster.get();
        } catch (InterruptedException e) {
            throw new RosRuntimeException("Error starting ros master", e);
        } catch (ExecutionException e) {
            throw new RosRuntimeException("Error starting ros master", e);
        }

        this.fireEventStartup();
    }


    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        return nodeMainExecutor.getScheduledExecutorService();
    }

    @Override
    public void execute(NodeMain nodeMain, NodeConfiguration nodeConfiguration, Collection<NodeListener> nodeListeneners) {
        this.nodeMainExecutor.execute(nodeMain, nodeConfiguration, nodeListeneners);
    }

    @Override
    public void execute(NodeMain nodeMain, NodeConfiguration nodeConfiguration) {
        this.execute(nodeMain, nodeConfiguration, null);
    }

    @Override
    public void shutdownNodeMain(NodeMain nodeMain) {
        this.nodeMainExecutor.shutdownNodeMain(nodeMain);
    }

    @Override
    public void shutdown() {

        Log.i(TAG, "Shutting down Node Main Executor Service ...");

        this.nodeMainExecutor.shutdown();

        if (rosCore != null) {
            rosCore.shutdown();
        }
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }

        this.fireEventShutdown();
    }


    public void addListener(NodeMainExecutorServiceListener listener) {
        listeners.add(listener);
    }


    private void fireEventShutdown() {
        listeners.signal(new SignalRunnable<NodeMainExecutorServiceListener>() {
            @Override
            public void run(NodeMainExecutorServiceListener nodeMainExecutorServiceListener) {
                nodeMainExecutorServiceListener.onShutdown(AndroidNodeMainExecutor.this);
            }
        });
    }

    private void fireEventStartup(){

        listeners.signal(new SignalRunnable<NodeMainExecutorServiceListener>() {
            @Override
            public void run(NodeMainExecutorServiceListener nodeMainExecutorServiceListener) {
                nodeMainExecutorServiceListener.ontStartup(AndroidNodeMainExecutor.this);
            }
        });

    }
}



