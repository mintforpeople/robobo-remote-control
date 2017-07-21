/*******************************************************************************
 *
 *   Copyright 2017 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2017 Gervasio Varela <gervasio.varela@mytechia.com>
 *   Copyright 2017 Julio Gomez <julio.gomez@mytechia.com>
 *
 *   This file is part of Robobo Ros Module.
 *
 *   Robobo Ros Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Ros Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Ros Module.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/
package com.mytechia.robobo.framework.remote_control.remotemodule.ros;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by julio on 12/07/17.
 */

public class NodeExecutorServiceConnection implements ServiceConnection {

    private static final String TAG = "Ros Service Connection";

    private NodeMainExecutorService nodeMainExecutorService;

    private NodeConfiguration nodeConfiguration;

    private Boolean activedRosService = false;

    private Collection<NodeMain> pendingStartRosNodes =new ArrayList<>();



    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {

        Log.d(TAG, "Service connected: " + name.flattenToShortString());

        nodeMainExecutorService = ((NodeMainExecutorService.LocalBinder) binder).getService();

        nodeConfiguration = NodeConfiguration.newPublic(nodeMainExecutorService.getRosHostname());

        nodeConfiguration.setMasterUri(nodeMainExecutorService.getMasterUri());

        nodeMainExecutorService.addListener(new NodeMainExecutorServiceListener() {
            @Override
            public void ontStartup(NodeMainExecutorService service) {
            }

            @Override
            public void onShutdown(NodeMainExecutorService service) {
                desactiveRosService();
            }
        });

        activeRosService();


    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Disconnected form service: "+name.flattenToShortString());

        this.desactiveRosService();
    }

    void startRoboRosNode(final NodeMain roboRosNode) {

        synchronized (activedRosService) {
            if (activedRosService) {
                this.startRosNode(roboRosNode);
            } else {
                pendingStartRosNodes.add(roboRosNode);
            }

        }
    }


    private void desactiveRosService(){
        synchronized (activedRosService) {
            activedRosService = false;
        }

        Log.d(TAG, "Desactived Ros Service");
    }

    private void activeRosService(){
        synchronized (activedRosService) {

            for (NodeMain roboRosNode : pendingStartRosNodes) {
                startRosNode(roboRosNode);
            }

            pendingStartRosNodes.clear();

            activedRosService = true;
        }

        Log.d(TAG, "Actived Ros Service");
    }


    private void startRosNode(NodeMain roboRosNode){

        Log.d(TAG, "Starting Ros Node: "+roboRosNode.getClass().getSimpleName());

        nodeMainExecutorService.execute(roboRosNode, nodeConfiguration);
    }




}
