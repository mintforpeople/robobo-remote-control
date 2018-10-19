package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;


/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class StringStatusTopic extends AStatusTopic {


    private Publisher<std_msgs.String> topic;


    public StringStatusTopic(StatusNode node, String topicName, String statusName, String valueKey) {
        super(node, topicName, statusName, valueKey);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), std_msgs.String._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            std_msgs.String msg = this.topic.newMessage();

            String value = status.getValue().get(this.valueKey);

            if (value != null) {

                msg.setData(value);

                this.topic.publish(msg);

            }

        }

    }
}
