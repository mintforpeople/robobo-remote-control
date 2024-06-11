package com.mytechia.robobo.framework.remote_control.robobo_discovery;

import com.mytechia.robobo.framework.IModule;

public interface IRoboboDiscoveryModule extends IModule {
    // Module tasked to reply to UDP discovery requests sent to broadcast interface over the network
    // To a certain port so we know that there's a robot there listening and running
    // Should run whenever the robot is running to advertise itself to the remote controllers

    public String getRoboboBTName();

    public void setRoboboBTName(String roboboBTName);
}
