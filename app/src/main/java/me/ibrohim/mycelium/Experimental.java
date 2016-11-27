package me.ibrohim.mycelium;

import android.util.Log;

import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.ContentPacketExtension;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.JingleIQ;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.JinglePacketFactory;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ibrohim on 11/20/16.
 */

public class Experimental {
    ServiceDiscoveryManager mServiceDiscoveryManager;
    private final String TAG = "Experimental";

    public Experimental(){
        XMPPConnection connection = MyceliumConnection.getInstance().getConnection();
        mServiceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);

//        try {
//
//            DiscoverInfo di = mServiceDiscoveryManager.discoverInfo("ibrohim@ibrohim.me");
//            Log.d(TAG, "DiscoverInfo: " + di.toString());
//
//        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
//            e.printStackTrace();
//        }

        Roster roster = Roster.getInstanceFor(connection);

        if (!roster.isLoaded())
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException | InterruptedException e) {
                e.printStackTrace();
            }


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        String FullJID = "";

        Collection<Presence> entries = roster.getPresences("ibrohim@ibrohim.me");

        for(Iterator<Presence> i = entries.iterator(); i.hasNext(); ) {
            Presence presence = i.next();
            Log.d(TAG, "getPriority: " + presence.getPriority());
            FullJID  = presence.getFrom();
            Log.d(TAG, "FullJID: " + FullJID);
        }

        Log.d(TAG, "FullJID: " + FullJID);


        List<ContentPacketExtension> offer = new ArrayList<>();

        JingleIQ sessionInitIQ = JinglePacketFactory.createSessionInitiate(
                "admin@ibrohim.me/Rooster",
                FullJID,
                JingleIQ.generateSID(),
                offer);

        String oldChildElementXML = sessionInitIQ.__getChildElementXML();

        Log.d(TAG, "oldChildElementXML: " +  oldChildElementXML);

        Log.d(TAG, "sessionInitIQ: " +  sessionInitIQ.toXML().toString());

        try {

            connection.sendPacket(sessionInitIQ);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}
