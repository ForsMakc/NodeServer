package student.bazhin;

import student.bazhin.node.ANode;
import student.bazhin.node.InitNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

//todo если клиент закрывается, то и сервер вылетает
public class Core {

    public static ConcurrentHashMap<String,ANode> pullNodeSockets = new ConcurrentHashMap<>();

    public void perform() {
        try (ServerSocket server = new ServerSocket(3345)){
            while (true) {
                new InitNode(server.accept());
            }
        } catch (IOException e) {
            System.out.println("Соединение прервалось");
            e.printStackTrace();
        }
    }

}



//        while (true) {
//            if(pocketData.getHeader()==PocketHeaders.DATA){
//            pocketData.setHeader(PocketHeaders.OK);
//    //                    byte[] buffer = Base64.getDecoder().decode(pocketData.getBinaryFrame());
//    //                    File targetFile = new File("C:\\Users\\Fors\\Desktop\\test.jpg");
//    //                    OutputStream outStream = new FileOutputStream(targetFile);
//    //                    outStream.write(buffer);
//    //                    outStream.flush();
//    //                    outStream.close();
//            }
//
//            clientOut.println(pocketData);
//        }
