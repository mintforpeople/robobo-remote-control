package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import android.util.Log;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import std_msgs.Int16;
import std_msgs.Int8;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public class Int16StatusTopic extends AStatusTopic {


    private Publisher<Int16> topic;


    public Int16StatusTopic(StatusNode node, String topicName, String statusName, String valueKey) {
        super(node, topicName, statusName, valueKey);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Int16._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {

            Int16 msg = this.topic.newMessage();

            String value = status.getValue().get(this.valueKey);

            if (value != null) {
                try {
                    msg.setData(Short.parseShort(value));

                    this.topic.publish(msg);
                    Log.d("INT16TOPIC","Received " +value);

                }catch (NumberFormatException e){
                    Log.e("INT16TOPIC","Bad number format, expected int16 received " +value);
                }

            }

        }

    }
}
