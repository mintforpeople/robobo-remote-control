package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.HashMap;

import robobo_msgs.SetCameraCommand;

class SetCameraSub {
    private static final String NODE_NAME = "set_camera";
    private SubNode subNode;

    public SetCameraSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<SetCameraCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeName(roboboName, NODE_NAME), SetCameraCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<SetCameraCommand>() {
            @Override
            public void onNewMessage(SetCameraCommand request) {
                String camera = "front";
                if (request.getCamera().getData() == 1) {
                    camera = "back";
                }

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("camera", camera);

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("SET-CAMERA", 0, parameters);

                subNode.getRemoteControlModule().queueCommand(command);

            }
        },3);
    }
}
