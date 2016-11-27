package me.ibrohim.mycelium;

/**
 * Created by ibrohim on 11/2/16.
 */

import org.jivesoftware.smack.XMPPConnection;

public interface ConnectionProvider {
    XMPPConnection getConnection();
}
