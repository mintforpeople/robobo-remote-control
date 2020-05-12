package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import android.util.Log;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;


import robobo_msgs.ResetWheelsCommand;

class ResetWheelsSub {
    private static final String NODE_NAME = "reset_wheels";
    private SubNode subNode;

    public ResetWheelsSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<ResetWheelsCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(
                NodeNameUtility.createNodeName(roboboName, NODE_NAME),
                ResetWheelsCommand._TYPE
        );
        subscriber.addMessageListener(new MessageListener<ResetWheelsCommand>() {
            @Override
            public void onNewMessage(ResetWheelsCommand request) {
                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("RESET-WHEELS", 0, null);

                subNode.getRemoteControlModule().queueCommand(command);
            }
        },3);
    }
}
