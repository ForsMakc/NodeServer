package student.bazhin.node;
import student.bazhin.Core;
import student.bazhin.database.BaseModel;
import student.bazhin.pocket.PocketData;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static student.bazhin.helper.Constants.SPROJECT_ID_MAPKEY;
import static student.bazhin.helper.Constants.SPROJECT_NAME_MAPKEY;
import static student.bazhin.pocket.PocketHeaders.*;

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
                    sendPocket(new PocketData(OK));
                    String sProjectId = pocket.getMetaData().get(SPROJECT_ID_MAPKEY);
                    String sProjectName = pocket.getMetaData().get(SPROJECT_NAME_MAPKEY);
                    if (!sProjectName.equals("") && !sProjectId.equals("")) {
                        sProjects.put(sProjectId,sProjectName);
                    }
                    if (!nodeId.equals("") && !sProjectId.equals("")) {
                        ArrayList<String> viewerNodes = BaseModel.getUnionViewerNodes(database,nodeId,sProjectId);
                        for (String viewerNodeId : viewerNodes) {
                            try {
                                Core.pullViewerNode.get(viewerNodeId).sendPocket(pocket); //todo возможно будут проблемы извлечения из пула, если нет такого id узла представления
                            } catch (Exception ignored) {}
                        }
                    }
                    break;
                }
                case TEST: {
                    sendPocket(new PocketData(OK));
                    synchronizeSProjects(sProjects,BaseModel.getSProjects(database,nodeId));
                    break;
                }
            }
        }
    }

    protected void synchronizeSProjects(HashMap<String, String> sProjectsPocket, HashMap<String,String> sProjectsDb) {
        ArrayList<String> disposalSProjects = new ArrayList<>();
        HashMap<String,String> missingSProjects = new HashMap<>();

        if (sProjectsPocket.size() > sProjectsDb.size()) {
            for (Map.Entry<String, String> sProject: sProjectsDb.entrySet()) {
                String sProjectId = sProject.getKey();
                if (!sProjectsPocket.containsKey(sProjectId)) {
                    disposalSProjects.add(sProjectId);
                } else {
                    //после удаления идентичных элементов, в коллекции останутся только те элементы, которые принадлежат только этому множеству
                    sProjectsPocket.remove(sProjectId);
                }
            }
            missingSProjects = sProjectsPocket;
        } else {
            for (Map.Entry<String,String> sProject: sProjectsPocket.entrySet()) {
                String sProjectId = sProject.getKey();
                String sProjectName = sProject.getValue();
                if (!sProjectsDb.containsKey(sProjectId)) {
                    missingSProjects.put(sProjectId,sProjectName);
                } else {
                    //после удаления идентичных элементов, в коллекции останутся только те элементы, которые принадлежат только этому множеству
                    sProjectsDb.remove(sProjectId);
                }
            }
            disposalSProjects = new ArrayList<>(sProjectsDb.keySet());
        }

        if (disposalSProjects.size() > 0) {
            ArrayList<String> viewerNodes = BaseModel.getUnionViewerNodes(database,nodeId,"");
            for (String viewerNodeId : viewerNodes) {
                try {
                    Core.pullViewerNode.get(viewerNodeId).sendPocket(new PocketData(FAIL)); //todo возможно будут проблемы извлечения из пула, если нет такого id узла представления
                } catch (Exception ignored) {}
            }
            BaseModel.removeSProjects(database,nodeId,disposalSProjects);
        }
        if (missingSProjects.size() > 0) {
            BaseModel.addSProjects(database,nodeId,missingSProjects);
        }
        sProjectsPocket.clear();
    }

    @Override
    protected void disconnect() {
        ArrayList<String> viewerNodes = BaseModel.getUnionViewerNodes(database,nodeId,"");
        for (String viewerNodeId : viewerNodes) {
            try {
                Core.pullViewerNode.get(viewerNodeId).sendPocket(new PocketData(FAIL)); //todo возможно будут проблемы извлечения из пула, если нет такого id узла представления
            } catch (Exception ignored) {}
        }
        //todo логирование в бд
        BaseModel.removeSProjects(database,nodeId,null);
        BaseModel.updateNode(database,DISCONNECTED_LITERAL,nodeId,"","","",BaseModel.getNodeType(database,READER_NODE_CODE));
        super.disconnect();
    }

}
