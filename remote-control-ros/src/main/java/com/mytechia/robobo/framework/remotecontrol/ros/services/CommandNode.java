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

package com.mytechia.robobo.framework.remotecontrol.ros.services;



import com.mytechia.commons.framework.exception.InternalErrorException;


import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;


import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;


/**
 * Created by julio on 12/07/17.
 */

public class CommandNode extends AbstractNodeMain {

    private static final String NODE_NAME_ROBOBO_COMMAND="robobo_commands";

    private IRemoteControlModule remoteControlModule;

    private String roboboName ="";

    private ConnectedNode connectedNode;

    private MoveWheelsService moveWheelsService;
    private MovePanTiltService movePanTiltService;
    private PlaySoundService playSoundService;
    private ResetWheelsService resetWheelsService;
    private SetCameraService setCameraService;
    private SetEmotionService setEmotionService;
    private SetFrequencyService setFrequencyService;
    private SetLedService setLedService;
    private SetModuleService setModuleService;
    private TalkService talkService;



    public CommandNode(IRemoteControlModule remoteControlModule, String roboboName) throws InternalErrorException {

        if(roboboName !=null){
            this.roboboName = roboboName;
        }

        if(remoteControlModule==null){
            throw new InternalErrorException("The parameter remoteControlModule is required");
        }

        this.remoteControlModule= remoteControlModule;

    }


    String getRoboboName() {
        return this.roboboName;
    }

    ConnectedNode getConnectedNode() {
        return this.connectedNode;
    }

    IRemoteControlModule getRemoteControlModule() {
        return this.remoteControlModule;
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);

        this.connectedNode = connectedNode;

        this.moveWheelsService = new MoveWheelsService(this);
        this.moveWheelsService.start();

        this.movePanTiltService = new MovePanTiltService(this);
        this.movePanTiltService.start();

        this.playSoundService = new PlaySoundService(this);
        this.playSoundService.start();

        this.resetWheelsService = new ResetWheelsService(this);
        this.resetWheelsService.start();

        this.setCameraService = new SetCameraService(this);
        this.setCameraService.start();

        this.setEmotionService = new SetEmotionService(this);
        this.setEmotionService.start();

        this.setFrequencyService = new SetFrequencyService(this);
        this.setFrequencyService.start();

        this.setLedService = new SetLedService(this);
        this.setLedService.start();

        this.setModuleService = new SetModuleService(this);
        this.setModuleService.start();

        this.talkService = new TalkService(this);
        this.talkService.start();

    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(NodeNameUtility.createNodeName(roboboName, NODE_NAME_ROBOBO_COMMAND));
    }

}
