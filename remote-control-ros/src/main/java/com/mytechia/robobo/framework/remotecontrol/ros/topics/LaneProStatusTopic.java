package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.Time;
import org.ros.node.topic.Publisher;

import robobo_msgs.Lane;


/**
 * Status Topic for detected lanes by the vision module.
 * <p>
 * The topic is robot/lane
 */
public class LaneProStatusTopic extends AStatusTopic {

    private static final String TOPIC = "lane";
    public static final String STATUS = "LANE_PRO";


    private Publisher<Lane> topic;


    public LaneProStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Lane._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {
            String [] minv_items;
            double[] minv_doubles = new double[9];

            Lane msg = this.topic.newMessage();

            String id = status.getValue().get("id"),
                    minv = status.getValue().get("minv"),
                    left_a = status.getValue().get("left_a"),
                    left_b = status.getValue().get("left_b"),
                    left_c = status.getValue().get("left_c"),
                    right_a = status.getValue().get("right_a"),
                    right_b = status.getValue().get("right_b"),
                    right_c = status.getValue().get("right_c");
            minv_items = (minv != null) ? minv.replaceAll("[\\[\\s\\]]", "").split(",") : null;

            if (id != null &&
                    minv_items != null &&
                    minv_items.length == 9 &&
                    left_a != null &&
                    left_b != null &&
                    left_c != null &&
                    right_a != null &&
                    right_b != null &&
                    right_c != null
            ) {
                for (int i = 0; i < 9; i++) {
                    minv_doubles[i]= Double.parseDouble(minv_items[i]);
                }

                msg.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
                msg.getHeader().setFrameId(id);
                msg.setMinv(minv_doubles);
                msg.setCoeffs1(new double[]{Double.parseDouble(left_a), Double.parseDouble(left_b), Double.parseDouble(left_c)});
                msg.setCoeffs2(new double[]{Double.parseDouble(right_a), Double.parseDouble(right_b), Double.parseDouble(right_c)});
                this.topic.publish(msg);

            }

        }

    }
}
