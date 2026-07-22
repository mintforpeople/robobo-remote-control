package com.mytechia.robobo.framework.remotecontrol.remote_mdns;

import android.content.res.AssetManager;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;


public class RemoteMDNSModule implements IRemoteMDNSModule {
    String TAG = "Robobo MDNS Module";
    String version = "0.0.5-SNAPSHOT";

    private final AtomicReference<JmDNS> jmdnsRef = new AtomicReference<>(null);
    private Thread startupThread;

    // mDNS configuration (loaded from remote.properties with defaults)
    private String mdnsHostname = "robobo-000";
    private int mdnsServicePort = 44304;
    private String serviceInstanceName = "Robobo 000";
    private String serviceType = "_robobo._tcp.local.";

    // Robot name set externally by the main app (mirrors RoboboDiscoveryModule behavior)
    private String roboboBTName = "ROB-000";

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {

    }

    @Override
    public void shutdown() throws InternalErrorException {
        JmDNS instance = jmdnsRef.getAndSet(null);
        if (instance != null) {
            instance.unregisterAllServices();
            try {
                instance.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing JmDNS instance", e);
            }
        }
        if (startupThread != null && startupThread.isAlive()) {
            startupThread.interrupt();
        }
    }

    @Override
    public String getModuleInfo() {
        return TAG;
    }

    @Override
    public String getModuleVersion() {
        return version;
    }

    @Override
    public String getRoboboBTName() {
        return roboboBTName;
    }

    @Override
    public void setRoboboBTName(String roboboBTName) {
        this.roboboBTName = roboboBTName;
        // Update derived values
        if (roboboBTName.startsWith("ROB-")) {
            this.serviceInstanceName = "Robobo " + roboboBTName.substring(4);
            this.mdnsHostname = "robobo-" + roboboBTName.substring(4).toLowerCase();
        } else {
            this.serviceInstanceName = "Robobo " + roboboBTName;
            this.mdnsHostname = "robobo-" + roboboBTName.substring(4).toLowerCase();
        }
    }

    public void startMDNSServer(){
        startupThread = new Thread(() -> {
            try {
                // Create JmDNS with custom hostname (will advertise as robobo-000.local)
                InetAddress addr = InetAddress.getLocalHost();
                JmDNS instance = JmDNS.create(addr, mdnsHostname);
                jmdnsRef.set(instance);

                // Update service instance name to include the robot name (mirrors RoboboDiscoveryModule)
                String serviceName = "Robobo " + roboboBTName.substring(4); // e.g., "Robobo 000" from "ROB-000"
                if (!roboboBTName.startsWith("ROB-")) {
                    serviceName = "Robobo " + roboboBTName;
                }

                ServiceInfo serviceInfo = ServiceInfo.create(
                        serviceType, serviceName, mdnsServicePort, "id=" + roboboBTName);
                instance.registerService(serviceInfo);

                Log.i(TAG, "mDNS service registered: " + serviceName + " on " + mdnsHostname + ".local:" + mdnsServicePort);
                Log.i(TAG, "Service type: " + serviceType);
                Log.i(TAG, "Discoverable at: " + mdnsHostname + ".local (mDNS hostname)");
            } catch (IOException e) {
                Log.e(TAG, "Failed to start mDNS module", e);
            }
        }, "mdns-startup");
        startupThread.start();
    }
}
