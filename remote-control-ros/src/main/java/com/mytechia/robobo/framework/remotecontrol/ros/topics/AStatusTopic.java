package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.node.topic.Publisher;

import std_msgs.Int8;

/**
 * Status Topic for the robot base battery level.
 *
 * The topic is robot/battery/base
 *
 */
public abstract class AStatusTopic implements IStatusTopic {

    private final String statusName;
    private final String topicName;
    protected final String valueKey;

    protected StatusNode node;


    public AStatusTopic(StatusNode node, String topicName, String statusName, String valueKey) {
        this.node = node;
        this.statusName = statusName;
        this.topicName = topicName;
        this.valueKey = valueKey;
    }


    public abstract void start();


    @Override
    public String getSupportedStatus() {
        return this.statusName;
    }

    @Override
    public String getTopicName() {
        return this.topicName;
    }

    @Override
    public abstract void publishStatus(Status status);


}
