package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import geometry_msgs.Accel;
import geometry_msgs.Quaternion;
import geometry_msgs.Vector3;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class OrientationStatusTopic extends AStatusTopic {

    private static final String TOPIC = "orientation";
    public static final String STATUS = "ORIENTATION";



    private Publisher<Quaternion> topic;


    public OrientationStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Quaternion._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Quaternion msg = this.topic.newMessage();

            String x = status.getValue().get("yaw");
            String y = status.getValue().get("pitch");
            String z = status.getValue().get("roll");

            if (x!=null && y!=null && z!= null) {

                double yaw = Double.parseDouble(x);
                double pitch = Double.parseDouble(y);
                double roll = Double.parseDouble(z);

                msg = toQuaternion(msg, yaw, pitch, roll);

                this.topic.publish(msg);

            }

        }

    }


    private Quaternion toQuaternion(Quaternion q, double yaw, double pitch, double roll) {

            double cy = Math.cos(yaw * 0.5);
            double sy = Math.sin(yaw * 0.5);
            double cr = Math.cos(roll * 0.5);
            double sr = Math.sin(roll * 0.5);
            double cp = Math.cos(pitch * 0.5);
            double sp = Math.sin(pitch * 0.5);

            q.setW(cy * cr * cp + sy * sr * sp);
            q.setX(cy * sr * cp - sy * cr * sp);
            q.setY(cy * cr * sp + sy * sr * cp);
            q.setZ(sy * cr * cp - cy * sr * sp);

            return q;
    }
}
