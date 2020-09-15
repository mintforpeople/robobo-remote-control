package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.HashMap;

import robobo_msgs.TalkCommand;

class TalkSub {
    private static final String NODE_NAME = "talk";
    private SubNode subNode;

    public TalkSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<TalkCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeAction(roboboName, NODE_NAME), TalkCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<TalkCommand>() {
            @Override
            public void onNewMessage(TalkCommand request) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("text", request.getText().getData());

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command =
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("TALK", 0, parameters);

                subNode.getRemoteControlModule().queueCommand(command);

            }
        }, 3);
    }
}
