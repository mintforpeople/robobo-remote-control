package com.mytechia.robobo.framework.remotecontrol.ros.subscribers;

import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

public class SubNode extends AbstractNodeMain {
    private static final String NAME_NODE_ROB_SUBSCRIBER = "robobo_subscriber";

    private IRemoteControlModule remoteControlModule;
    private String roboboName = "";
    private boolean started = false;

    private ConnectedNode connectedNode;
    private ModuleControlSub moduleControlSub;
    private MovePanTiltSub movePanTiltSub;
    private MoveWheelsSub moveWheelsSub;
    private PlaySoundSub playSoundSub;
    private ResetWheelsSub resetWheelsSub;
    private SetCameraSub setCameraSub;
    private SetEmotionSub setEmotionSub;
    private SetFrequencySub setFrequencySub;
    private SetLedSub setLedSub;
    private TalkSub talkSub;

    public SubNode(IRemoteControlModule remoteControlModule, String roboboName) throws InternalErrorException {

        if (remoteControlModule == null) {
            throw new InternalErrorException("The parameter remoteControlModule is required");
        }

        this.remoteControlModule = remoteControlModule;

        if (roboboName != null) {
            this.roboboName = roboboName;
        }

    }


    String getRoboboName() {
        return this.roboboName;
    }

    ConnectedNode getConnectedNode() {
        return this.connectedNode;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(NodeNameUtility.createNodeName(roboboName, NAME_NODE_ROB_SUBSCRIBER));

    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);

        this.connectedNode = connectedNode;

        this.moduleControlSub = new ModuleControlSub(this);
        this.moduleControlSub.start();

        this.movePanTiltSub = new MovePanTiltSub(this);
        this.movePanTiltSub.start();
        this.moveWheelsSub = new MoveWheelsSub(this);
        this.moveWheelsSub.start();
        this.playSoundSub = new PlaySoundSub(this);
        this.playSoundSub.start();
        this.resetWheelsSub = new ResetWheelsSub(this);
        this.resetWheelsSub.start();
        this.setCameraSub = new SetCameraSub(this);
        this.setCameraSub.start();
        this.setEmotionSub = new SetEmotionSub(this);
        this.setEmotionSub.start();
        this.setFrequencySub = new SetFrequencySub(this);
        this.setFrequencySub.start();
        this.setLedSub = new SetLedSub(this);
        this.setLedSub.start();
        this.talkSub = new TalkSub(this);
        this.talkSub.start();

        this.started = true;
    }


    public IRemoteControlModule getRemoteControlModule() {
        return remoteControlModule;
    }
}
