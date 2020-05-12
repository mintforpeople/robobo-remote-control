package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import android.util.Log;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.HashMap;

import robobo_msgs.MovePanTiltCommand;

class MovePanTiltSub {
    private static final String NODE_NAME = "move_pan_tilt";
    private SubNode subNode;

    public MovePanTiltSub(SubNode subNode) {
        this.subNode = subNode;
    }

    public void start() {

        String roboboName = this.subNode.getRoboboName();
        Subscriber<MovePanTiltCommand> subscriber = this.subNode.getConnectedNode().newSubscriber(NodeNameUtility.createNodeName(roboboName, NODE_NAME), MovePanTiltCommand._TYPE);
        subscriber.addMessageListener(new MessageListener<MovePanTiltCommand>() {
            @Override
            public void onNewMessage(MovePanTiltCommand request) {
                HashMap<String, String> panParams = new HashMap<>();
                panParams.put("pos", String.valueOf(request.getPanPos().getData()));
                panParams.put("speed", String.valueOf(request.getPanSpeed().getData()));
                int panId = request.getPanUnlockId().getData();
                panParams.put("blockid", String.valueOf(panId));


                Log.i("MOVE-PT", "MovePanMsg: "+panParams.get("pos")+" - "+panParams.get("speed"));

                com.mytechia.robobo.framework.remote_control.remotemodule.Command panCommand=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("MOVEPAN-BLOCKING", panId, panParams);

                HashMap<String, String> tiltParams = new HashMap<>();
                tiltParams.put("pos", String.valueOf(request.getTiltPos().getData()));
                tiltParams.put("speed", String.valueOf(request.getTiltSpeed().getData()));
                int tiltId = request.getTiltUnlockId().getData();
                tiltParams.put("blockid", String.valueOf(tiltId));

                Log.i("MOVE-PT", "MovePanMsg: "+tiltParams.get("pos")+" - "+tiltParams.get("speed"));

                com.mytechia.robobo.framework.remote_control.remotemodule.Command tiltCommand=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("MOVETILT-BLOCKING", tiltId, tiltParams);

                if (panId > 0) {
                    subNode.getRemoteControlModule().queueCommand(panCommand);
                }

                if (tiltId > 0) {
                    subNode.getRemoteControlModule().queueCommand(tiltCommand);
                }

            }
        },3);
    }
}
