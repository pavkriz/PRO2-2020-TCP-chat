package cz.uhk.pro2.tcpchat.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TcpChatServer implements MessageBroadcaster {
    final List<Socket> connectedClients = new ArrayList<>();

    public static void main(String[] args) {
        TcpChatServer s = new TcpChatServer();
        s.start();
    }

    private void start() {
        try {
            ServerSocket socket = new ServerSocket(8080);
            while (true) {
                Socket connectedClient = socket.accept();
                System.out.println("New client connected " + connectedClient);
                synchronized (connectedClients) {
                    connectedClients.add(connectedClient);
                }
                TcpServerUserThread t = new TcpServerUserThread(connectedClient, this);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastMessage(String message) {
        // TODO DU 3.11.2020 neposilat zpravu tomu, kdo ji odelal (puvodci)
        synchronized (connectedClients) {
            for (Socket s : connectedClients) {
                //if(s == 55)
                try {
                    OutputStream os = s.getOutputStream();
                    PrintWriter w = new PrintWriter(new OutputStreamWriter(os), true);
                    w.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
