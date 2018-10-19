package com.mytechia.robobo.framework.remotecontrol.ros.services;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;

import robobo_msgs.MovePanTilt;
import robobo_msgs.MovePanTiltRequest;
import robobo_msgs.MovePanTiltResponse;
import robobo_msgs.MoveWheels;
import robobo_msgs.MoveWheelsRequest;
import std_msgs.Int8;


/**
 * ROS service for the Move Pan and Tilt command.
 *
 * It sends a MOVEPAN-BLOCKING and/or MOVETILT-BLOCKING commands to the robobo remote control module.
 *
 */
public class MovePanTiltService {

    private CommandNode commandNode;


    public MovePanTiltService(CommandNode commandNode) {
        this.commandNode = commandNode;
    }


    public void start() {

        String roboboName = this.commandNode.getRoboboName();

        this.commandNode.getConnectedNode().newServiceServer(NodeNameUtility.createNodeAction(roboboName, "movePanTilt"), MovePanTilt._TYPE, new ServiceResponseBuilder<MovePanTiltRequest, MovePanTiltResponse>() {

            @Override
            public void build(MovePanTiltRequest request, MovePanTiltResponse response) throws ServiceException {


                HashMap<String, String> panParams = new HashMap<>();
                panParams.put("pos", String.valueOf(request.getPanPos().getData()));
                panParams.put("speed", String.valueOf(request.getPanSpeed().getData()));
                int panId = request.getPanUnlockId().getData();
                panParams.put("blockid", String.valueOf(panId));

                com.mytechia.robobo.framework.remote_control.remotemodule.Command panCommand=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("MOVEPAN-BLOCKING", panId, panParams);

                HashMap<String, String> tiltParams = new HashMap<>();
                tiltParams.put("pos", String.valueOf(request.getTiltPos().getData()));
                tiltParams.put("speed", String.valueOf(request.getTiltSpeed().getData()));
                int tiltId = request.getTiltUnlockId().getData();
                tiltParams.put("blockid", String.valueOf(tiltId));

                com.mytechia.robobo.framework.remote_control.remotemodule.Command tiltCommand=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("MOVETILT-BLOCKING", tiltId, tiltParams);

                if (panId >= 0) {
                    commandNode.getRemoteControlModule().queueCommand(panCommand);
                }

                if (tiltId >= 0) {
                    commandNode.getRemoteControlModule().queueCommand(tiltCommand);
                }

                Int8 r = response.getError();
                r.setData((byte)0);
                response.setError(r);

            }

        });
    }

}
