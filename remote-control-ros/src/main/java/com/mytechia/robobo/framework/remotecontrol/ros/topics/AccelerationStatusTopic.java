package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import geometry_msgs.Accel;
import geometry_msgs.Vector3;
import std_msgs.Int8;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class AccelerationStatusTopic extends AStatusTopic {

    private static final String TOPIC = "accel";
    public static final String STATUS = "ACCELERATION";



    private Publisher<Accel> topic;


    public AccelerationStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Accel._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Accel msg = this.topic.newMessage();
            Vector3 linear = msg.getLinear();

            String x = status.getValue().get("xaccel");
            String y = status.getValue().get("yaccel");
            String z = status.getValue().get("zaccel");

            if (x!=null && y!=null && z!= null) {

                linear.setX(Double.parseDouble(x));
                linear.setY(Double.parseDouble(y));
                linear.setZ(Double.parseDouble(z));

                msg.setLinear(linear);

                this.topic.publish(msg);

            }

        }

    }
}
