package student.bazhin.node;

import student.bazhin.database.ADatabase;
import student.bazhin.pocket.PocketData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class ANode extends Thread {

    String nodeId;
    Socket nodeSocket;
    ADatabase database;
    PrintWriter clientOut;
    BufferedReader clientIn;

    public static final String READER_NODE_CODE = "reader";
    public static final String VIEWER_NODE_CODE = "viewer";
    public static final String CONNECTED_LITERAL = "y";
    public static final String DISCONNECTED_LITERAL = "n";

    public ANode() {
    }

    public ANode(InitNode node) {
        nodeId = node.nodeId;
        database = node.database;
        clientIn = node.clientIn;
        clientOut = node.clientOut;
        nodeSocket = node.nodeSocket;
        start();
    }

    protected void disconnect() {
        System.out.println("Отключение клиента");
        try {
            clientIn.close();
            clientOut.close();
            nodeSocket.close();
            database.release();
        } catch (IOException e) {
            System.out.println("Ошибка закрытия потоков обмена данных");
            e.printStackTrace();
        }
    }

    protected PocketData waitPocket() {
        String line;
        PocketData pocketData = null;
        StringBuilder request = new StringBuilder();

        if (clientIn != null) {
            try {
                while (!(line = clientIn.readLine()).equals("")) {
                    request.append(line);
                }
                pocketData = new PocketData().setJson(request.toString());
            } catch (Exception e) {
                handleFailConnection("Ошибка передачи пакета!");
                e.printStackTrace();
            }
        }

        return pocketData;
    }

    public void sendPocket(PocketData pocket) {
        clientOut.println(pocket);
    }

    protected void handleFailConnection(String errMsg) {
        disconnect();
        if ((errMsg == null)) {
            System.out.println("Ошибка!");
        } else {
            System.out.println(errMsg);
        }
    }

}
