package com.mytechia.robobo.framework.remotecontrol.remote_mdns;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;


public class RemoteMDNSModule implements IRemoteMDNSModule{
    String TAG = "Robobo MDNS Module";
    String version = "0.0.1-SNAPSHOT";

    JmDNS jmdns;
    String RoboboBTName = "ROB-???";

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        try {
            // Create a JmDNS instance
            jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Register a service
            // Get the connected Robobo BTName somehow.
            // Let's see how that's one on the Discovery module
            ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "example", 44304, "path=index.html");
            jmdns.registerService(serviceInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() throws InternalErrorException {
        // Unregister all services
        jmdns.unregisterAllServices();
    }

    @Override
    public String getModuleInfo() {
        return "";
    }

    @Override
    public String getModuleVersion() {
        return "";
    }
}
