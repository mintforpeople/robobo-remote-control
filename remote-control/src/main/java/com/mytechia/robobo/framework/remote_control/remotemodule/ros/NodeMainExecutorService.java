/*******************************************************************************
 *
 *   Copyright 2017 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2017 Gervasio Varela <gervasio.varela@mytechia.com>
 *   Copyright 2017 Julio Gomez <julio.gomez@mytechia.com>
 *
 *   This file is part of Robobo Ros Module.
 *
 *   Robobo Ros Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Ros Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Ros Module.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/
package com.mytechia.robobo.framework.remote_control.remotemodule.ros;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import org.ros.RosCore;
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

/**
 * Created by julio on 11/07/17.
 */

public class NodeMainExecutorService extends Service implements NodeMainExecutor {


    private static final String TAG = "NodeMainExecutorService";

    public static final String MASTER_URI = "org.ros.android.publicMaster.uri";

    public static final String ROS_HOST_NAME = "org.ros.android.ros.host.name";

    public static final String ROS_PORT = "org.ros.android.ros.port";

    public static final String ROS_PUBLI_MASTER_NODE = "org.ros.android.ros.public.master.node";

    public static final int DEFAULT_ROS_PORT=11311;

    private final NodeMainExecutor nodeMainExecutor;

    private final IBinder binder;

    private final ListenerGroup<NodeMainExecutorServiceListener> listeners;

    private PowerManager.WakeLock wakeLock;

    private WifiManager.WifiLock wifiLock;

    private RosCore rosCore;

    private URI masterUri;

    private String rosHostname;

    private int rosPort;




    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public NodeMainExecutorService getService() {
            return NodeMainExecutorService.this;
        }
    }

    public NodeMainExecutorService() {
        super();
        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        binder = new LocalBinder();
        listeners =new ListenerGroup<NodeMainExecutorServiceListener>(nodeMainExecutor.getScheduledExecutorService());
    }

    @Override
    public void onCreate() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        wakeLock.acquire();

        int wifiLockType = WifiManager.WIFI_MODE_FULL;

        try {
            wifiLockType = WifiManager.class.getField("WIFI_MODE_FULL_HIGH_PERF").getInt(null);
        } catch (Exception e) {
            // We must be running on a pre-Honeycomb device.
            Log.w(TAG, "Unable to acquire high performance wifi lock.", e);
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        wifiLock = wifiManager.createWifiLock(wifiLockType, TAG);

        wifiLock.acquire();
    }

    @Override
    public void execute(NodeMain nodeMain, NodeConfiguration nodeConfiguration,  Collection<NodeListener> nodeListeneners) {
        nodeMainExecutor.execute(nodeMain, nodeConfiguration, nodeListeneners);
    }

    @Override
    public void execute(NodeMain nodeMain, NodeConfiguration nodeConfiguration) {
        execute(nodeMain, nodeConfiguration, null);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        return nodeMainExecutor.getScheduledExecutorService();
    }

    @Override
    public void shutdownNodeMain(NodeMain nodeMain) {
        nodeMainExecutor.shutdownNodeMain(nodeMain);
    }

    @Override
    public void shutdown() {
        forceShutdown();
    }

    private void forceShutdown() {
        signalOnShutdown();
        stopForeground(true);
        stopSelf();
    }

    public void addListener(NodeMainExecutorServiceListener listener) {
        listeners.add(listener);
    }

    private void signalOnShutdown() {
        listeners.signal(new SignalRunnable<NodeMainExecutorServiceListener>() {
            @Override
            public void run(NodeMainExecutorServiceListener nodeMainExecutorServiceListener) {
                nodeMainExecutorServiceListener.onShutdown(NodeMainExecutorService.this);
            }
        });
    }

    private void signalOnStartup(){

        listeners.signal(new SignalRunnable<NodeMainExecutorServiceListener>() {
            @Override
            public void run(NodeMainExecutorServiceListener nodeMainExecutorServiceListener) {
                nodeMainExecutorServiceListener.ontStartup(NodeMainExecutorService.this);
            }
        });

    }



    @Override
    public void onDestroy() {

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
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.hasExtra(MASTER_URI)) {
            String strMasterUri = intent.getStringExtra(MASTER_URI);
            try {
                this.masterUri = new URI(strMasterUri);
            } catch (URISyntaxException ex) {
                Log.d(TAG, "No valid publicMaster uri", ex);
                this.stopSelf(startId);
            }
        }

        if (intent.hasExtra(ROS_HOST_NAME)) {
            this.rosHostname = intent.getStringExtra(ROS_HOST_NAME);
            this.rosPort = intent.getIntExtra(ROS_PORT, DEFAULT_ROS_PORT);
        }

        boolean publicMaster = intent.getBooleanExtra(ROS_PUBLI_MASTER_NODE, false);

        if(publicMaster) {
            this.startMaster(false);
        }

        return START_NOT_STICKY;


    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    public URI getMasterUri() {
        return masterUri;
    }

    public String getRosHostname() {
        return rosHostname;
    }


    public int getRosPort() {
        return rosPort;
    }

    public void setRosPort(int rosPort) {
        this.rosPort = rosPort;
    }

    /**
     * This version of startMaster can only create private masters.
     *
     * @deprecated use {@link public void startMaster(Boolean isPrivate)} instead.
     */
    @Deprecated
    private void startMaster() {
        startMaster(true);
    }

    /**
     * Starts a new ros publicMaster in an AsyncTask.
     * @param isPrivate
     */
    private void startMaster(boolean isPrivate) {

        Log.d(TAG, String.format("Starting public master ros node [private=%s]", isPrivate));

        AsyncTask<Boolean, Void, URI> task = new AsyncTask<Boolean, Void, URI>() {
            @Override
            protected URI doInBackground(Boolean[] params) {
                NodeMainExecutorService.this.startMasterBlocking(params[0]);
                return NodeMainExecutorService.this.getMasterUri();
            }
        };
        task.execute(isPrivate);
        try {
            task.get();
        } catch (InterruptedException e) {
            throw new RosRuntimeException(e);
        } catch (ExecutionException e) {
            throw new RosRuntimeException(e);
        }

        signalOnStartup();
    }

    /**
     * Private blocking method to start a Ros Master.
     * @param isPrivate
     */
    private void startMasterBlocking(boolean isPrivate) {
        if (isPrivate) {
            rosCore = RosCore.newPrivate();
        } else if (rosHostname != null) {
            rosCore = RosCore.newPublic(rosHostname, rosPort);
        } else {
            rosCore = RosCore.newPublic(rosPort);
        }
        rosCore.start();

        try {
            rosCore.awaitStart();
        } catch (Exception e) {
            throw new RosRuntimeException(e);
        }
        this.masterUri = rosCore.getUri();
    }


}
