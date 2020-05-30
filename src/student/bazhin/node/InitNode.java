package student.bazhin.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import student.bazhin.Core;
import student.bazhin.database.BaseModel;
import student.bazhin.database.FirebirdDatabase;
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

            try {
                database = new FirebirdDatabase();
                database.init(BaseModel.DATABASE_NAME);
                database.connect();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Ошибка создания потоков обмена данных");
            e.printStackTrace();
        }
        start();
    }

    public void run() {
        PocketData pocket;
        while ((pocket = waitPocket()) != null) {
            nodeId = pocket.getNodeId();
            switch (pocket.getHeader()) {
                case INIT: {
                    handleNodeId(BaseModel.getNodeType(database,READER_NODE_CODE));
                    if (respondToNode()) {
                        new ReaderNode(this);
                    } else {
                        handleFailConnection("Ошибка инициализации считывающего узла!");
                    }
                    return;
                }
                case CONNECT: {
                    handleNodeId(BaseModel.getNodeType(database,VIEWER_NODE_CODE));
                    if (respondToNode()) {
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

    protected void handleNodeId(int nodeType) {
        String ip = nodeSocket.getInetAddress().toString();
        String port = String.valueOf(nodeSocket.getPort());
        String date = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss.SSS").format(new Date());
        nodeId = nodeId.equals("") ? BaseModel.addNode(database,ip,port,date,nodeType) : BaseModel.updateNode(database,CONNECTED_LITERAL,nodeId,ip,port,date,nodeType);
    }

    private boolean respondToNode() {
        if (!nodeId.equals("")) {
            sendPocket(new PocketData(OK).setNodeId(nodeId));
            return true;
        }
        return false;
    }

    @Override
    protected void disconnect() {
        //todo логирование в бд
        BaseModel.removeNode(database,nodeId);
        super.disconnect();
    }

}
