package cz.uhk.pro2.tcpchat.server;

import java.net.Socket;

public interface MessageBroadcaster {
    void broadcastMessage(String message, Socket sourceSocket);
}
