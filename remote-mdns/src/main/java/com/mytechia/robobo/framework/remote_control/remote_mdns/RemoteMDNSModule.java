package com.mytechia.robobo.framework.remote_control.remote_mdns;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;

public class RemoteMDNSModule implements IRemoteMDNSModule{
    String TAG = "Robobo MDNS Module";
    String version = "0.0.1-SNAPSHOT";

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {

    }

    @Override
    public void shutdown() throws InternalErrorException {

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
