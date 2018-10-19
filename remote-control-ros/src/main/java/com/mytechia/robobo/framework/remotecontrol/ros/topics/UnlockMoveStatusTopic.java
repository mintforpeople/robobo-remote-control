package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import std_msgs.Int16;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class UnlockMoveStatusTopic extends AStatusTopic {

    private static final String TOPIC = "unlock/move";

    private static final String UNLOCK = "UNLOCK";
    private static final String UNLOCK_MOVE = "UNLOCK-MOVE";
    private static final String UNLOCK_PAN ="UNLOCK-PAN";
    private static final String UNLOCK_TILT ="UNLOCK-TILT";


    private Publisher<Int16> topic;


    public UnlockMoveStatusTopic(StatusNode node) {
        super(node, TOPIC, null, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Int16._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().startsWith(UNLOCK)) {

            Int16 msg = this.topic.newMessage();

            String value = status.getValue().get("blockid");

            if (value != null) {

                msg.setData(Short.parseShort(value));

                this.topic.publish(msg);

            }

        }

    }
}
