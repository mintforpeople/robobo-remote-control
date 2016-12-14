package com.mytechia.robobo.framework.hri.touch;

import android.util.Log;

import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;

import java.util.HashSet;

/**
 * Created by luis on 5/4/16.
 */


public abstract class ATouchModule implements ITouchModule {
    private HashSet<ITouchListener> listeners;
    protected IRemoteControlModule rcmodule = null;

    private String TAG = "ATOUCHMODULE";
    public ATouchModule(){
        listeners = new HashSet<ITouchListener>();
    }

    /**
     * Notifies a single tap on the screen, the origin is the top left of the screen
     * @param x Horizontal coordinates
     * @param y Vertical coordinates
     */
    protected void notifyTap(Integer x, Integer y){
        for (ITouchListener listener:listeners){
            listener.tap(x, y);
        }
        Log.d(TAG,"-----TAP-----");
        if (rcmodule!=null) {
            Status status = new Status("TAP");
            status.putContents("coordx",x.toString());
            status.putContents("coordy",y.toString());
            Log.d(TAG, status.toString());
            rcmodule.postStatus(status);
        }
    }

    /**
     * Notifies a fast ending scroll movement
     * @param dir The TouchGestureDirection direction
     * @param angle The angle of the movement towards the starting point
     * @param time The time elapsed by the movement
     * @param distance The distance elapsed
     */
    protected void notifyFling(TouchGestureDirection dir, double angle, long time, double distance){
        for (ITouchListener listener:listeners){
            listener.fling(dir,angle,time,distance);
        }
        Log.d(TAG,"-----FLING-----"+Math.toDegrees(angle));
        if (rcmodule!=null) {
            Status status = new Status("FLING");
            status.putContents("angle",Math.toDegrees(angle)+"");
            status.putContents("time",time+"");
            status.putContents("distance",distance+"");
            Log.d(TAG, status.toString());
            rcmodule.postStatus(status);
        }
    }

    /**
     * Notifies a scrolling action
     * @param dir The TouchGestureDirection direction
     */
    protected void notifyCaress(TouchGestureDirection dir){
        for (ITouchListener listener:listeners){
            listener.caress(dir);
        }
    }

    /**
     * Notifies a long tap, the origin is the top left of the screen
     * @param x Horizontal coordinates
     * @param y Vertical coordinates
     */
    protected void notifyTouch(Integer x, Integer y){
        for (ITouchListener listener:listeners){
            listener.touch(x,y);
        }
    }
    public void suscribe(ITouchListener listener){
        Log.d("AT_module", "Suscribed:"+listener.toString());
        listeners.add(listener);
    }
    public void unsuscribe(ITouchListener listener){
        listeners.remove(listener);
    }

}
