package com.mytechia.robobo.framework.remotecontrol.remote_mdns;

import com.mytechia.robobo.framework.IModule;

public interface IRemoteMDNSModule extends IModule {

    String getRoboboBTName();

    void setRoboboBTName(String roboboBTName);

    void startMDNSServer();
}
