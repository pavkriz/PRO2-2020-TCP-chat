package cz.uhk.pro2.tcpchat.server;

import java.net.Socket;

public interface MessageBroacaster {
    void broadcastMessage(String message, Socket sender);
}
