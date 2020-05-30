package student.bazhin.node;

import student.bazhin.Core;
import student.bazhin.database.BaseModel;
import student.bazhin.pocket.PocketData;
import student.bazhin.pocket.PocketHeaders;

import java.util.HashMap;

import static student.bazhin.helper.Constants.NODE_ID_MAPKEY;
import static student.bazhin.helper.Constants.SPROJECT_ID_MAPKEY;
import static student.bazhin.pocket.PocketHeaders.*;

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
            PocketHeaders header;
            try {
                header = pocket.getHeader();
                switch (header) {
                    case NODE: {
                        String readerNodeId = pocket.getMetaData().get(NODE_ID_MAPKEY);
                        HashMap<String, String> sProjects = BaseModel.getSProjects(database, readerNodeId);
                        if (sProjects.size() > 0) {
                            sendPocket(new PocketData(OK).setMetaData(sProjects));
                        } else {
                            sendPocket(new PocketData(FAIL));
                        }
                        break;
                    }
                    case PROJECT: {
                        String readerNodeId = pocket.getMetaData().get(NODE_ID_MAPKEY);
                        String sProjectId = pocket.getMetaData().get(SPROJECT_ID_MAPKEY);
                        if ((BaseModel.addNodeUnion(database, nodeId, readerNodeId, sProjectId))) {
                            sendPocket(new PocketData(OK));
                        } else {
                            sendPocket(new PocketData(FAIL));
                        }
                        break;
                    }
                    case TEST: {
                        sendPocket(new PocketData(OK));
                        break;
                    }
                    case CLOSE: {
                        disconnect();
                        break;
                    }
                }
            } catch (Exception e) {
                sendPocket(new PocketData(FAIL));
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

};
