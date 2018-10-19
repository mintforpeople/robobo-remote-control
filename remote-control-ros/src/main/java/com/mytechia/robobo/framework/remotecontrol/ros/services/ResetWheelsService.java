package com.mytechia.robobo.framework.remotecontrol.ros.services;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;

import robobo_msgs.MovePanTiltResponse;
import robobo_msgs.MoveWheels;
import robobo_msgs.MoveWheelsRequest;
import robobo_msgs.ResetWheels;
import robobo_msgs.ResetWheelsRequest;
import robobo_msgs.ResetWheelsResponse;
import std_msgs.Int8;


/**
 * ROS service for the Reset Wheels commands.
 *
 * It sends a RESET-WHEELS command to the robobo remote control module.
 *
 */
public class ResetWheelsService {

    private CommandNode commandNode;


    public ResetWheelsService(CommandNode commandNode) {
        this.commandNode = commandNode;
    }


    public void start() {

        String roboboName = this.commandNode.getRoboboName();

        this.commandNode.getConnectedNode().newServiceServer(NodeNameUtility.createNodeAction(roboboName, "resetWheels"), ResetWheels._TYPE, new ServiceResponseBuilder<ResetWheelsRequest, ResetWheelsResponse>() {

            @Override
            public void build(ResetWheelsRequest request, ResetWheelsResponse response) throws ServiceException {

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("RESET-WHEELS", 0, null);

                commandNode.getRemoteControlModule().queueCommand(command);

                Int8 r = response.getError();
                r.setData((byte)0);
                response.setError(r);

            }

        });
    }

}
