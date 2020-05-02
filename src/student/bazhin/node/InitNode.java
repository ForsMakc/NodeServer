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
        //
        disconnect();
        return;
        //

        String ip = nodeSocket.getRemoteSocketAddress().toString();
        String port =  String.valueOf(nodeSocket.getPort());
        String date = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        PocketData pocket = waitPocket();
        nodeId = pocket.getNodeId();
        switch (pocket.getHeader()) {
            case INIT: {
                nodeId = nodeId.equals("") ? BaseModel.addReaderNode(ip,port,date) : BaseModel.updateReaderNode(nodeId,ip,port,date);
                if (!nodeId.equals("")) {
                    pocket.setHeader(OK);
                    pocket.setNodeId(nodeId);
                    try {
                        sendPocket(pocket);
                    } catch (Exception e) {
                        handleFailSending();
                        e.printStackTrace();
                        return;
                    }
                } else {
                    //todo стоит ли отправлять пакет с FAIL или узел и так отключиться?
                    disconnect();
                    System.out.println("Ошибка инициализации считывающего узла!");
                    return;
                }
                Core.pullNodeSockets.put(nodeId,new ReaderNode(this));
                break;
            }
            case CONNECT: {
                nodeId = nodeId.equals("") ? BaseModel.addViewerNode(ip,port,date) : BaseModel.updateViewerNode(nodeId,ip,port,date);
                if (!nodeId.equals("")) {
                    pocket.setHeader(OK);
                    pocket.setNodeId(nodeId);
                    try {
                        sendPocket(pocket);
                    } catch (Exception e) {
                        handleFailSending();
                        e.printStackTrace();
                        return;
                    }
                } else {
                    //todo стоит ли отправлять пакет с FAIL или узел и так отключиться?
                    disconnect();
                    System.out.println("Ошибка инициализации узла представления!");
                    return;
                }
                Core.pullNodeSockets.put(nodeId,new ViewerNode(this));
                break;
            }
        }
    }

    @Override
    protected PocketData waitPocket() {
        String line;
        PocketData pocketData = null;
        StringBuilder request = new StringBuilder();

        try {
            while (!(line = clientIn.readLine()).equals("")) {
                request.append(line);
            }
            pocketData = new PocketData().setJson(request.toString());
        } catch (IOException e) {
            disconnect();
            System.out.println("Ошибка чтения данных из сокета узла при инициализацим!");
            e.printStackTrace();
        }

        return pocketData;
    }

    @Override
    protected void handleFailSending() {
        disconnect();
        System.out.println("Ошибка передачи пакета при инициализации узла представления!");
    }

}
