package student.bazhin.node;
import student.bazhin.Core;
import student.bazhin.database.BaseModel;
import student.bazhin.pocket.PocketData;


import java.util.ArrayList;
import java.util.HashMap;

import static student.bazhin.helper.Constants.SPROJECT_ID_MAPKEY;
import static student.bazhin.helper.Constants.SPROJECT_NAME_MAPKEY;
import static student.bazhin.pocket.PocketHeaders.CLOSE;
import static student.bazhin.pocket.PocketHeaders.OK;

public class ReaderNode extends ANode {

    protected ReaderNode() {
        super();
    }

    public ReaderNode(InitNode node) {
        super(node);
    }

    public void run() {
        PocketData pocket;
        HashMap<String,String> sProjects = new HashMap<>();
        while ((pocket = waitPocket()) != null) {
            switch (pocket.getHeader()) {
                case DATA: {
                    String sProjectId = pocket.getMetaData().get(SPROJECT_ID_MAPKEY);
                    String sProjectName = pocket.getMetaData().get(SPROJECT_NAME_MAPKEY);
                    if (!sProjectName.equals("") && !sProjectId.equals("")) {
                        sProjects.put(sProjectId,sProjectName);
                    }
                    if (!nodeId.equals("") && !sProjectId.equals("")) {
                        ArrayList<String> viewerNodes = BaseModel.getUnionViewerNodes(nodeId,sProjectId);
                        if (viewerNodes != null) {
                            for (String viewerNodeId : viewerNodes) {
                                try {
                                    Core.pullViewerNode.get(viewerNodeId).sendPocket(pocket);
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                    break;
                }
                case TEST: {
                    sendPocket(new PocketData(OK));
                    if (sProjects.size() != 0) {
                        BaseModel.synchronizeSProjects(sProjects);
                        sProjects.clear();
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void disconnect() {
        super.disconnect();
        ArrayList<String> viewerNodes = BaseModel.getUnionViewerNodes(nodeId);
        if (viewerNodes != null) {
            for (String viewerNodeId : viewerNodes) {
                try {
                    Core.pullViewerNode.get(viewerNodeId).sendPocket(new PocketData(CLOSE));
                } catch (Exception ignored) {}
            }
        }
        BaseModel.removeUnionsByReaderNode(nodeId);
        BaseModel.removeReaderNodeSProjects(nodeId);
        BaseModel.removeReaderNode(nodeId);
    }
}


//byte[] buffer = Base64.getDecoder().decode(pocketData.getBinaryFrame());
//File targetFile = new File("C:\\Users\\Fors\\Desktop\\test.jpg");
//OutputStream outStream = new FileOutputStream(targetFile);
//outStream.write(buffer);
//outStream.flush();
//outStream.close();
