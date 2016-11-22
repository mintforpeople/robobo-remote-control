package com.mytechia.robobo.framework.remote_control.remoterob.implementation;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.Command;
import com.mytechia.robobo.framework.remote_control.ICommandExecutor;
import com.mytechia.robobo.framework.remote_control.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.rob.BatteryStatus;
import com.mytechia.robobo.rob.FallStatus;
import com.mytechia.robobo.rob.GapStatus;
import com.mytechia.robobo.rob.IRSensorStatus;
import com.mytechia.robobo.rob.IRob;
import com.mytechia.robobo.rob.IRobInterfaceModule;
import com.mytechia.robobo.rob.IRobStatusListener;
import com.mytechia.robobo.rob.MotorStatus;
import com.mytechia.robobo.rob.WallConnectionStatus;

import java.util.Collection;

import remoterob.IRemoteRobModule;

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

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        rcmodule = manager.getModuleInstance(IRemoteControlModule.class);
        irob = manager.getModuleInstance(IRobInterfaceModule.class).getRobInterface();
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

            }
        });

        rcmodule.registerCommand("MOVEBYTIME", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {

            }
        });

        rcmodule.registerCommand("TURNINPLACE", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {

            }
        });

        rcmodule.registerCommand("MOVEPAN", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {

            }
        });

        rcmodule.registerCommand("MOVETILT", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {

            }
        });
    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return null;
    }

    @Override
    public String getModuleVersion() {
        return null;
    }
}
