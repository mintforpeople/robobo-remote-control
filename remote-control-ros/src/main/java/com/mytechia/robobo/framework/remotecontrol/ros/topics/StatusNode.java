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

package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;


import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import opencv_apps.Line;
import robobo_msgs.QrCode;


/**
 * Created by julio on 12/07/17.
 */

public class StatusNode extends AbstractNodeMain {

    private static final String STATUS_BATBASE = "BAT-BASE";
    private static final String TOPIC_BATBASE = "battery/base";
    private static final String KEY_BATTERY = "level";

    private static final String STATUS_PHONEBASE = "BAT-PHONE";
    private static final String TOPIC_PHONEBASE = "battery/phone";

    private static final String STATUS_PAN = "PAN";
    private static final String TOPIC_PAN = "pan";
    private static final String STATUS_TILT = "TILT";
    private static final String TOPIC_TILT = "tilt";

    private static final String STATUS_ALIGHT = "AMBIENTLIGHT";
    private static final String TOPIC_ALIGHT = "ambientlight";

    private static final String STATUS_EMOTION = "EMOTION";
    private static final String TOPIC_EMOTION = "emotion";



    private static final String TAG = "Robobo Status";

    private static final String NAME_NODE_ROB_STATUS ="robobo_status";


    private String roboboName ="";

    private ConnectedNode connectedNode;


    private Int8StatusTopic baseBatteryStatusTopic;
    private Int8StatusTopic phoneBatteryStatusTopic;
    private Int16StatusTopic panStatusTopic;
    private Int16StatusTopic tiltStatusTopic;
    private Int32StatusTopic ambientLightStatusTopic;
    private StringStatusTopic emotionStatusTopic;
    private AccelerationStatusTopic accelStatusTopic;
    private OrientationStatusTopic orientationStatusTopic;
    private OrientationEulerStatusTopic orientationEulerStatusTopic;
    private UnlockMoveStatusTopic unlockMoveStatusTopic;
    private UnlockTalkStatusTopic unlockTalkStatusTopic;
    private WheelsStatusTopic wheelsStatusTopic;
    private LedStatusTopic ledStatusTopic;
    private TapStatusTopic tapStatusTopic;
    private FlingStatusTopic flingStatusTopic;
    private IRsStatusTopic irsStatusTopic;
    private DetectedObjectStatusTopic detectedObjectStatusTopic;
    private TagStatusTopic tagStatusTopic;
    private LaneBasicStatusTopic laneBasicStatusTopic;
    private LaneProStatusTopic laneProStatusTopic;
    private QrCodeStatusTopic qrCodeStatusTopic;
    private LineStatusTopic lineStatusTopic;
    private QrCodeLostTopic qrCodeLostTopic;
    private QrCodeAppearTopic qrCodeAppearTopic;

    private boolean started = false;


    public StatusNode(String roboboName){

        if(roboboName !=null){
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
        return GraphName.of(NodeNameUtility.createNodeName(roboboName, NAME_NODE_ROB_STATUS));
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {

        super.onStart(connectedNode);

        this.connectedNode = connectedNode;

        this.baseBatteryStatusTopic = new Int8StatusTopic(this, TOPIC_BATBASE, STATUS_BATBASE, KEY_BATTERY);
        this.baseBatteryStatusTopic.start();

        this.phoneBatteryStatusTopic = new Int8StatusTopic(this, TOPIC_PHONEBASE, STATUS_PHONEBASE, KEY_BATTERY);
        this.phoneBatteryStatusTopic.start();

        this.panStatusTopic = new Int16StatusTopic(this, TOPIC_PAN, STATUS_PAN, "panPos");
        this.panStatusTopic.start();

        this.tiltStatusTopic = new Int16StatusTopic(this, TOPIC_TILT, STATUS_TILT, "tiltPos");
        this.tiltStatusTopic.start();

        this.ambientLightStatusTopic = new Int32StatusTopic(this, TOPIC_ALIGHT, STATUS_ALIGHT, "level");
        this.ambientLightStatusTopic.start();

        this.emotionStatusTopic = new StringStatusTopic(this, TOPIC_EMOTION, STATUS_EMOTION, "emotion");
        this.emotionStatusTopic.start();

        this.accelStatusTopic = new AccelerationStatusTopic(this);
        this.accelStatusTopic.start();

        this.orientationStatusTopic = new OrientationStatusTopic(this);
        this.orientationStatusTopic.start();

        this.orientationEulerStatusTopic = new OrientationEulerStatusTopic(this);
        this.orientationEulerStatusTopic.start();

        this.unlockMoveStatusTopic = new UnlockMoveStatusTopic(this);
        this.unlockMoveStatusTopic.start();

        this.unlockTalkStatusTopic = new UnlockTalkStatusTopic(this);
        this.unlockTalkStatusTopic.start();

        this.wheelsStatusTopic = new WheelsStatusTopic(this);
        this.wheelsStatusTopic.start();

        this.ledStatusTopic = new LedStatusTopic(this);
        this.ledStatusTopic.start();

        this.tapStatusTopic = new TapStatusTopic(this);
        this.tapStatusTopic.start();

        this.flingStatusTopic = new FlingStatusTopic(this);
        this.flingStatusTopic.start();

        this.irsStatusTopic = new IRsStatusTopic(this);
        this.irsStatusTopic.start();

        this.detectedObjectStatusTopic = new DetectedObjectStatusTopic(this);
        this.detectedObjectStatusTopic.start();

        this.tagStatusTopic = new TagStatusTopic(this);
        this.tagStatusTopic.start();

        this.laneBasicStatusTopic=new LaneBasicStatusTopic(this);
        laneBasicStatusTopic.start();

        this.laneProStatusTopic=new LaneProStatusTopic(this);
        laneProStatusTopic.start();

        this.qrCodeStatusTopic=new QrCodeStatusTopic(this);
        qrCodeStatusTopic.start();

        this.qrCodeAppearTopic=new QrCodeAppearTopic(this);
        qrCodeAppearTopic.start();

        this.qrCodeLostTopic=new QrCodeLostTopic(this);
        qrCodeLostTopic.start();

        this.lineStatusTopic=new LineStatusTopic(this);
        lineStatusTopic.start();

        this.started = true;

    }



    public void publishStatusMessage(com.mytechia.robobo.framework.remote_control.remotemodule.Status status) {

        if (started) {

            switch (status.getName()) {

                case STATUS_BATBASE:
                    this.baseBatteryStatusTopic.publishStatus(status);
                    break;

                case STATUS_PHONEBASE:
                    this.phoneBatteryStatusTopic.publishStatus(status);
                    break;

                case STATUS_PAN:
                    this.panStatusTopic.publishStatus(status);
                    break;

                case STATUS_TILT:
                    this.tiltStatusTopic.publishStatus(status);
                    break;

                case STATUS_ALIGHT:
                    this.ambientLightStatusTopic.publishStatus(status);
                    break;

                case STATUS_EMOTION:
                    this.emotionStatusTopic.publishStatus(status);
                    break;

                case AccelerationStatusTopic.STATUS:
                    this.accelStatusTopic.publishStatus(status);
                    break;

                case OrientationStatusTopic.STATUS:
                    this.orientationStatusTopic.publishStatus(status);
                    this.orientationEulerStatusTopic.publishStatus(status);
                    break;

                case UnlockTalkStatusTopic.STATUS_UNLOCK_TALK:
                    this.unlockTalkStatusTopic.publishStatus(status);
                    break;

                case WheelsStatusTopic.STATUS:
                    this.wheelsStatusTopic.publishStatus(status);
                    break;

                case LedStatusTopic.STATUS:
                    this.ledStatusTopic.publishStatus(status);
                    break;

                case TapStatusTopic.STATUS:
                    this.tapStatusTopic.publishStatus(status);
                    break;

                case FlingStatusTopic.STATUS:
                    this.flingStatusTopic.publishStatus(status);
                    break;

                case IRsStatusTopic.STATUS:
                    this.irsStatusTopic.publishStatus(status);
                    break;

                case DetectedObjectStatusTopic.STATUS:
                    this.detectedObjectStatusTopic.publishStatus(status);
                    break;

                case TagStatusTopic.STATUS:
                    this.tagStatusTopic.publishStatus(status);
                    break;

                case LaneBasicStatusTopic.STATUS:
                    this.laneBasicStatusTopic.publishStatus(status);
                    break;

                case LaneProStatusTopic.STATUS:
                    this.laneProStatusTopic.publishStatus(status);
                    break;

                case QrCodeStatusTopic.STATUS:
                    this.qrCodeStatusTopic.publishStatus(status);
                    break;

                case QrCodeAppearTopic.STATUS:
                    this.qrCodeAppearTopic.publishStatus(status);
                    break;

                case QrCodeLostTopic.STATUS:
                    this.qrCodeLostTopic.publishStatus(status);
                    break;

                case LineStatusTopic.STATUS:
                    this.lineStatusTopic.publishStatus(status);
                    break;

                default:
                    this.unlockMoveStatusTopic.publishStatus(status);
                    break;

            }

        }

    }


}
