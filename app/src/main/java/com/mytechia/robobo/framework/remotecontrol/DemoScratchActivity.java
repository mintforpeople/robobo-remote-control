package com.mytechia.robobo.framework.remotecontrol;

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


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.emotion.Emotion;
import com.mytechia.robobo.framework.hri.emotion.IEmotionModule;
import com.mytechia.robobo.framework.hri.emotion.ITouchEventListener;
import com.mytechia.robobo.framework.hri.emotion.webgl.WebGLEmotionDisplayActivity;

import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;
import com.mytechia.robobo.framework.hri.speech.production.ISpeechProductionModule;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionListener;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionModule;

import com.mytechia.robobo.framework.hri.touch.ITouchModule;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraModule;
import com.mytechia.robobo.framework.hri.vision.colorDetection.IColorDetectionModule;
import com.mytechia.robobo.framework.hri.vision.colorDetection.IColorListener;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceDetectionModule;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceListener;

import com.mytechia.robobo.framework.sensing.brightness.IBrightnessModule;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;
import com.mytechia.robobo.rob.IRob;
import com.mytechia.robobo.rob.IRobInterfaceModule;
import com.mytechia.robobo.rob.MoveMTMode;
import com.mytechia.robobo.rob.movement.DefaultRobMovementModule;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;
import com.mytechia.robobo.util.Color;

import org.opencv.core.Mat;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class DemoScratchActivity extends Activity implements ITouchEventListener{

    private RoboboServiceHelper roboboHelper;
    private RoboboManager robobo;

    private ProgressDialog waitDialog;

    private String TAG = "PETROBOBO";

    private IEmotionModule emotionModule;
    private ISpeechProductionModule speechModule;

    private ISpeechRecognitionModule recognitionModule;
    private IFaceDetectionModule faceDetectionModule;
    private ICameraModule cameraModule;
    private IColorDetectionModule colorDetectionModule;

    private ISoundDispatcherModule soundDispatcherModule;

    private ITouchModule touchModule;

    private IRobInterfaceModule interfaceModule;
    private IRobMovementModule movementModule;

    private IBrightnessModule brightnessModule;

    private IRob iRob;

    private static Random r = new Random();
    private Timer t = new Timer();


    private boolean test = true;

    String ip = "";
    //region Listeners

    //region LIGHTS

    //endregion

    //region ActivityListeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        roboboHelper = new RoboboServiceHelper(this, new RoboboApplication());
//        roboboHelper.bindRoboboService(new Bundle());

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        showIpDialog();


    }



    @Override
    protected void onDestroy() {

        super.onDestroy();
        roboboHelper.unbindRoboboService();
        finish();
    }


    //endregion





    //endregion





//endregion

    //region App Initialization things





    protected void startRoboboApplication() {

        roboboHelper.launchDisplayActivity(WebGLEmotionDisplayActivity.class);

        t.schedule(new startInterface(),(long)1000);


        try {

            emotionModule =
                    robobo.getModuleInstance(IEmotionModule.class);
            speechModule =
                    robobo.getModuleInstance(ISpeechProductionModule.class);


            movementModule=
                    robobo.getModuleInstance(IRobMovementModule.class);
            interfaceModule=
                    robobo.getModuleInstance(IRobInterfaceModule.class);
            cameraModule=
                    robobo.getModuleInstance(ICameraModule.class);
            colorDetectionModule=
                    robobo.getModuleInstance(IColorDetectionModule.class);
            faceDetectionModule=
                    robobo.getModuleInstance(IFaceDetectionModule.class);
            recognitionModule=
                    robobo.getModuleInstance(ISpeechRecognitionModule.class);
            touchModule=
                    robobo.getModuleInstance(ITouchModule.class);
            soundDispatcherModule=
                    robobo.getModuleInstance(ISoundDispatcherModule.class);
            brightnessModule=
                    robobo.getModuleInstance(IBrightnessModule.class);
        }

        catch(ModuleNotFoundException e) {
            final Exception ex = e;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorDialog(ex.getMessage());
                }
            });
        }

        emotionModule.subscribeTouchListener(this);

        showIpDialog();

        soundDispatcherModule.runDispatcher();

        Locale spanish = new Locale("es", "ES");
        speechModule.setLocale(spanish);
        //cameraModule.passOCVthings(emotionModule.getCameraBridgeView());




        colorDetectionModule.pauseDetection();
        iRob = interfaceModule.getRobInterface();




        try {
            iRob.setOperationMode((byte) 1);
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }



//        try {
//            interfaceModule.getRobInterface().resetPanTiltOffset();
//        } catch (InternalErrorException e) {
//            e.printStackTrace();
//        }


    }

    private void showRoboboDeviceSelectionDialog() {

        RoboboDeviceSelectionDialog dialog = new RoboboDeviceSelectionDialog();
        dialog.setListener(new RoboboDeviceSelectionDialog.Listener() {
            @Override
            public void roboboSelected(String roboboName) {

                final String roboboBluetoothName = roboboName;

                //start the framework in background
                AsyncTask<Void, Void, Void> launchRoboboService =
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                launchAndConnectRoboboService(roboboBluetoothName);
                                return null;
                            }
                        };
                launchRoboboService.execute();


            }

            @Override
            public void selectionCancelled() {

            }

            @Override
            public void bluetoothIsDisabled() {
                finish();
            }

        });
        dialog.show(getFragmentManager(),"BLUETOOTH-DIALOG");

    }


    private void launchAndConnectRoboboService(String roboboBluetoothName) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //wait to dialog shown during the startup of the framework and the bluetooth connection
                waitDialog = ProgressDialog.show(DemoScratchActivity.this,
                        "Conectando","conectando", true);
            }
        });


        //we use the RoboboServiceHelper class to manage the startup and binding
        //of the Robobo Manager service and Robobo modules
        roboboHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager robobom) {

                //the robobo service and manager have been started up
                robobo = robobom;

                //dismiss the wait dialog
                waitDialog.dismiss();

                //start the "custom" robobo application








                startRoboboApplication();

            }

            @Override
            public void onError(String errorMsg) {

                final String error = errorMsg;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //dismiss the wait dialog
                        waitDialog.dismiss();

                        //show an error dialog


                    }
                });

            }

        });

        //start & bind the Robobo service
        Bundle options = new Bundle();
        options.putString(BluetoothRobInterfaceModule.ROBOBO_BT_NAME_OPTION, roboboBluetoothName);
        roboboHelper.bindRoboboService(options);

    }




    /** Shows an error dialog with the message 'msg'
     *
     * @param msg the message to be shown in the error dialog
     */
    protected void showErrorDialog(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(com.mytechia.robobo.framework.R.string.title_error_dialog).
                setMessage(msg);
        builder.setPositiveButton(com.mytechia.robobo.framework.R.string.ok_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //endregion

    //region Game Methods



    private static int generateRandom(int min, int max) {
        // max - min + 1 will create a number in the range of min and max, including max. If you don´t want to include it, just delete the +1.
        // adding min to it will finally create the number in the range between min and max
        return r.nextInt(max-min+1) + min;
    }

    private void showIpDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DemoScratchActivity.this);

        // set title
        alertDialogBuilder.setTitle("ROBOBO IP");

        // set dialog message
        alertDialogBuilder
                .setMessage("Introduce esta IP en el bloque de conexión: "+ip)
                .setCancelable(false)
                .setPositiveButton("Continue",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        showRoboboDeviceSelectionDialog();

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.getButton(0).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showRoboboDeviceSelectionDialog();
//            }
//        });
        // show it
        alertDialog.show();
    }

    @Override
    public void onScreenTouchEvent(MotionEvent event) {
        Log.d(TAG,"OSTEV");
        touchModule.feedTouchEvent(event);
    }


    private class startInterface extends TimerTask {

        @Override
        public void run() {
            if (test) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.d(TAG,"PASSOCV");
                        cameraModule.passOCVthings(emotionModule.getCameraBridgeView());
                        cameraModule.signalInit();
                        test = false;
                        colorDetectionModule.startDetection();


                    }
                });


            }
        }
    }

    //endregion

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d("WebGlActivity","EVENT");

        return super.onTouchEvent(event);
    }


}