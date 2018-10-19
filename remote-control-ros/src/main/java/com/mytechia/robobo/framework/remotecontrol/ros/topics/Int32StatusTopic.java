package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import std_msgs.Int16;
import std_msgs.Int32;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class Int32StatusTopic extends AStatusTopic {


    private Publisher<Int32> topic;


    public Int32StatusTopic(StatusNode node, String topicName, String statusName, String valueKey) {
        super(node, topicName, statusName, valueKey);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Int32._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Int32 msg = this.topic.newMessage();

            String value = status.getValue().get(this.valueKey);

            if (value != null) {

                msg.setData(Integer.parseInt(value));

                this.topic.publish(msg);

            }

        }

    }
}
