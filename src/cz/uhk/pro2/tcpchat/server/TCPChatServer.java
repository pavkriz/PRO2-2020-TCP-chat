package cz.uhk.pro2.tcpchat.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPChatServer implements MessageBroadcaster {

    final List<Socket> connectedClients = new ArrayList<>();

    public static void main(String[] args) {
        TCPChatServer s = new TCPChatServer();
        s.start();
    }

    private void start() {
        try {
            ServerSocket socket = new ServerSocket(5000);
            while (true) {
                Socket connectedClient = socket.accept();
                synchronized (connectedClients) { // v pripade rychleho provozu na serveru by nestihalo a zacalo blbnout - nutna synchronizace - pockej, az bude list upraven a pak teprve si ho vem
                    connectedClients.add(connectedClient);
                }
                System.out.println("New connected client +" + connectedClient); // jiz ted lze spustit a komunikovat př. přes aplikaci putty nebo telnet
                TCPServerUserThread t = new TCPServerUserThread(connectedClient, this); // alternativne misto this lambda vyraz m -> ...
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastMessage(String message) {
        //todo neposilat zpravu puvodci zpravy - socket - zdrojovy port - najit a vyjmout ho
        synchronized (connectedClients) {
            for (Socket s : connectedClients) {
                OutputStream os = null;
                try {
                    connectedClients.get(0).getPort(); // porty tech v seznamu
                    s.getPort(); // port toho socketu co prisel = port odesilatele a continue
                    if (s.getPort() == connectedClients.get(0).getPort())
                        continue;
                    os = s.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(os),true); // autoflush buffer - jinak by se to v nem ukladalo, ale nevypisovalo
                writer.println(message);
            }
        }
    }
}