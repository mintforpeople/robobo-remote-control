package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.HashMap;

import robobo_msgs.SetEmotionCommand;

class SetEmotionSub {
    private static final String NODE_NAME = "set_emotion";
    private SubNode subNode;

    public SetEmotionSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<SetEmotionCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeAction(roboboName, NODE_NAME), SetEmotionCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<SetEmotionCommand>() {
            @Override
            public void onNewMessage(SetEmotionCommand request) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("emotion", request.getEmotion().getData());

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("SET-EMOTION", 0, parameters);

                subNode.getRemoteControlModule().queueCommand(command);

            }
        },3);
    }
}
