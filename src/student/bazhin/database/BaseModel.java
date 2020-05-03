package student.bazhin.database;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseModel {

    public static String addReaderNode(String ip, String port, String date) {
        return "123";
    }

    public static String updateReaderNode(String nodeId, String ip, String port, String date) {
        return nodeId;
    }

    public static String addViewerNode(String ip, String port, String date) {
        return "123";
    }

    public static String updateViewerNode(String nodeId, String ip, String port, String date) {
        return nodeId;
    }

    public static ArrayList<String> getUnionViewerNodes(String nodeId, String sProjectId) {
        return null;
    }

    public static ArrayList<String> getUnionViewerNodes(String id) {
        return null;
    }

    public static void removeReaderNode(String nodeId) {
    }

    public static void removeReaderNodeSProjects(String nodeId) {
    }

    public static void removeUnionsByReaderNode(String nodeId) {
    }

    public static HashMap<String,String> getSProjects(String nodeId) {
        return null;
    }

    public static void addNodeUnion(String nodeId, String readerNodeId, String sProjectId) {
    }

    public static void synchronizeSProjects(HashMap<String, String> sProjects) {
    }

    public static void removeUnionsByViewerNode(String nodeId) {
    }

    public static void removeViewerNode(String nodeId) {
    }
}
