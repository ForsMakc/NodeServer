package student.bazhin.node;


import student.bazhin.Core;
import student.bazhin.database.BaseModel;
import student.bazhin.pocket.PocketData;

import java.util.ArrayList;
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
                    String nodeId = pocket.getMetaData().get(NODE_ID_MAPKEY);
                    HashMap<String,String> sProjects = BaseModel.getSProjects(nodeId);
                    pocket.setMetaData(sProjects);
                    sendPocket(pocket);
                    break;
                }
                case PROJECT: {
                    String readerNodeId = pocket.getMetaData().get(NODE_ID_MAPKEY);
                    String sProjectId = pocket.getMetaData().get(SPROJECT_ID_MAPKEY);
                    BaseModel.addNodeUnion(nodeId,readerNodeId,sProjectId);
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
        super.disconnect();
        BaseModel.removeUnionsByViewerNode(nodeId);
        BaseModel.removeViewerNode(nodeId);
    }

}
