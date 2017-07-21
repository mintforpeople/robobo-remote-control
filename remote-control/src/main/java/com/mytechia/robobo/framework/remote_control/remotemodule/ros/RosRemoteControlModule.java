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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.IModule;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlProxy;
import com.mytechia.robobo.framework.remote_control.remotemodule.Response;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remote_control.remotemodule.ros.services.CommandService;
import com.mytechia.robobo.framework.remote_control.remotemodule.ros.topics.ResponseTopic;
import com.mytechia.robobo.framework.remote_control.remotemodule.ros.topics.StatusTopic;

import java.net.MalformedURLException;
import java.net.URL;

import org.ros.address.InetAddressFactory;
import org.ros.node.NodeMain;

/**
 * Created by julio on 11/07/17.
 */
public class RosRemoteControlModule implements IRemoteControlProxy, IModule {

    private static final String MODULE_INFO = "Ros RC Module";

    private static final String TAG = MODULE_INFO;

    private static final String MODULE_VERSION = "0.1.0";

    public final static String DEFAULT_MASTER_URI = "http://localhost:11311/";

    public static final String MASTER_URI="com.mytehia.ros.master.uri";

    public static final String ROBOBO_NAME="robobo.name";

    private Context context;

    private NodeExecutorServiceConnection nodeMainExecutorServiceConnection;

    private StatusTopic statusTopic;

    private String roboName;

    private IRemoteControlModule remoteControlModule;

    private CommandService commandService;

    private ResponseTopic responseTopic;


    @Override
    public void startup(RoboboManager roboboManager) throws InternalErrorException {

        Log.d(TAG, "Start Ros Remote Control Module");

        this.remoteControlModule = roboboManager.getModuleInstance(IRemoteControlModule.class);

        if (this.remoteControlModule == null) {
            throw new InternalErrorException("No found instance IRemoteControlModule.");
        }

        try {
            this.init(roboboManager);
        } catch (MalformedURLException e) {
            throw new InternalErrorException(e, "Error startup Ros Remote Control Module");
        }

        Bundle roboboOptions = roboboManager.getOptions();

        this.roboName = roboboOptions.getString(RosRemoteControlModule.ROBOBO_NAME, "");

        this.initRoboRosNodes(remoteControlModule, this.roboName);

        this.remoteControlModule.registerRemoteControlProxy(this);

    }



    private void initRoboRosNodes(IRemoteControlModule remoteControlModule, String roboName) throws InternalErrorException {

       this.statusTopic= this.startRoboRosNode(new StatusTopic(roboName));

        this.commandService= this.startRoboRosNode(new CommandService(remoteControlModule, roboName));

       this.responseTopic= this.startRoboRosNode(new ResponseTopic(roboName));

    }


    @Override
    public void shutdown() throws InternalErrorException {

        context.unbindService(nodeMainExecutorServiceConnection);

    }




    private void init(RoboboManager manager) throws MalformedURLException {


        this.context= manager.getApplicationContext();

        Intent intent = new Intent(context,  com.mytechia.robobo.framework.remote_control.remotemodule.ros.NodeMainExecutorService.class);

        intent.putExtra(NodeMainExecutorService.ROS_HOST_NAME,  InetAddressFactory.newNonLoopback().getHostAddress());

        Bundle roboboOptions = manager.getOptions();

        String masterUri = roboboOptions.getString(MASTER_URI, DEFAULT_MASTER_URI);

        intent.putExtra(NodeMainExecutorService.MASTER_URI, masterUri);

        boolean thisNodeMaster= false;

        URL url= new URL(masterUri);

        String host= url.getHost();

        if(host.equals("localhost") || host.equals("127.0.0.1")){
            thisNodeMaster=true;
        }

        intent.putExtra(NodeMainExecutorService.ROS_PUBLI_MASTER_NODE, thisNodeMaster);

        if(!thisNodeMaster) {
            intent.putExtra(NodeMainExecutorService.ROS_PORT, url.getPort());
        }

        this.context.startService(intent);

        this.nodeMainExecutorServiceConnection = new NodeExecutorServiceConnection();

        this.context.bindService(intent, nodeMainExecutorServiceConnection, Context.BIND_AUTO_CREATE);

    }


    public <T extends NodeMain> T startRoboRosNode(final T roboRosNode){

        if(nodeMainExecutorServiceConnection!=null){
            nodeMainExecutorServiceConnection.startRoboRosNode(roboRosNode);
        }

        return roboRosNode;

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
        statusTopic.publishStatusMessage(status);
    }

    @Override
    public void notifyReponse(Response response) {
        responseTopic.publishResponseMessage(response);
    }



}
