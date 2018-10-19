package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import robobo_msgs.Fling;
import robobo_msgs.Led;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class LedStatusTopic extends AStatusTopic {

    private static final String TOPIC = "leds";
    public static final String STATUS = "LED";

    private static float MAX_VALUE = 3000;


    private Publisher<Led> topic;


    public LedStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Led._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Led msg = this.topic.newMessage();

            String id = status.getValue().get("id");
            String R = status.getValue().get("R");
            String G = status.getValue().get("G");
            String B = status.getValue().get("B");

            if (id!=null && R!=null && G!= null && B!= null) {

                msg.getId().setData(id);
                msg.getColor().setA(0);
                msg.getColor().setR(Float.parseFloat(R) / MAX_VALUE);
                msg.getColor().setG(Float.parseFloat(G) / MAX_VALUE);
                msg.getColor().setB(Float.parseFloat(B) / MAX_VALUE);

                this.topic.publish(msg);

            }

        }

    }
}
