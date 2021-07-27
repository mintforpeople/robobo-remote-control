package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import android.util.Log;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.Time;
import org.ros.node.topic.Publisher;

import java.util.Arrays;

import opencv_apps.Point2D;
import robobo_msgs.QrCode;


/**
 * Status Topic for detected qr codes by the vision module.
 * <p>
 * The topic is robot/qrcode
 */
public class QrCodeStatusTopic extends AStatusTopic {

    private static final String TOPIC = "qr/status";
    public static final String STATUS = "QRCODE";


    private Publisher<QrCode> topic;


    public QrCodeStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), QrCode._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {
            QrCode msg = this.topic.newMessage();
            Log.d("ROS","Publish qr status");
            String id = status.getValue().get("id"),
                    coordx = status.getValue().get("coordx"),
                    coordy = status.getValue().get("coordy"),
                    distance = status.getValue().get("distance"),
                    frame_id = status.getValue().get("frame_id"),
                    p1x = status.getValue().get("p1x"),
                    p1y = status.getValue().get("p1y"),
                    p2x = status.getValue().get("p2x"),
                    p2y = status.getValue().get("p2y"),
                    p3x = status.getValue().get("p3x"),
                    p3y = status.getValue().get("p3y");
            Log.d("QR",id +" "+ coordx+ " "+coordy+" "+ distance +" "+frame_id );

            if (id != null &&
                    coordx != null &&
                    coordy != null &&
                    distance != null &&
                    p1x != null &&
                    p1y != null &&
                    p2x != null &&
                    p2y != null &&
                    p3x != null &&
                    p3y != null &&
                    frame_id != null
            ) {

                msg.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
                msg.getHeader().setFrameId(frame_id);
                msg.setText(id);
                msg.setDistance(Float.parseFloat(distance));
                msg.getCenter().setX(Double.parseDouble(coordx));
                msg.getCenter().setY(Double.parseDouble(coordy));

//                Point2D[] resultPoints= new Point2D[3];
//                resultPoints[0]=this.node.getConnectedNode().getTopicMessageFactory().newFromType(Point2D._TYPE);
//                resultPoints[1]=this.node.getConnectedNode().getTopicMessageFactory().newFromType(Point2D._TYPE);
//                resultPoints[2]=this.node.getConnectedNode().getTopicMessageFactory().newFromType(Point2D._TYPE);
//
//                resultPoints[1].setX(Double.parseDouble(p1x));
//                resultPoints[1].setY(Double.parseDouble(p1y));
//                msg.setResultPoints(Arrays.asList(resultPoints));

                msg.getResultPoints().add(((Point2D) node.getConnectedNode().getTopicMessageFactory().newFromType(Point2D._TYPE)));
                msg.getResultPoints().add(((Point2D) node.getConnectedNode().getTopicMessageFactory().newFromType(Point2D._TYPE)));
                msg.getResultPoints().add(((Point2D) node.getConnectedNode().getTopicMessageFactory().newFromType(Point2D._TYPE)));
                msg.getResultPoints().get(0).setX(Double.parseDouble(p1x));
                msg.getResultPoints().get(0).setY(Double.parseDouble(p1y));
                msg.getResultPoints().get(1).setX(Double.parseDouble(p2x));
                msg.getResultPoints().get(1).setY(Double.parseDouble(p2y));
                msg.getResultPoints().get(2).setX(Double.parseDouble(p3x));
                msg.getResultPoints().get(2).setY(Double.parseDouble(p3y));
                this.topic.publish(msg);

            }

        }

    }
}
