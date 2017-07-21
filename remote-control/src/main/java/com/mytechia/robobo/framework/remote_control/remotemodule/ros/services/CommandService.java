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

package com.mytechia.robobo.framework.remote_control.remotemodule.ros.services;

import android.os.Bundle;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;

import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.ros.MapperListKeyValueMap;
import com.mytechia.robobo.framework.remote_control.remotemodule.ros.RosRemoteControlModule;
import com.mytechia.util.NodeNameUtility;


import org.ros.exception.ServiceException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com_mytechia_robobo_ros_msgs.Command;
import com_mytechia_robobo_ros_msgs.CommandRequest;
import com_mytechia_robobo_ros_msgs.CommandResponse;
import com_mytechia_robobo_ros_msgs.KeyValue;


/**
 * Created by julio on 12/07/17.
 */

public class CommandService extends AbstractNodeMain {


    private static final String ROB_COMMAND="command";

    private static final String NODE_NAME_ROBOBO_COMMAND="robobo_srv_command";

    private IRemoteControlModule remoteControlModule;

    private String roboName="";

    private MapperListKeyValueMap mapperListKeyValueMap= new MapperListKeyValueMap();


    public CommandService(IRemoteControlModule remoteControlModule, String roboName) throws InternalErrorException {

        if(roboName!=null){
            this.roboName = roboName;
        }

        if(remoteControlModule==null){
            throw new InternalErrorException("The parameter remoteControlModule is required");
        }

        this.remoteControlModule= remoteControlModule;

    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);

        connectedNode.newServiceServer(NodeNameUtility.createNodeAction(roboName, ROB_COMMAND), Command._TYPE, new ServiceResponseBuilder<CommandRequest, CommandResponse>() {

            @Override
            public void build(CommandRequest commandRequest, CommandResponse commandResponse) throws ServiceException {


                List<KeyValue> commandRequestParameters = commandRequest.getParameters();

                HashMap<String, String> parameters = mapperListKeyValueMap.listKeyValueToMap(commandRequestParameters);

                String name= commandRequest.getName();

                int id= commandRequest.getId();

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command(name, id, parameters);

                remoteControlModule.queueCommand(command);

            }

        });



    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(NodeNameUtility.createNodeName(roboName, NODE_NAME_ROBOBO_COMMAND));
    }

}
