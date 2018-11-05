package com.mytechia.robobo.framework.remotecontrol.ros.services;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;

import robobo_msgs.MoveWheels;
import robobo_msgs.MoveWheelsRequest;
import robobo_msgs.MoveWheelsResponse;
import robobo_msgs.SetCamera;
import robobo_msgs.SetCameraRequest;
import robobo_msgs.SetCameraResponse;
import std_msgs.Int8;


/**
 * ROS service changing the active camera of the robot
 *
 * It sends a SET-CAMERA command to the robobo remote control module.
 *
 */
public class SetCameraService {

    private CommandNode commandNode;


    public SetCameraService(CommandNode commandNode) {
        this.commandNode = commandNode;
    }


    public void start() {

        String roboboName = this.commandNode.getRoboboName();

        this.commandNode.getConnectedNode().newServiceServer(NodeNameUtility.createNodeAction(roboboName, "setCamera"), SetCamera._TYPE, new ServiceResponseBuilder<SetCameraRequest, SetCameraResponse>() {

            @Override
            public void build(SetCameraRequest request, SetCameraResponse response) throws ServiceException {

                String camera = "front";
                if (request.getCamera().getData() == 1) {
                    camera = "back";
                }

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("camera", camera);

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("SET-CAMERA", 0, parameters);

                commandNode.getRemoteControlModule().queueCommand(command);

                Int8 r = response.getError();
                r.setData((byte)0);
                response.setError(r);

            }

        });
    }

}
