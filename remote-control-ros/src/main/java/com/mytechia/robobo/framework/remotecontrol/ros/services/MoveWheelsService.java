package com.mytechia.robobo.framework.remotecontrol.ros.services;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;

import robobo_msgs.MovePanTiltResponse;
import robobo_msgs.MoveWheels;
import robobo_msgs.MoveWheelsRequest;
import robobo_msgs.MoveWheelsResponse;
import std_msgs.Int8;


/**
 * ROS service for the Move Wheels commands.
 *
 * It sends a MOVE-BLOCKING command to the robobo remote control module.
 *
 */
public class MoveWheelsService {

    private CommandNode commandNode;


    public MoveWheelsService(CommandNode commandNode) {
        this.commandNode = commandNode;
    }


    public void start() {

        String roboboName = this.commandNode.getRoboboName();

        this.commandNode.getConnectedNode().newServiceServer(NodeNameUtility.createNodeAction(roboboName, "moveWheels"), MoveWheels._TYPE, new ServiceResponseBuilder<MoveWheelsRequest, MoveWheelsResponse>() {

            @Override
            public void build(MoveWheelsRequest request, MoveWheelsResponse response) throws ServiceException {


                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("lspeed", String.valueOf(request.getLspeed().getData()));
                parameters.put("rspeed", String.valueOf(request.getRspeed().getData()));
                parameters.put("time", String.valueOf(request.getTime().getData()));
                int id = request.getUnlockid().getData();

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("MOVE-BLOCKING", id, parameters);

                commandNode.getRemoteControlModule().queueCommand(command);

                Int8 r = response.getError();
                r.setData((byte)0);
                response.setError(r);

            }

        });
    }

}
