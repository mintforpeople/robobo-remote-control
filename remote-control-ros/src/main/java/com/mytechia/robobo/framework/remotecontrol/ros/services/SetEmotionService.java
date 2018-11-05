package com.mytechia.robobo.framework.remotecontrol.ros.services;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;

import robobo_msgs.SetEmotion;
import robobo_msgs.SetEmotionRequest;
import robobo_msgs.SetEmotionResponse;
import robobo_msgs.Talk;
import robobo_msgs.TalkRequest;
import robobo_msgs.TalkResponse;
import std_msgs.Int8;


/**
 * ROS service for changing the emotion of the robot.
 *
 * It sends a SET-EMOTION command to the robobo remote control module.
 *
 */
public class SetEmotionService {

    private CommandNode commandNode;


    public SetEmotionService(CommandNode commandNode) {
        this.commandNode = commandNode;
    }


    public void start() {

        String roboboName = this.commandNode.getRoboboName();

        this.commandNode.getConnectedNode().newServiceServer(NodeNameUtility.createNodeAction(roboboName, "setEmotion"), SetEmotion._TYPE, new ServiceResponseBuilder<SetEmotionRequest, SetEmotionResponse>() {

            @Override
            public void build(SetEmotionRequest request, SetEmotionResponse response) throws ServiceException {


                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("emotion", request.getEmotion().getData());

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("SET-EMOTION", 0, parameters);

                commandNode.getRemoteControlModule().queueCommand(command);

                Int8 r = response.getError();
                r.setData((byte)0);
                response.setError(r);

            }

        });
    }

}
