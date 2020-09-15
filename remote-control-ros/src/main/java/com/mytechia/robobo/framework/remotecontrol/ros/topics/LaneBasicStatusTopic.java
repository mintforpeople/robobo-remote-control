package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.Time;
import org.ros.node.topic.Publisher;

import aruco_msgs.Marker;
import robobo_msgs.Lane;


/**
 * Status Topic for detected lanes by the vision module.
 * <p>
 * The topic is robot/lane
 */
public class LaneBasicStatusTopic extends AStatusTopic {

    private static final String TOPIC = "lane";
    public static final String STATUS = "LANE_BASIC";


    private Publisher<Lane> topic;


    public LaneBasicStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Lane._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Lane msg = this.topic.newMessage();

            String id = status.getValue().get("id"),
                    a1 = status.getValue().get("a1"),
                    a2 = status.getValue().get("a2"),
                    b1 = status.getValue().get("b1"),
                    b2 = status.getValue().get("b2");

            if (id != null &&
                    a1 != null &&
                    a2 != null &&
                    b1 != null &&
                    b2 != null
            ) {
                msg.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
                msg.getHeader().setFrameId(id);
                msg.setCoeffs1(new double[]{Double.parseDouble(a1), Double.parseDouble(b1)});
                msg.setCoeffs2(new double[]{Double.parseDouble(a2), Double.parseDouble(b2)});
                this.topic.publish(msg);

            }

        }

    }
}
