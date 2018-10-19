package com.mytechia.robobo.framework.remotecontrol.ros.services;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;

import robobo_msgs.SetCamera;
import robobo_msgs.SetCameraRequest;
import robobo_msgs.SetCameraResponse;
import robobo_msgs.SetSensorFrequency;
import robobo_msgs.SetSensorFrequencyRequest;
import robobo_msgs.SetSensorFrequencyResponse;
import std_msgs.Int8;


/**
 * ROS service for change the update frequency of the robot sensors
 *
 * It sends a SET-SENSOR-FREQUENCY command to the robobo remote control module.
 *
 */
public class SetFrequencyService {

    private CommandNode commandNode;


    public SetFrequencyService(CommandNode commandNode) {
        this.commandNode = commandNode;
    }


    public void start() {

        String roboboName = this.commandNode.getRoboboName();

        this.commandNode.getConnectedNode().newServiceServer(NodeNameUtility.createNodeAction(roboboName, "setSensorFrequency"), SetSensorFrequency._TYPE, new ServiceResponseBuilder<SetSensorFrequencyRequest, SetSensorFrequencyResponse>() {

            @Override
            public void build(SetSensorFrequencyRequest request, SetSensorFrequencyResponse response) throws ServiceException {

                String freq = "FAST";
                switch(request.getFrequency().getData()) {
                    case 0:
                        freq = "LOW";
                        break;
                    case 1:
                        freq = "NORMAL";
                        break;
                    case 2:
                        freq = "FAST";
                        break;
                    case 3:
                        freq = "MAX";
                        break;
                    default:
                        freq = "FAST";
                        break;
                }

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("frequency", freq);

                com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("SET-SENSOR-FREQUENCY", 0, parameters);

                commandNode.getRemoteControlModule().queueCommand(command);

                Int8 r = response.getError();
                r.setData((byte)0);
                response.setError(r);

            }

        });
    }

}
