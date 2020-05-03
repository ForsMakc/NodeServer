package student.bazhin.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import student.bazhin.Core;
import student.bazhin.database.BaseModel;
import student.bazhin.pocket.PocketData;

import static student.bazhin.pocket.PocketHeaders.OK;

public class InitNode extends ANode {

    public InitNode(Socket nodeSocket) {
        this.nodeSocket = nodeSocket;
        System.out.println("Клиент пришёл.");
        try {
            clientOut = new PrintWriter(nodeSocket.getOutputStream(),true);
            System.out.println("Поток записи создан.");
            clientIn = new BufferedReader(new InputStreamReader(nodeSocket.getInputStream()));
            System.out.println("Поток чтения создан.");
        } catch (IOException e) {
            System.out.println("Ошибка создания потоков обмена данных");
            e.printStackTrace();
        }
        start();
    }

    public void run() {
        PocketData pocket;
        String ip = nodeSocket.getInetAddress().toString();
        String port = String.valueOf(nodeSocket.getPort());
        String date = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new Date());

        while ((pocket = waitPocket()) != null) {
            nodeId = pocket.getNodeId();
            switch (pocket.getHeader()) {
                case INIT: {
                    nodeId = nodeId.equals("") ? BaseModel.addReaderNode(ip, port, date) : BaseModel.updateReaderNode(nodeId, ip, port, date);
                    if (respondToNode(nodeId)) {
                        new ReaderNode(this);
                    } else {
                        handleFailConnection("Ошибка инициализации считывающего узла!");
                    }
                    return;
                }
                case CONNECT: {
                    nodeId = nodeId.equals("") ? BaseModel.addViewerNode(ip, port, date) : BaseModel.updateViewerNode(nodeId, ip, port, date);
                    if (respondToNode(nodeId)) {
                        Core.pullViewerNode.put(nodeId,new ViewerNode(this));
                    } else {
                        handleFailConnection("Ошибка инициализации узла представления!");
                    }
                    return;
                }
                case TEST: {
                    sendPocket(new PocketData(OK));
                    break;
                }
            }
        }
    }

    private boolean respondToNode(String nodeId) {
        if (!nodeId.equals("")) {
            sendPocket(new PocketData(OK).setNodeId(nodeId));
            return true;
        }
        return false;
    }

}
