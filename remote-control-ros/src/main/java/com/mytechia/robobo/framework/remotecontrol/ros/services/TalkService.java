package com.mytechia.robobo.framework.remotecontrol.ros.services;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;

import robobo_msgs.MoveWheels;
import robobo_msgs.MoveWheelsRequest;
import robobo_msgs.MoveWheelsResponse;
import robobo_msgs.Talk;
import robobo_msgs.TalkRequest;
import robobo_msgs.TalkResponse;
import std_msgs.Int8;


/**
 * ROS service for the Talk commands.
 * <p>
 * It sends a TALK command to the robobo remote control module.
 */
public class TalkService {

    private CommandNode commandNode;


    public TalkService(CommandNode commandNode) {
        this.commandNode = commandNode;
    }


    public void start() {

        String roboboName = this.commandNode.getRoboboName();

        this.commandNode.getConnectedNode().newServiceServer(NodeNameUtility.createNodeAction(roboboName, "talk"), Talk._TYPE, new ServiceResponseBuilder<TalkRequest, TalkResponse>() {

            @Override
            public void build(TalkRequest request, TalkResponse response) throws ServiceException {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("text", request.getText().getData());

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command =
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("TALK", 0, parameters);

                commandNode.getRemoteControlModule().queueCommand(command);

                Int8 r = response.getError();
                r.setData((byte) 0);
                response.setError(r);

            }

        });
    }

}
