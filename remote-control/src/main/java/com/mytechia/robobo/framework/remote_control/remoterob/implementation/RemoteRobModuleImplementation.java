package com.mytechia.robobo.framework.remote_control.remoterob.implementation;

import com.mytechia.commons.framework.exception.InternalErrorException;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.remotemodule.Command;
import com.mytechia.robobo.framework.remote_control.remotemodule.ICommandExecutor;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remote_control.remoterob.IRemoteRobModule;
import com.mytechia.robobo.rob.BatteryStatus;
import com.mytechia.robobo.rob.FallStatus;
import com.mytechia.robobo.rob.GapStatus;
import com.mytechia.robobo.rob.IRSensorStatus;
import com.mytechia.robobo.rob.IRob;

import com.mytechia.robobo.rob.IRobInterfaceModule;
import com.mytechia.robobo.rob.IRobStatusListener;
import com.mytechia.robobo.rob.MotorStatus;
import com.mytechia.robobo.rob.MoveMTMode;
import com.mytechia.robobo.rob.WallConnectionStatus;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.util.Color;

import java.util.Collection;
import java.util.HashMap;


/*******************************************************************************
 * Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright 2016 Luis Llamas <luis.llamas@mytechia.com>
 * <p>
 * This file is part of Robobo Remote Control Module.
 * <p>
 * Robobo Remote Control Module is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Robobo Remote Control Module is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with Robobo Remote Control Module.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
public class RemoteRobModuleImplementation implements IRemoteRobModule {

    private IRemoteControlModule rcmodule;
    private IRob irob;
    private IRobMovementModule movementModule;

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        rcmodule = manager.getModuleInstance(IRemoteControlModule.class);
        movementModule = manager.getModuleInstance(IRobMovementModule.class);
        irob = manager.getModuleInstance(IRobInterfaceModule.class).getRobInterface();
        irob.setOperationMode((byte)1);
        irob.addRobStatusListener(new IRobStatusListener() {
            @Override
            public void statusMotorsMT(MotorStatus left, MotorStatus right) {

            }

            @Override
            public void statusMotorPan(MotorStatus status) {

            }

            @Override
            public void statusMotorTilt(MotorStatus status) {

            }

            @Override
            public void statusGaps(Collection<GapStatus> gaps) {

            }

            @Override
            public void statusFalls(Collection<FallStatus> fall) {

            }

            @Override
            public void statusIRSensorStatus(Collection<IRSensorStatus> irSensorStatus) {
                Status s  = new Status("IRSTATUS");
                for (IRSensorStatus status : irSensorStatus){
                    s.putContents(status.getId().toString(),String.valueOf(status.getDistance()));
                }
                rcmodule.postStatus(s);
            }

            @Override
            public void statusBattery(BatteryStatus battery) {

            }

            @Override
            public void statusWallConnectionStatus(WallConnectionStatus wallConnectionStatus) {

            }

            @Override
            public void robCommunicationError(InternalErrorException ex) {

            }
        });

        rcmodule.registerCommand("MOVEBYDEGREES", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                HashMap<String,String> par = c.getParameters();
                String wheel = par.get("wheel");
                int degrees = Integer.parseInt(par.get("degrees"));
                int speed = Integer.parseInt(par.get("speed"));

                if (wheel.equals("right")){

                }else if (wheel.equals("left")){

                }else if (wheel.equals("both")){
                    if (speed>0){
                        try {
                            movementModule.moveForwardsAngle((short)speed,degrees);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            movementModule.moveBackwardsAngle((short)(speed*(-1)),degrees);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        rcmodule.registerCommand("MOVEBYTIME", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                HashMap<String,String> par = c.getParameters();
                String wheel = par.get("wheel");
                int time = Integer.parseInt(par.get("time"));
                int speed = Integer.parseInt(par.get("speed"));

                if (wheel.equals("right")){
                    if (speed>0){
                        try {
                            movementModule.turnRightBackwardsTime((short)speed,time);

                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            movementModule.turnRightTime((short)(speed*(-1)),time);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }

                }else if (wheel.equals("left")){
                    if (speed>0){
                        try {
                            movementModule.turnLeftBackwardsTime((short)speed,time);

                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            movementModule.turnLeftTime((short)(speed*(-1)),time);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }

                }else if (wheel.equals("both")){
                    if (speed>0){
                        try {
                            movementModule.moveForwardsTime((short)speed,time);

                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            movementModule.moveBackwardsAngle((short)(speed*(-1)),time);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        rcmodule.registerCommand("TURNINPLACE", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                HashMap<String,String> par = c.getParameters();
                int degrees = Integer.parseInt(par.get("degrees"));
                if (degrees>0){
                    try {
                        irob.moveMT(MoveMTMode.FORWARD_REVERSE,(short)50,degrees,(short)50,degrees);

                    } catch (InternalErrorException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        irob.moveMT(MoveMTMode.REVERSE_FORWARD,(short)50,degrees*(-1),(short)50,degrees*(-1));
                    } catch (InternalErrorException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        rcmodule.registerCommand("MOVETWOWHEELS", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                HashMap<String,String> par = c.getParameters();
                int time = Integer.parseInt(par.get("time"));
                int lspeed = Integer.parseInt(par.get("lspeed"));
                int rspeed = Integer.parseInt(par.get("rspeed"));

                if (lspeed>0){
                    if(rspeed>0){
                        //FF
                        try {
                            irob.moveMT(MoveMTMode.FORWARD_FORWARD,(short)lspeed,(short)rspeed,time);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }else {
                        //FR
                        try {
                            irob.moveMT(MoveMTMode.FORWARD_REVERSE,(short)lspeed,(short)(rspeed*(-1)),time);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    if(rspeed>0){
                        //RF
                        try {
                            irob.moveMT(MoveMTMode.REVERSE_FORWARD,(short)(lspeed*(-1)),(short)rspeed,time);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }else {
                        //RR
                        try {
                            irob.moveMT(MoveMTMode.REVERSE_REVERSE,(short)(lspeed*(-1)),(short)(rspeed*(-1)),time);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        rcmodule.registerCommand("MOVEPAN", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                HashMap<String,String> par = c.getParameters();
                int pos = Integer.parseInt(par.get("pos"));
                int speed = Integer.parseInt(par.get("speed"));

                try {
                    irob.movePan((short)speed, pos);
                } catch (InternalErrorException e) {
                    e.printStackTrace();
                }

            }
        });

        rcmodule.registerCommand("MOVETILT", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                HashMap<String,String> par = c.getParameters();
                int pos = Integer.parseInt(par.get("pos"));
                int speed = Integer.parseInt(par.get("speed"));

                try {
                    irob.moveTilt((short)speed, pos);
                } catch (InternalErrorException e) {
                    e.printStackTrace();
                }
            }
        });

        rcmodule.registerCommand("LEDCOLOR", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                HashMap<String,String> par = c.getParameters();
                String led =par.get("led");
                int ledint = 0;
                Color color = new Color(0,0,0);
                boolean all=false;
                if (led.equals("all")){
                    all = true;
                }else {
                    ledint = Integer.parseInt(led);
                }

                String colorST = par.get("color");

                switch (colorST){
                    case "white":
                           color = new Color(255,255,255);
                        break;
                    case "red":
                        color = new Color(255,0,0);
                        break;
                    case "blue":
                        color = new Color(0,0,255);
                        break;
                    case "cyan":
                        color = new Color(0,255,255);
                        break;
                    case "magenta":
                        color = new Color(255,0,255);
                        break;
                    case "yellow":
                        color = new Color(255,255,0);
                        break;
                    case "green":
                        color = new Color(0,255,0);
                        break;
                    case "orange":
                        color = new Color(255,165,0);
                        break;
                    case "on":
                        color = new Color(255,255,255);
                        break;
                    case "off":
                        color = new Color(0,0,0);
                        break;
                }
                if (all){
                    for (int i = 0; i<10; i++) {
                        try {
                            irob.setLEDColor(i,color);
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    try {
                        irob.setLEDColor(ledint,color);
                    } catch (InternalErrorException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return "Remote Rob Module";
    }

    @Override
    public String getModuleVersion() {
        return "v0.1";
    }
}
