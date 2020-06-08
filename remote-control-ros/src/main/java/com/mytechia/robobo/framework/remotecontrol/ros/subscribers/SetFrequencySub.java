package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.HashMap;

import robobo_msgs.SetSensorFrequencyCommand;

class SetFrequencySub {
    private static final String NODE_NAME = "set_frequency";
    private SubNode subNode;

    public SetFrequencySub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<SetSensorFrequencyCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeAction(roboboName, NODE_NAME), SetSensorFrequencyCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<SetSensorFrequencyCommand>() {
            @Override
            public void onNewMessage(SetSensorFrequencyCommand request) {
                String freq;
                switch(request.getFrequency().getData()) { //Case 2 -> Fast, but same as default
                    case 0:
                        freq = "LOW";
                        break;
                    case 1:
                        freq = "NORMAL";
                        break;
                    case 3:
                        freq = "MAX";
                        break;
                    default:
                        freq = "FAST";
                        break;
                }

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("frequency", freq);

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("SET-SENSOR-FREQUENCY", 0, parameters);

                subNode.getRemoteControlModule().queueCommand(command);

            }
        },3);
    }
}
