package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.Time;
import org.ros.node.topic.Publisher;

import aruco_msgs.Marker;


/**
 * Status Topic for detected tags by the vision module.
 * <p>
 * The topic is robot/detected_object
 */
public class TagStatusTopic extends AStatusTopic {

    private static final String TOPIC = "tag";
    public static final String STATUS = "TAG";


    private Publisher<Marker> topic;


    public TagStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Marker._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Marker msg = this.topic.newMessage();

            String id = status.getValue().get("id"),
                    frame_id=status.getValue().get("frame_id"),
                    quaternion_0 = status.getValue().get("quaternion_0"),
                    quaternion_1 = status.getValue().get("quaternion_1"),
                    quaternion_2 = status.getValue().get("quaternion_2"),
                    quaternion_3 = status.getValue().get("quaternion_3"),
                    tvec_0 = status.getValue().get("tvec_0"),
                    tvec_1 = status.getValue().get("tvec_1"),
                    tvec_2 = status.getValue().get("tvec_2");

            if (id != null &&
                    quaternion_0 != null &&
                    quaternion_1 != null &&
                    quaternion_2 != null &&
                    quaternion_3 != null &&
                    tvec_0 != null &&
                    tvec_1 != null &&
                    tvec_2 != null &&
                    frame_id != null
            ) {

                msg.setId(Integer.parseInt(id));
                msg.setConfidence(1);
                msg.getPose().getPose().getPosition().setX(Double.parseDouble(tvec_0));
                msg.getPose().getPose().getPosition().setY(Double.parseDouble(tvec_1));
                msg.getPose().getPose().getPosition().setZ(Double.parseDouble(tvec_2));
                msg.getPose().getPose().getOrientation().setX(Double.parseDouble(quaternion_0));
                msg.getPose().getPose().getOrientation().setY(Double.parseDouble(quaternion_1));
                msg.getPose().getPose().getOrientation().setZ(Double.parseDouble(quaternion_2));
                msg.getPose().getPose().getOrientation().setW(Double.parseDouble(quaternion_3));
                msg.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
                msg.getHeader().setFrameId(frame_id);

                this.topic.publish(msg);

            }

        }

    }
}
