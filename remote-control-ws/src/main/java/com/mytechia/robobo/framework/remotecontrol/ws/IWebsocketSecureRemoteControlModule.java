package com.mytechia.robobo.framework.remotecontrol.ws;

import com.mytechia.robobo.framework.IModule;

public interface IWebsocketSecureRemoteControlModule extends IModule {

    String getRoboboBTName();

    void setRoboboBTName(String roboboBTName);

    void startWssServer();
}
