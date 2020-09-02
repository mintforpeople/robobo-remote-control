package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import geometry_msgs.Accel;
import geometry_msgs.Vector3;
import robobo_msgs.Wheels;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class WheelsStatusTopic extends AStatusTopic {

    private static final String TOPIC = "wheels";
    public static final String STATUS = "WHEELS";


    private Publisher<Wheels> topic;


    public WheelsStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Wheels._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Wheels msg = this.topic.newMessage();

            String wheelPosR = status.getValue().get("wheelPosR");
            String wheelPosL = status.getValue().get("wheelPosL");
            String wheelSpeedR = status.getValue().get("wheelSpeedR");
            String wheelSpeedL = status.getValue().get("wheelSpeedL");

            if (wheelPosR!=null && wheelPosL!=null && wheelSpeedR!= null && wheelSpeedL!= null) {

                msg.getWheelPosR().setData(Long.parseLong(wheelPosR));
                msg.getWheelPosL().setData(Long.parseLong(wheelPosL));
                msg.getWheelSpeedR().setData(Long.parseLong(wheelSpeedR));
                msg.getWheelSpeedL().setData(Long.parseLong(wheelSpeedL));

                this.topic.publish(msg);

            }

        }

    }
}
