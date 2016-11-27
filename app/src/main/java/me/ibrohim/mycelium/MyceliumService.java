package me.ibrohim.mycelium;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;


/**
 * Created by ibrohim on 11/2/16.
 */

public class MyceliumService extends Service {

    private static final String TAG ="MyceliumService";

    public static MyceliumConnection.ConnectionState sConnectionState;
    public static MyceliumConnection.LoggedInState sLoggedInState;
    private MyceliumConnection mConnection;

    private boolean mActive;//Stores whether or not the thread is active
    private Thread mThread;
    private Handler mTHandler;//We use this handler to post messages to
    //the background thread.

    public MyceliumService() {

    }

    public static MyceliumConnection.ConnectionState getState()
    {
        if (sConnectionState == null)
        {
            return MyceliumConnection.ConnectionState.DISCONNECTED;
        }
        return sConnectionState;
    }

    public static MyceliumConnection.LoggedInState getLoggedInState()
    {
        if (sLoggedInState == null)
        {
            return MyceliumConnection.LoggedInState.LOGGED_OUT;
        }
        return sLoggedInState;
    }

    private void initConnection()
    {
        Log.d(TAG,"initConnection()");

        mConnection = MyceliumConnection.getInstance();

        try
        {
            mConnection.connect();

        } catch (IOException |XMPPException | InterruptedException | SmackException e) {
            Log.d(TAG,"Something went wrong while connecting ,make sure the credentials are right and try again");
            e.printStackTrace();
            //Stop the service all together.
            stopSelf();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate()");
    }


    public void start()
    {
        Log.d(TAG," Service Start() function called.");
        if(!mActive)
        {
            mActive = true;
            if( mThread ==null || !mThread.isAlive())
            {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                        Looper.loop();

                    }
                });
                mThread.start();
            }


        }


    }

    public void stop()
    {
        Log.d(TAG,"stop()");
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
                if( mConnection != null)
                {
                    mConnection.disconnect();
                }
            }
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()");
        start();
        return Service.START_STICKY;
        //RETURNING START_STICKY CAUSES OUR CODE TO STICK AROUND WHEN THE APP ACTIVITY HAS DIED.
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
        stop();
    }
}
