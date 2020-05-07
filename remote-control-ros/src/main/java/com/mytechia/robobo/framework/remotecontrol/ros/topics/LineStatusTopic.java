package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.Time;
import org.ros.node.topic.Publisher;

import opencv_apps.Line;
import opencv_apps.LineArrayStamped;


/**
 * Status Topic for detected lines by the vision module.
 * <p>
 * The topic is robot/line
 */
public class LineStatusTopic extends AStatusTopic {

    private static final String TOPIC = "line";
    public static final String STATUS = "LINE";


    private Publisher<LineArrayStamped> topic;


    public LineStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), LineArrayStamped._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {
            String [] mat_items;

            LineArrayStamped msg = this.topic.newMessage();

            String id = status.getValue().get("id"),
                    mat = status.getValue().get("mat");
            mat_items = (mat != null) ? mat.replaceAll("[\\[\\s\\]]", "").split(",") : null;

            if (id != null &&
                    mat != null &&
                    mat_items.length % 4 == 0

            ) {
                for (int i = 0; i < mat_items.length/4; i++) {
                    msg.getLines().add(node.getConnectedNode().getTopicMessageFactory().<Line>newFromType(Line._TYPE));
                    msg.getLines().get(i).getPt1().setX(Double.parseDouble(mat_items[i]));
                    msg.getLines().get(i).getPt1().setY(Double.parseDouble(mat_items[i+1]));
                    msg.getLines().get(i).getPt2().setX(Double.parseDouble(mat_items[i+2]));
                    msg.getLines().get(i).getPt2().setY(Double.parseDouble(mat_items[i+3]));
                }

                msg.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
                msg.getHeader().setFrameId(id);

                this.topic.publish(msg);

            }

        }

    }
}
