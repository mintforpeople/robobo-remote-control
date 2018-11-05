package com.mytechia.robobo.framework.remotecontrol.ros.services;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;

import robobo_msgs.MoveWheels;
import robobo_msgs.MoveWheelsRequest;
import robobo_msgs.MoveWheelsResponse;
import robobo_msgs.PlaySound;
import robobo_msgs.PlaySoundRequest;
import robobo_msgs.PlaySoundResponse;
import std_msgs.Int8;


/**
 * ROS service for the Play Sound commands.
 *
 * It sends a PLAY-SOUND command to the robobo remote control module.
 *
 */
public class PlaySoundService {

    private CommandNode commandNode;


    public PlaySoundService(CommandNode commandNode) {
        this.commandNode = commandNode;
    }


    public void start() {

        String roboboName = this.commandNode.getRoboboName();

        this.commandNode.getConnectedNode().newServiceServer(NodeNameUtility.createNodeAction(roboboName, "playSound"), PlaySound._TYPE, new ServiceResponseBuilder<PlaySoundRequest, PlaySoundResponse>() {

            @Override
            public void build(PlaySoundRequest request, PlaySoundResponse response) throws ServiceException {


                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("sound", request.getSound().getData());

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("PLAY-SOUND", 0, parameters);

                commandNode.getRemoteControlModule().queueCommand(command);

                Int8 r = response.getError();
                r.setData((byte)0);
                response.setError(r);

            }

        });
    }

}
