package student.bazhin.node;

import student.bazhin.Core;
import student.bazhin.database.BaseModel;
import student.bazhin.pocket.PocketData;

import java.util.HashMap;

import static student.bazhin.helper.Constants.NODE_ID_MAPKEY;
import static student.bazhin.helper.Constants.SPROJECT_ID_MAPKEY;
import static student.bazhin.pocket.PocketHeaders.CLOSE;
import static student.bazhin.pocket.PocketHeaders.OK;

public class ViewerNode extends ANode {

    protected ViewerNode() {
        super();
    }

    public ViewerNode(InitNode node) {
        super(node);
    }

    public void run() {
        PocketData pocket;
        while ((pocket = waitPocket()) != null) {
            switch (pocket.getHeader()) {
                case NODE: {
                    String readerNodeId = pocket.getMetaData().get(NODE_ID_MAPKEY);
                    HashMap<String,String> sProjects = BaseModel.getSProjects(database,readerNodeId);
                    if (sProjects.size() > 0) {
                        pocket.setMetaData(sProjects);
                        sendPocket(pocket);
                    } else {
                        sendPocket(new PocketData(CLOSE));
                    }
                    break;
                }
                case PROJECT: {
                    String readerNodeId = pocket.getMetaData().get(NODE_ID_MAPKEY);
                    String sProjectId = pocket.getMetaData().get(SPROJECT_ID_MAPKEY);
                    if ((BaseModel.addNodeUnion(database,nodeId,readerNodeId,sProjectId))) {
                        sendPocket(new PocketData(OK));
                    } else {
                        sendPocket(new PocketData(CLOSE));
                    }
                    break;
                }
                case TEST: {
                    sendPocket(new PocketData(OK));
                    break;
                }
            }
        }
    }

    @Override
    protected void disconnect() {
        //todo логирование в бд
        BaseModel.removeUnionsByViewerNode(database,nodeId);
        BaseModel.removeNode(database,nodeId,BaseModel.getNodeType(database,VIEWER_NODE_CODE));
        Core.pullViewerNode.remove(nodeId);
        super.disconnect();
    }

}


//byte[] buffer = Base64.getDecoder().decode(pocketData.getBinaryFrame());
//File targetFile = new File("C:\\Users\\Fors\\Desktop\\test.jpg");
//OutputStream outStream = new FileOutputStream(targetFile);
//outStream.write(buffer);
//outStream.flush();
//outStream.close();
