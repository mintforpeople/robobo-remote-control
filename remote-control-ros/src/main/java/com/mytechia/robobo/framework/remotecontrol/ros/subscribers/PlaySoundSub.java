package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.HashMap;

import robobo_msgs.PlaySoundCommand;

class PlaySoundSub {
    private static final String NODE_NAME = "play_sound";
    private SubNode subNode;

    public PlaySoundSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<PlaySoundCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeName(roboboName, NODE_NAME), PlaySoundCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<PlaySoundCommand>() {
            @Override
            public void onNewMessage(PlaySoundCommand request) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("sound", request.getSound().getData());

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("PLAY-SOUND", 0, parameters);

                subNode.getRemoteControlModule().queueCommand(command);

            }
        },3);
    }
}
