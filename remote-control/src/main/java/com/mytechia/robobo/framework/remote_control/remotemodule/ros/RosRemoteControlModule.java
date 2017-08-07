package com.mytechia.robobo.framework.remote_control.remotemodule.ros;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlProxy;
import com.mytechia.robobo.framework.remote_control.remotemodule.Response;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remote_control.remotemodule.ros.services.CommandService;
import com.mytechia.robobo.framework.remote_control.remotemodule.ros.topics.ResponseTopic;
import com.mytechia.robobo.framework.remote_control.remotemodule.ros.topics.StatusTopic;

import org.ros.address.InetAddressFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by julio on 7/08/17.
 */

public class RosRemoteControlModule implements IRemoteControlProxy, IRosRemoteControlModule {

    private static final String MODULE_INFO = "Ros RC Module";

    private static final String TAG = MODULE_INFO;

    private static final String MODULE_VERSION = "0.1.0";

    public final static String DEFAULT_MASTER_URI = "http://localhost:11311/";

    public static final String MASTER_URI = "com.mytehia.ros.master.uri";

    public static final String ROBOBO_NAME="robobo.name";

    private Context context;

    private StatusTopic statusTopic;

    private String roboName;

    private IRemoteControlModule remoteControlModule;

    private CommandService commandService;

    private ResponseTopic responseTopic;

    private NodeConfiguration nodeConfiguration;

    private AndroidNodeMainExecutor nodeMainExecutor;


    @Override
    public void startup(RoboboManager roboboManager) throws InternalErrorException {

        Log.d(TAG, "Start Ros Remote Control Module");

        this.remoteControlModule = roboboManager.getModuleInstance(IRemoteControlModule.class);

        if (this.remoteControlModule == null) {
            throw new InternalErrorException("No found instance IRemoteControlModule.");
        }


        this.context = roboboManager.getApplicationContext();

        Bundle roboboBundleOptions = roboboManager.getOptions();

        String masterUri = roboboBundleOptions.getString(MASTER_URI, DEFAULT_MASTER_URI);

        String rosHostName = InetAddressFactory.newNonLoopback().getHostAddress();

        try {
            this.nodeMainExecutor = new AndroidNodeMainExecutor(context, masterUri, rosHostName);
        } catch (URISyntaxException ex) {
            throw new InternalErrorException(ex, "Error startup Ros Remote Control Module");
        }

        this.nodeConfiguration = NodeConfiguration.newPublic(rosHostName);

        try {
            this.nodeConfiguration.setMasterUri(new URI(masterUri));

        } catch (URISyntaxException ex) {
            this.nodeMainExecutor.shutdown();
            throw new InternalErrorException(ex, "Error startup Ros Remote Control Module");
        }


        Bundle roboboOptions = roboboManager.getOptions();

        this.roboName = roboboOptions.getString(RosRemoteControlModule.ROBOBO_NAME, "");

        this.initRoboRosNodes(remoteControlModule, this.roboName);

        this.remoteControlModule.registerRemoteControlProxy(this);


    }


    @Override
    public void startRoboRosNode(NodeMain node) {
        Log.d(TAG, "Starting Ros Node: " + node.getClass().getSimpleName());

        nodeMainExecutor.execute(node, this.nodeConfiguration);
    }

    @Override
    public String getRoboboName() {
        return this.roboName;
    }


    @Override
    public void shutdown() throws InternalErrorException {

        if (nodeMainExecutor != null) {
            nodeMainExecutor.shutdown();
        }

    }


    private void initRoboRosNodes(IRemoteControlModule remoteControlModule, String roboName) throws InternalErrorException {

        this.statusTopic = new StatusTopic(roboName);

        this.startRoboRosNode(this.statusTopic);

        this.commandService = new CommandService(remoteControlModule, roboName);

        this.startRoboRosNode(this.commandService);

        this.responseTopic = new ResponseTopic(roboName);

        this.startRoboRosNode(this.responseTopic);

    }


    @Override
    public String getModuleInfo() {
        return MODULE_INFO;
    }

    @Override
    public String getModuleVersion() {
        return MODULE_VERSION;
    }

    @Override
    public void notifyStatus(Status status) {
        statusTopic.publishStatusMessage(status);
    }

    @Override
    public void notifyReponse(Response response) {
        responseTopic.publishResponseMessage(response);
    }
}
