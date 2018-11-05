/*******************************************************************************
 * Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright 2016 Luis Llamas <julio.gomez@mytechia.com>
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

package com.mytechia.robobo.framework.remote_control.remotemodule;

import static java.lang.String.format;
import android.util.Log;

import com.mytechia.robobo.framework.LogLvl;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.power.PowerMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;


/**
 * Created by julio on 7/07/17.
 */

public class CommandQueueProcessor extends Thread {

    private static final String COMMAND_QUEUE_THREAD_NAME="Command Queue Processor";

    private String TAG = "CommandQueueProcessor";

    private BlockingQueue<Command> commands= new LinkedBlockingDeque();

    private Map<String, ICommandExecutor> commandsExecutors= new HashMap<>();

    private Object lockCommandsExecutors= new Object();

    private IRemoteControlModule module;
    private RoboboManager roboboManager;

    private long lastCommandReceivedTime = 0;
    private static final long MAX_TIME_WITHOUT_COMMANDS_TO_SLEEP = 1000*60*3; //3minutes



    public CommandQueueProcessor(IRemoteControlModule module, RoboboManager roboboManager) {

        Objects.requireNonNull(module, "The parameter module is required");
        Objects.requireNonNull(roboboManager, "The parameter roboboModule is required");

        this.module = module;
        this.roboboManager = roboboManager;

        TimerTask tt = new PeriodicCommandReceptionChecker();
        Timer t = new Timer();
        t.scheduleAtFixedRate(tt, MAX_TIME_WITHOUT_COMMANDS_TO_SLEEP, MAX_TIME_WITHOUT_COMMANDS_TO_SLEEP);

        this.registerCommand("KEEP-ALIVE", new KeepAliveCommandExecutor(roboboManager));


        this.registerCommand("SET-SENSOR-FREQUENCY", new SetSensorFrequencyCommandExecutor(roboboManager));


    }
    @Override
    public void run() {

        Log.i(TAG, format("Running %s", COMMAND_QUEUE_THREAD_NAME));

        while (!this.isInterrupted()) {

            Command command = null;

            try {
                command = commands.take();
            } catch (InterruptedException e) {
                Log.w(TAG, format("Error processing command. Interrupted thread: %s", COMMAND_QUEUE_THREAD_NAME), e);
                return;
            }

            ICommandExecutor commandExecutor = null;

            try {
                commandExecutor = this.getCommandExecutor(command.getName());
            } catch (Throwable th) {
                Log.e(TAG, format("Error processing command. Failed to get command", COMMAND_QUEUE_THREAD_NAME), th);
                continue;
            }

            if (commandExecutor == null) {
                Log.e(TAG, format("Error processing command. Not found ICommandExecutor to process Command[id=%s, name=%s]", command.getId(), command.getName()));
                continue;
            }

            try {
                commandExecutor.executeCommand(command, module);
            } catch (Throwable th) {
                Log.e(TAG, format("Error processing command [id=%s, name=%s]", command.getId(), command.getName()), th);
            }

        }

    }

    public void registerCommand(String commandName, ICommandExecutor module){
        commandsExecutors.put(commandName, module);
    }

    public void dispose(){
        this.interrupt();
    }

    private  ICommandExecutor getCommandExecutor(String commandExecutorName){

        Objects.requireNonNull(commandExecutorName, "The parameter command can not be null");

        synchronized (lockCommandsExecutors) {
            ICommandExecutor commandExecutor = commandsExecutors.get(commandExecutorName);
            return commandExecutor;
        }
    }

    public  void registerCommandExecutor(String commandName, ICommandExecutor commandExecutor){

        if(this.isInterrupted()){
            throw new RuntimeException(format("The command can not be added to the CommandQueueProcessor. Interrupted thread: %s", COMMAND_QUEUE_THREAD_NAME));
        }

        Objects.requireNonNull(commandName, "The parameter command can not be null");

        Objects.requireNonNull(commandExecutor, "The parameter commandExecutor can not be null");


        synchronized (lockCommandsExecutors){
             commandsExecutors.put(commandName, commandExecutor);
        }

    }

    public void put(Command command) throws RuntimeException{

        if(this.isInterrupted()){
            throw new RuntimeException(format("The command can not be added to the CommandQueueProcessor. Interrupted thread: %s", COMMAND_QUEUE_THREAD_NAME));
        }

        Objects.requireNonNull(command, "The parameter command can not be null");

        try {

            this.lastCommandReceivedTime = System.currentTimeMillis();
            roboboManager.changePowerModeTo(PowerMode.NORMAL);

            commands.put(command);

        } catch (InterruptedException ex) {
            throw new RuntimeException(format("The command can not be added to the CommandQueueProcessor. Interrupted thread: %s", COMMAND_QUEUE_THREAD_NAME), ex);
        }
    }


    private class PeriodicCommandReceptionChecker extends TimerTask {

        @Override
        public void run() {

            if ((System.currentTimeMillis() - lastCommandReceivedTime) > MAX_TIME_WITHOUT_COMMANDS_TO_SLEEP) {
                //entering low power mode
                roboboManager.log(LogLvl.INFO, "REMOTE-MODULE", "Entering low power mode.");
                roboboManager.changePowerModeTo(PowerMode.LOWPOWER);
            }

        }
    }


}
