package cz.uhk.pro2.tcpchat.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TcpChatServer implements MessageBroacaster {
    List<Socket> activeConnections = new ArrayList<>();
    Socket clientConnect = null;

    public static void main(String[] args) {
        TcpChatServer s = new TcpChatServer();
        s.start();
    }

    private void start() {
        try {
            ServerSocket socket = new ServerSocket(5000);

            while (true) {
                clientConnect = socket.accept();
                System.out.println("New client " + clientConnect);
                TcpServerUserThread t = new TcpServerUserThread(clientConnect, this);
                synchronized (activeConnections) {
                    activeConnections.add(clientConnect);
                }
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastMessage(String message, Socket connectedClientSocket) {
        for (Socket sc : activeConnections) {
            if (sc != connectedClientSocket && !sc.isClosed()) {
                try {
                    OutputStream os = sc.getOutputStream();
                    PrintWriter w = new PrintWriter(new OutputStreamWriter(os), true);
                    w.println(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
