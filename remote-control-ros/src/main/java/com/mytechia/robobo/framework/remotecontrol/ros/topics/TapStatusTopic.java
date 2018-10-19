package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import robobo_msgs.Fling;
import robobo_msgs.Tap;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class TapStatusTopic extends AStatusTopic {

    private static final String TOPIC = "tap";
    public static final String STATUS = "TAP";


    private Publisher<Tap> topic;


    public TapStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Tap._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Tap msg = this.topic.newMessage();

            String x = status.getValue().get("coordx");
            String y = status.getValue().get("coordy");

            if (x!=null && y!=null) {

                msg.getX().setData(Byte.parseByte(x));
                msg.getY().setData(Byte.parseByte(y));

                this.topic.publish(msg);

            }

        }

    }
}
