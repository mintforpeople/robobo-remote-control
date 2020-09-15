/*******************************************************************************
 *
 *   Copyright 2018 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2018 Gervasio Varela <gervasio.varela@mytechia.com>
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

package com.mytechia.robobo.framework.remotecontrol.ros;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.frequency.FrequencyMode;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlProxy;
import com.mytechia.robobo.framework.remote_control.remotemodule.Response;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.services.CommandNode;
import com.mytechia.robobo.framework.remotecontrol.ros.subscribers.SubNode;
import com.mytechia.robobo.framework.remotecontrol.ros.topics.StatusNode;

import org.ros.address.InetAddressFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * A robobo module that implements a remote control proxy for ROS (Robot Operating Systems).
 *
 * This module allows the connection of the Robobo robot to a ROS network, publishing its
 * funcionatily through ROS services and topics. Furthermore, it has the hability to provide
 * its own ROS master running on the robot.
 *
 */

public class RosRemoteControlModule implements IRemoteControlProxy, IRosRemoteControlModule {

    private static final String MODULE_INFO = "ROS RC Module";

    private static final String TAG = MODULE_INFO;

    private static final String MODULE_VERSION = "1.1.0";

    public final static String DEFAULT_MASTER_URI = "http://localhost:11311/";

    public static final String MASTER_URI = "com.mytehia.ros.master.uri";

    public static final String ROBOBO_NAME="robobo.name";

    private Context context;

    private StatusNode statusNode;

    private String roboName;

    private IRemoteControlModule remoteControlModule;

    private CommandNode commandNode;

    private NodeConfiguration nodeConfiguration;

    private AndroidNodeMainExecutor nodeMainExecutor;
    private SubNode subNode;


    @Override
    public void startup(RoboboManager roboboManager) throws InternalErrorException {

        Log.d(TAG, "Starting ROS Remote Control Module");

        this.remoteControlModule = roboboManager.getModuleInstance(IRemoteControlModule.class);

        if (this.remoteControlModule == null) {
            throw new InternalErrorException("No instance IRemoteControlModule found.");
        }


        this.context = roboboManager.getApplicationContext();

        //get the user options
        Bundle roboboBundleOptions = roboboManager.getOptions();

        String masterUri = roboboBundleOptions.getString(MASTER_URI, DEFAULT_MASTER_URI);

        String rosHostName = InetAddressFactory.newNonLoopback().getHostAddress();

        try {
            this.nodeMainExecutor = new AndroidNodeMainExecutor(context, masterUri, rosHostName);
        } catch (URISyntaxException ex) {
            throw new InternalErrorException(ex, "Error strating up ROS Remote Control Module");
        }

        this.nodeConfiguration = NodeConfiguration.newPublic(rosHostName);

        try {
            this.nodeConfiguration.setMasterUri(new URI(masterUri));

        } catch (URISyntaxException ex) {
            this.nodeMainExecutor.shutdown();
            throw new InternalErrorException(ex, "Error starting up ROS Remote Control Module");
        }


        Bundle roboboOptions = roboboManager.getOptions();

        this.roboName = roboboOptions.getString(RosRemoteControlModule.ROBOBO_NAME, "");

        this.initRoboboRosNodes(remoteControlModule, this.roboName);

        this.remoteControlModule.registerRemoteControlProxy(this);

        roboboManager.changeFrequencyModeTo(FrequencyMode.MAX);

    }


    @Override
    public void startRoboboRosNode(NodeMain node) {
        Log.d(TAG, "Starting new ROS Node: " + node.getClass().getSimpleName());

        nodeMainExecutor.execute(node, this.nodeConfiguration);
    }

    @Override
    public String getRoboboName() {
        return this.roboName;
    }


    @Override
    public void shutdown() throws InternalErrorException {

        if (nodeMainExecutor != null) {
            nodeMainExecutor.shutdown();
        }

    }


    private void initRoboboRosNodes(IRemoteControlModule remoteControlModule, String roboboName) throws InternalErrorException {

        this.statusNode = new StatusNode(roboboName);

        this.startRoboboRosNode(this.statusNode);

        this.commandNode = new CommandNode(remoteControlModule, roboboName);

        this.startRoboboRosNode(this.commandNode);

        this.subNode = new SubNode(remoteControlModule, roboboName);

        this.startRoboboRosNode(this.subNode);

    }


    @Override
    public String getModuleInfo() {
        return MODULE_INFO;
    }

    @Override
    public String getModuleVersion() {
        return MODULE_VERSION;
    }

    @Override
    public void notifyStatus(Status status) {
        statusNode.publishStatusMessage(status);
    }

    @Override
    public void notifyReponse(Response response) {
        //do nothing
    }
}
