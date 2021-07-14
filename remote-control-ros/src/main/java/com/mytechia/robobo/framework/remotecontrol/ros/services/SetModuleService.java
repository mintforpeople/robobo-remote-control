package com.mytechia.robobo.framework.remotecontrol.ros.services;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import java.util.HashMap;

import robobo_msgs.SetLed;
import robobo_msgs.SetLedRequest;
import robobo_msgs.SetLedResponse;
import robobo_msgs.SetModule;
import robobo_msgs.SetModuleRequest;
import robobo_msgs.SetModuleResponse;
import std_msgs.Int8;


/**
 * ROS service for changing the color of the robot leds
 *
 * It sends a SET-LEDCOLOR command to the robobo remote control module.
 *
 */
public class SetModuleService {

    private CommandNode commandNode;


    public SetModuleService(CommandNode commandNode) {
        this.commandNode = commandNode;
    }


    public void start() {

        String roboboName = this.commandNode.getRoboboName();

        this.commandNode.getConnectedNode().newServiceServer(NodeNameUtility.createNodeAction(roboboName, "setModule"), SetModule._TYPE, new ServiceResponseBuilder<SetModuleRequest, SetModuleResponse>() {

            @Override
            public void build(SetModuleRequest request, SetModuleResponse response) throws ServiceException {
                //HashMap<String, String> parameters = new HashMap<>();
                //parameters.put("module_name", request.getModuleName().getData());
                //parameters.put("module_state", request.getModuleState().getData());
//
                //com.mytechia.robobo.framework.remote_control.remotemodule.Command command=
                //        new com.mytechia.robobo.framework.remote_control.remotemodule.Command("SET-LEDCOLOR", 0, parameters);
//
                //commandNode.getRemoteControlModule().queueCommand(command);
//
                //Int8 r = response.getError();
                //r.setData((byte)0);
                //response.setError(r);
            }



        });
    }

}
