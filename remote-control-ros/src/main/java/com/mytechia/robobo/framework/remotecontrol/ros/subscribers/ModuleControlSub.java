package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import android.util.Log;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import robobo_msgs.ModuleControlCommand;

class ModuleControlSub {
    private static final String NODE_NAME = "module_control";
    static final ArrayList<String> MODULES = new ArrayList<>(Arrays.asList(
            "STREAM",
            "COLOR-DETECTION",
            "COLOR-MEASUREMENT",
            "FACE-DETECTION",
            "LANE",
            "LINE",
            "LINE-STATS",
            "OBJECT-RECOGNITION",
            "QR-TRACKING",
            "TAG"
    ));
    private SubNode subNode;

    public ModuleControlSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<ModuleControlCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeAction(roboboName, NODE_NAME), ModuleControlCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<ModuleControlCommand>() {
            @Override
            public void onNewMessage(ModuleControlCommand msg) {
                if (MODULES.contains(msg.getModuleName())) {
                    int id = 0;
                    HashMap<String, String> params = new HashMap<>();
                    String commandName = (msg.getOn()?"START-":"STOP-")+msg.getModuleName();
                    com.mytechia.robobo.framework.remote_control.remotemodule.Command command =
                            new com.mytechia.robobo.framework.remote_control.remotemodule.Command(commandName, id, params);
                    subNode.getRemoteControlModule().queueCommand(command);
                }
            }
        },10);
    }
}
