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
    final List<Socket> connectedClients = new ArrayList<Socket>();
    public static void main(String[] args) {
        System.out.println("TCP Server init");
        TcpChatServer s = new TcpChatServer();
        s.start();
    }
    public void start() {
        try {
            ServerSocket socket = new ServerSocket(5000);
            while (true) {
                Socket connectedClient = socket.accept();
                System.out.println("New connected client: " + connectedClient.getRemoteSocketAddress().toString());
                synchronized (connectedClients) {
                    connectedClients.add(connectedClient);
                }
                TcpServerUserThread t = new TcpServerUserThread(connectedClient,this);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastMessage(Socket sender, String message) {
        synchronized (connectedClients) {
            for (Socket connectedClient : connectedClients) {
                if(connectedClient == sender) {
                    continue;
                }
                try {
                    OutputStream os = connectedClient.getOutputStream();
                    PrintWriter w = new PrintWriter(new OutputStreamWriter(os),true);
                    w.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
