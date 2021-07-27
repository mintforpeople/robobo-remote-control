package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.Time;
import org.ros.node.topic.Publisher;

import robobo_msgs.Blob;
import vision_msgs.Detection2D;
import vision_msgs.Detection2DArray;
import vision_msgs.ObjectHypothesisWithPose;

/**
 * Status Topic for detected objects by the vision module.
 * <p>
 * The topic is robot/detected_object
 */
public class BlobStatusTopic extends AStatusTopic {

    private static final String TOPIC = "blob";
    public static final String STATUS = "BLOB";
    public static final String TAG = "BLOB_TOPIC";


    private Publisher<Blob> topic;


    public BlobStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Blob._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {
            Blob msg = this.topic.newMessage();
            Log.d("ROS","Publish blob status");

            String frame_id = status.getValue().get("frame_id"),
                    color = status.getValue().get("color"),
                    size = status.getValue().get("size"),
                    posX = status.getValue().get("posx"),
                    posY = status.getValue().get("posy");
            //Log.d(TAG,"Detected "+detections);
            Log.d("ROS","Publish blob status"+frame_id+" "+color+" "+size+" "+posX+" "+posY);



            if (frame_id != null &&
                    color != null &&
                    size != null &&
                    posX != null &&
                    posY != null

            ) {
                Time time = Time.fromMillis(System.currentTimeMillis());
                msg.getHeader().setFrameId(frame_id);
                msg.getHeader().setStamp(time);
                msg.getCenter().setX(Double.parseDouble(posX));
                msg.getCenter().setY(Double.parseDouble(posY));
                msg.getSize().setData(Integer.parseInt(size));
                msg.getColor().setData(color);


                this.topic.publish(msg);

            }
        }

    }
}

