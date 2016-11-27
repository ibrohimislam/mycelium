package me.ibrohim.mycelium.jabber;

import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;

/**
 * Created by ibrohim on 11/22/16.
 */

public class PresenceListener implements StanzaListener {

    public void processPacket(Stanza packet) {
        Presence presence = (Presence) packet;

        if (presence.getType() == null || presence.getType() == Presence.Type.available) {
            String from = presence.getFrom();
            if (from != null && from.lastIndexOf("/") > 0) {
                String resource = from.substring(from.lastIndexOf("/") + 1);
            }
        }

    }
}