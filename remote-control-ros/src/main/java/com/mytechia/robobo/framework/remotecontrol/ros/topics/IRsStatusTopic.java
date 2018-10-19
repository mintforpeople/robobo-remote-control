package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import robobo_msgs.IRs;
import robobo_msgs.Led;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class IRsStatusTopic extends AStatusTopic {

    private static final String TOPIC = "irs";
    public static final String STATUS = "IRS";


    private Publisher<IRs> topic;


    public IRsStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), IRs._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            IRs msg = this.topic.newMessage();

            String BackC = status.getValue().get("Back-C");
            String BackR = status.getValue().get("Back-R");
            String BackL = status.getValue().get("Back-L");
            String FrontC = status.getValue().get("Front-C");
            String FrontR = status.getValue().get("Front-R");
            String FrontRR = status.getValue().get("Front-RR");
            String FrontL = status.getValue().get("Front-L");
            String FrontLL = status.getValue().get("Front-LL");

            if (BackC!=null && BackR!=null && BackL!= null && FrontC!= null
                    && FrontR!= null && FrontRR!= null && FrontL!= null && FrontLL!= null) {

                msg.getBackC().setRange(Float.parseFloat(BackC));
                msg.getBackC().setMinRange(0);
                msg.getBackC().setMaxRange(1.0f);
                msg.getBackC().setRadiationType((byte)1);
                msg.getBackC().setFieldOfView(0);

                msg.getBackR().setRange(Float.parseFloat(BackR));
                msg.getBackR().setMinRange(0);
                msg.getBackR().setMaxRange(1.0f);
                msg.getBackR().setRadiationType((byte)1);
                msg.getBackR().setFieldOfView(0);

                msg.getBackL().setRange(Float.parseFloat(BackL));
                msg.getBackL().setMinRange(0);
                msg.getBackL().setMaxRange(1.0f);
                msg.getBackL().setRadiationType((byte)1);
                msg.getBackL().setFieldOfView(0);

                msg.getFrontC().setRange(Float.parseFloat(FrontC));
                msg.getFrontC().setMinRange(0);
                msg.getFrontC().setMaxRange(1.0f);
                msg.getFrontC().setRadiationType((byte)1);
                msg.getFrontC().setFieldOfView(0);

                msg.getFrontR().setRange(Float.parseFloat(FrontR));
                msg.getFrontR().setMinRange(0);
                msg.getFrontR().setMaxRange(1.0f);
                msg.getFrontR().setRadiationType((byte)1);
                msg.getFrontR().setFieldOfView(0);

                msg.getFrontRR().setRange(Float.parseFloat(FrontRR));
                msg.getFrontRR().setMinRange(0);
                msg.getFrontRR().setMaxRange(1.0f);
                msg.getFrontRR().setRadiationType((byte)1);
                msg.getFrontRR().setFieldOfView(0);

                msg.getFrontL().setRange(Float.parseFloat(FrontL));
                msg.getFrontL().setMinRange(0);
                msg.getFrontL().setMaxRange(1.0f);
                msg.getFrontL().setRadiationType((byte)1);
                msg.getFrontL().setFieldOfView(0);

                msg.getFrontLL().setRange(Float.parseFloat(FrontLL));
                msg.getFrontLL().setMinRange(0);
                msg.getFrontLL().setMaxRange(1.0f);
                msg.getFrontLL().setRadiationType((byte)1);
                msg.getFrontLL().setFieldOfView(0);

                this.topic.publish(msg);

            }

        }

    }
}
