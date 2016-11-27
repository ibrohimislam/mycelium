package me.ibrohim.mycelium;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import me.ibrohim.mycelium.jabber.PresenceListener;


public class MyceliumConnection implements ConnectionListener, ConnectionProvider {
    private static final String TAG = "MyceliumConnection";

    private final String mUsername;
    private final String mPassword;
    private final String mServiceName;
    private XMPPTCPConnection mConnection;

    public enum ConnectionState
    {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED;
    }

    public enum LoggedInState
    {
        LOGGED_IN, LOGGED_OUT;
    }


    public MyceliumConnection()
    {
        Log.d(TAG,"RoosterConnection Constructor called.");

        String jid = "admin@ibrohim.me";
        mPassword = "love4all";

        if( jid != null)
        {
            mUsername = jid.split("@")[0];
            mServiceName = jid.split("@")[1];
        }else
        {
            mUsername ="";
            mServiceName="";
        }
    }

    private static class MyceliumConnectionHolder {
        public static final MyceliumConnection INSTANCE = new MyceliumConnection();
    }

    public static MyceliumConnection getInstance(){
        return MyceliumConnectionHolder.INSTANCE;
    }


    public void connect() throws IOException, XMPPException, InterruptedException, SmackException {
        Log.d(TAG, "Connecting to server " + mServiceName);
        XMPPTCPConnectionConfiguration.Builder builder=
                XMPPTCPConnectionConfiguration.builder();
        builder.setServiceName(mServiceName);
        builder.setUsernameAndPassword(mUsername, mPassword);
        builder.setResource("Rooster");

        //builder.setHost("167.205.3.186");
        //builder.setPort(5222);


        //Set up the ui thread broadcast message receiver.
        //setupUiThreadBroadCastMessageReceiver();

        mConnection = new XMPPTCPConnection(builder.build());
        mConnection.addConnectionListener(this);
        mConnection.connect();
        mConnection.login();

        mConnection.addSyncStanzaListener(new PresenceListener(), new StanzaTypeFilter(Presence.class));

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

    }

    public void disconnect()
    {
        Log.d(TAG,"Disconnecting from server "+ mServiceName);

        if (mConnection != null) {
            mConnection.disconnect();
        }

        mConnection = null;


    }


    @Override
    public void connected(XMPPConnection connection) {
        MyceliumService.sConnectionState=ConnectionState.CONNECTED;
        Log.d(TAG,"Connected Successfully");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        MyceliumService.sConnectionState=ConnectionState.CONNECTED;
        Log.d(TAG,"Authenticated Successfully");

        //Experimental exp = new Experimental();
    }


    @Override
    public void connectionClosed() {
        MyceliumService.sConnectionState=ConnectionState.DISCONNECTED;
        Log.d(TAG,"Connectionclosed()");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        MyceliumService.sConnectionState=ConnectionState.DISCONNECTED;
        Log.d(TAG,"ConnectionClosedOnError, error "+ e.toString());

    }

    @Override
    public void reconnectingIn(int seconds) {
        MyceliumService.sConnectionState = ConnectionState.CONNECTING;
        Log.d(TAG,"ReconnectingIn(" + seconds + ") ");

    }

    @Override
    public void reconnectionSuccessful() {
        MyceliumService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG,"ReconnectionSuccessful()");

    }

    @Override
    public void reconnectionFailed(Exception e) {
        MyceliumService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG,"ReconnectionFailed()");

    }

    @Override
    public XMPPConnection getConnection() {
        return mConnection;
    }
}
