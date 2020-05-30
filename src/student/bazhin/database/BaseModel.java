package student.bazhin.database;

import student.bazhin.node.ANode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseModel {

    public static final String DATABASE_NAME = "E:\\Inst\\NODE_SERVER_DB.FDB";

    public static int getNodeType(ADatabase db, String typeCode) {
        String sql = "SELECT id_type FROM node_type WHERE type_code = '" + typeCode + "'";
        return (int)db.executeSelect(sql).getValueAt(0,"ID_TYPE");
    }

    public static String addNode(ADatabase db, String ip, String port, String date, int nodeType) {
        String idNode = "";
        String sql = "INSERT INTO node (ip, port, date_last_conn, type) " +
                     "VALUES ('" + ip + "','" + port + "','" + date + "'," + nodeType + ")";
        if (db.executeQuery(sql) > 0) {
            sql = "SELECT id_node FROM node WHERE date_last_conn = '" + date + "'";
            idNode = String.valueOf(db.executeSelect(sql).getValueAt(0,"ID_NODE"));
        }
        return idNode;
    }

    public static void removeNode(ADatabase db, String nodeId, int nodeType) {
        String sql = "DELETE FROM node WHERE ID_NODE = " + nodeId + " AND type = " + nodeType;
        db.executeQuery(sql);
    }

    public static void removeNode(ADatabase db, String nodeId) {
        String sql = "DELETE FROM node WHERE ID_NODE = " + nodeId;
        db.executeQuery(sql);
    }

    public static String updateNode(ADatabase db, String isConnect, String nodeId, String ip, String port, String date, int nodeType) {
        ArrayList<String> sqlSet = new ArrayList<>();
        if (!ip.equals("")) {
            sqlSet.add("ip = '" + ip + "'");
        }
        if (!port.equals("")) {
            sqlSet.add("port = '" + port + "'");
        }
        if (!date.equals("")) {
            sqlSet.add("date_last_conn = '" + date + "'");
        }
        if (!isConnect.equals("")) {
            sqlSet.add("is_connect = '" + isConnect + "'");
        }
        String sql = "UPDATE node SET " + String.join(", ",sqlSet) + " WHERE id_node = " + nodeId + " AND type = " + nodeType;
        return (db.executeQuery(sql) > 0) ? nodeId : "";
    }

    public static HashMap<String,String> getSProjects(ADatabase db, String readerNodeId) {
        HashMap<String,String> sProjects = new HashMap<>();
        String sql = "SELECT id_sproject, name FROM sproject JOIN node " +
                     "ON sproject.id_reader_node = node.id_node " +
                     "AND is_connect = '" + ANode.CONNECTED_LITERAL + "' " +
                     "AND id_reader_node = " + readerNodeId;
        ADatabase.TableModel result = db.executeSelect(sql);
        for (int i = 0; i < result.getRowCount(); i++) {
            String idSProject = String.valueOf(result.getValueAt(i,"ID_SPROJECT"));
            String nameSProject = String.valueOf(result.getValueAt(i,"NAME"));
            if (!idSProject.equals("") && !nameSProject.equals("")) {
                sProjects.put(idSProject,nameSProject);
            }
        }
        return sProjects;
    }

    //todo может быть исключение из СУБД: неверный тип добавляемого узла
    public static void addSProjects(ADatabase db, String nodeId, HashMap<String, String> missingSProjects) {
        if ((missingSProjects != null) && (missingSProjects.size() > 0)) {
            String sql = "EXECUTE BLOCK AS BEGIN ";
            for (Map.Entry<String, String> sProject : missingSProjects.entrySet()) {
                String sProjectId = sProject.getKey();
                String sProjectName = sProject.getValue();
                sql += "INSERT INTO sproject VALUES (" + sProjectId + "," + nodeId + ",'" + sProjectName + "'); ";
            }
            sql += "END";
            db.executeQuery(sql);
        }
    }

    public static void removeSProjects(ADatabase db, String readerNodeId, ArrayList<String> sProjectsIds) {
        String sqlInArray = "", sqlInArrayNodeUnion = "", sqlInArraySProject = "";
        if ((sProjectsIds != null) && (sProjectsIds.size() > 0)) {
            for (String sProjectId: sProjectsIds) {
                sqlInArray += (sqlInArray.equals("")) ? sProjectId : ", " + sProjectId;
            }
            sqlInArrayNodeUnion = " AND sproject IN (" + sqlInArray + ")";
            sqlInArraySProject = " AND id_sproject IN (" + sqlInArray + ")";
        }
        String sql = "DELETE FROM node_union WHERE reader_node = " + readerNodeId + sqlInArrayNodeUnion;
        db.executeQuery(sql);
        sql = "DELETE FROM sproject WHERE id_reader_node = " + readerNodeId + sqlInArraySProject;
        db.executeQuery(sql);
    }

    public static ArrayList<String> getUnionViewerNodes(ADatabase db, String readerNodeId, String sProjectId) {
        ArrayList<String> viewerNodes = new ArrayList<>();
        String sql = "SELECT viewer_node FROM node_union WHERE reader_node = " + readerNodeId;
        if (!sProjectId.equals("")) {
            sql += " AND sproject = " + sProjectId;
        }
        ADatabase.TableModel result = db.executeSelect(sql);
        for (int i = 0; i < result.getRowCount(); i++) {
            String viewerNodeId = String.valueOf(result.getValueAt(i,"VIEWER_NODE"));
            if (!viewerNodeId.equals("")) {
                viewerNodes.add(viewerNodeId);
            }
        }
        return viewerNodes;
    }

    //todo может быть исключение из СУБД: неверный тип добавляемого узла
    public static boolean addNodeUnion(ADatabase db, String viewerNodeId, String readerNodeId, String sProjectId) {
        String sql = "INSERT INTO node_union " +
                     "(viewer_node,reader_node,sproject) " +
                     "SELECT " + viewerNodeId + " AS viewer_node, sproject.id_reader_node AS reader_node, sproject.id_sproject AS sproject " +
                     "FROM sproject JOIN node " +
                     "ON sproject.id_reader_node = node.id_node " +
                     "  AND is_connect = '" + ANode.CONNECTED_LITERAL + "' " +
                     "  AND id_reader_node = " + readerNodeId +
                     "WHERE id_sproject = " + sProjectId;
        return (db.executeQuery(sql) > 0);
    }

    public static void removeUnionsByReaderNode(ADatabase db, String readerNodeId) {
        String sql = "DELETE FROM node_union WHERE reader_node = " + readerNodeId;
        db.executeQuery(sql);
    }

    public static void removeUnionsByViewerNode(ADatabase db, String viewerNodeId) {
        String sql = "DELETE FROM node_union WHERE viewer_node = " + viewerNodeId;
        db.executeQuery(sql);
    }

    public static void prepareTables() throws SQLException, ClassNotFoundException {
        //todo выполнить очистку бд убрать узловые соединения, убрать узлы представления, установить n считывающим узлам
        ADatabase database = new FirebirdDatabase();
        database.init(DATABASE_NAME);
        database.connect();
        String sql =
            "EXECUTE BLOCK AS BEGIN " +
            "DELETE FROM node_union; " +
            "DELETE FROM sproject; " +
            "DELETE FROM node WHERE type = " + getNodeType(database,ANode.VIEWER_NODE_CODE) + "; " +
            "UPDATE node SET is_connect = '" + ANode.DISCONNECTED_LITERAL + "' ; " +
            "END";
        database.executeQuery(sql);
        database.release();
    }

}
