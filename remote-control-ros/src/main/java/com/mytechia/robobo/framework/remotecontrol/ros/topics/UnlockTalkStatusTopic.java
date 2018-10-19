package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import std_msgs.Empty;
import std_msgs.Int16;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class UnlockTalkStatusTopic extends AStatusTopic {

    private static final String TOPIC = "unlock/talk";

    public static final String STATUS_UNLOCK_TALK = "UNLOCK-TALK";


    private Publisher<Empty> topic;


    public UnlockTalkStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS_UNLOCK_TALK, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Empty._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(getSupportedStatus())) {

            Empty msg = this.topic.newMessage();

            this.topic.publish(msg);

        }

    }
}
