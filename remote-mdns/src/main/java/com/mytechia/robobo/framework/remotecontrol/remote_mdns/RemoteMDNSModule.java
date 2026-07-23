package com.mytechia.robobo.framework.remotecontrol.remote_mdns;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;


public class RemoteMDNSModule implements IRemoteMDNSModule {
    String TAG = "Robobo MDNS Module";
    String version = "0.1.1-SNAPSHOT";

    private RoboboMDNSServer mdnsServer;

    private RoboboManager roboboManager;


    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        roboboManager = manager;
        mdnsServer = new RoboboMDNSServer();
    }

    @Override
    public void shutdown() throws InternalErrorException {
        mdnsServer.stop();
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
        return mdnsServer.getRoboboBTName();
    }

    @Override
    public void setRoboboBTName(String roboboBTName) {
        mdnsServer.setRoboboBTName(roboboBTName);
    }

    public void startMDNSServer(){
        mdnsServer.start(roboboManager.getApplicationContext());
    }
}
