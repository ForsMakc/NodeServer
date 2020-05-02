package student.bazhin.node;

import student.bazhin.database.ADataBase;
import student.bazhin.pocket.PocketData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class ANode extends Thread {

    String nodeId;
    Socket nodeSocket;
    ADataBase database;
    PrintWriter clientOut;
    BufferedReader clientIn;

    public ANode() {
        start();
    }

    public ANode(InitNode node) {
        database = node.database;
        clientIn = node.clientIn;
        clientOut = node.clientOut;
        nodeSocket = node.nodeSocket;
    }

    protected void disconnect() {
        System.out.println("Клиент ушёл");
        try {
            clientIn.close();
            clientOut.close();
            nodeSocket.close();
        } catch (IOException e) {
            System.out.println("Ошибка создания потоков обмена данных");
            e.printStackTrace();
        }
    }

    abstract protected PocketData waitPocket();

    public void sendPocket(PocketData pocket) throws Exception {
        clientOut.println(pocket);
    };

    abstract protected void handleFailSending();
}
