package student.bazhin;

import student.bazhin.database.BaseModel;
import student.bazhin.node.ANode;
import student.bazhin.node.InitNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class Core {

    public static ConcurrentHashMap<String,ANode> pullViewerNode = new ConcurrentHashMap<>();

    public void perform() {
        try {
            BaseModel.prepareTables();
            try (ServerSocket server = new ServerSocket(3345)){
                while (true) {
                    new InitNode(server.accept());
                }
            } catch (IOException e) {
                System.out.println("Соединение прервалось");
                e.printStackTrace();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
