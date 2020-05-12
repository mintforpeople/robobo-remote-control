package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import android.util.Log;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.HashMap;

import robobo_msgs.MoveWheelsCommand;

class MoveWheelsSub {
    private static final String NODE_NAME = "move_wheels";
    private SubNode subNode;

    public MoveWheelsSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<MoveWheelsCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeName(roboboName, NODE_NAME), MoveWheelsCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<MoveWheelsCommand>() {
            @Override
            public void onNewMessage(MoveWheelsCommand request) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("lspeed", String.valueOf(request.getLspeed().getData()));
                parameters.put("rspeed", String.valueOf(request.getRspeed().getData()));
                int time = request.getTime().getData();
                parameters.put("time", String.valueOf(time/1000.0));
                int id = request.getUnlockid().getData();
                parameters.put("blockid", String.valueOf(id));

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("MOVE-BLOCKING", id, parameters);

                subNode.getRemoteControlModule().queueCommand(command);

            }
        },3);
    }
}
