package com.mytechia.robobo.framework.remotecontrol.robobo_discovery;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;

public class RoboboDiscoveryModule implements IRoboboDiscoveryModule{
    String TAG = "Robobo Discovery Module";
    String version = "0.5.0-SNAPSHOT";

    private UDPInfoServer infoServer;
    private int bufferSize = 1024;
    private int serverPort = 44444;

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        infoServer = new UDPInfoServer(serverPort, bufferSize, manager);
        infoServer.start();
    }

    @Override
    public void shutdown() throws InternalErrorException {
        try {
            infoServer.interrupt();
            infoServer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
        return infoServer.getRoboboBTName();
    }

    @Override
    public void setRoboboBTName(String roboboBTName) {
        infoServer.setRoboboBTName(roboboBTName);
    }
}
