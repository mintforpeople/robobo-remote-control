package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.HashMap;

import robobo_msgs.SetLedCommand;

class SetLedSub {
    private static final String NODE_NAME = "set_led";
    private SubNode subNode;

    public SetLedSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<SetLedCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeAction(roboboName, NODE_NAME), SetLedCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<SetLedCommand>() {
            @Override
            public void onNewMessage(SetLedCommand request) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("led", request.getId().getData());
                parameters.put("color", request.getColor().getData());

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("SET-LEDCOLOR", 0, parameters);

                subNode.getRemoteControlModule().queueCommand(command);

            }
        },3);
    }
}
