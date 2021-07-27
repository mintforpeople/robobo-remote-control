package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.HashMap;

import robobo_msgs.SetBlobCommand;
import robobo_msgs.SetCameraCommand;

class SetBlobSub {
    private static final String NODE_NAME = "set_blob";
    private SubNode subNode;

    public SetBlobSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<SetBlobCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeAction(roboboName, NODE_NAME), SetBlobCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<SetBlobCommand>() {
            @Override
            public void onNewMessage(SetBlobCommand request) {

                boolean red = request.getRed();
                boolean blue = request.getBlue();
                boolean green = request.getGreen();
                boolean custom = request.getCustom();
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("red", Boolean.toString(red));
                parameters.put("green", Boolean.toString(green));
                parameters.put("blue", Boolean.toString(blue));
                parameters.put("custom", Boolean.toString(custom));

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("CONFIGURE-BLOBTRACKING", 0, parameters);

                subNode.getRemoteControlModule().queueCommand(command);
            }


        },3);
    }
}
