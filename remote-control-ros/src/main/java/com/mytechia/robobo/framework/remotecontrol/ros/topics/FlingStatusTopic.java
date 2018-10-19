package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import robobo_msgs.Fling;
import robobo_msgs.Wheels;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class FlingStatusTopic extends AStatusTopic {

    private static final String TOPIC = "fling";
    public static final String STATUS = "FLING";


    private Publisher<Fling> topic;


    public FlingStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Fling._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Fling msg = this.topic.newMessage();

            String angle = status.getValue().get("angle");
            String time = status.getValue().get("time");
            String distance = status.getValue().get("distance");

            if (angle!=null && time!=null && distance!= null) {

                msg.getAngle().setData(Short.parseShort(angle));
                msg.getTime().setData(Integer.parseInt(time));
                msg.getDistance().setData(Short.parseShort(distance));

                this.topic.publish(msg);

            }

        }

    }
}
