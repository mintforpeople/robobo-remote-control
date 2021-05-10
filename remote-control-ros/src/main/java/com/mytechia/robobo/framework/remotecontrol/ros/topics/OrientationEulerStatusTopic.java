package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import geometry_msgs.Quaternion;
import geometry_msgs.Vector3;
import robobo_msgs.OrientationEuler;
import std_msgs.Float64;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class OrientationEulerStatusTopic extends AStatusTopic {

    private static final String TOPIC = "orientation_euler";
    public static final String STATUS = "ORIENTATION";



    private Publisher<OrientationEuler> topic;


    public OrientationEulerStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), OrientationEuler._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            OrientationEuler msg = this.topic.newMessage();

            String yaws = status.getValue().get("yaw");
            String pitchs = status.getValue().get("pitch");
            String rolls = status.getValue().get("roll");

            if (yaws!=null && pitchs!=null && rolls!= null) {

                double yaw = Double.parseDouble(yaws);
                double pitch = Double.parseDouble(pitchs);
                double roll = Double.parseDouble(rolls);
                Float64 test;

                msg.getYaw().setData(yaw);
                msg.getPitch().setData(pitch);
                msg.getRoll().setData(roll);

                msg.getVector().setX(roll);
                msg.getVector().setY(pitch);
                msg.getVector().setZ(yaw);


                this.topic.publish(msg);

            }

        }

    }



}
