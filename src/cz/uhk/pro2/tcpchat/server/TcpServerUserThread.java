package cz.uhk.pro2.tcpchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Thread that handles a single connected client
 */
public class TcpServerUserThread extends Thread {
    private final Socket connectedClientSocket;
    private final MessageBroadcaster broadcaster;
    private Date date;

    public TcpServerUserThread(Socket connectedClientSocket, MessageBroadcaster broadcaster) {
        this.connectedClientSocket = connectedClientSocket;
        this.broadcaster = broadcaster;
    }

    @Override
    public void run() {
        try {
            InputStream is = connectedClientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String message;
            String date = String.valueOf(new Date().getTime());
            while ((message  = reader.readLine()) != null) {
                // TODO DU 3.11.2020
                //    zprava "/time" od klienta -> odpovime mu, kolik je hodin
                //    zprava "/quit" od klienta -> ukoncime komunikaci s klientem (vyskocime z while cyklu)
                if(message.equals("/time")) {
                    System.out.println("Aktualní datum a čas: ");
                    Date dat = GregorianCalendar.getInstance().getTime();
                    DateFormat formCas = DateFormat.getTimeInstance();
                    broadcaster.broadcastMessage(formCas.format(dat));
                }
                else if(message.equals("/quit")) {
                    break;
                } else {
                    System.out.println("New message received: " + message + " " + connectedClientSocket);
                    broadcaster.broadcastMessage(message);
                }
            }
            System.out.println("UserThread ended " + connectedClientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
