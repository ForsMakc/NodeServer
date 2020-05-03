package student.bazhin.pocket;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import static student.bazhin.helper.Constants.NODE_ID_MAPKEY;
import static student.bazhin.pocket.PocketHeaders.DATA;

public class PocketData {

    protected PocketScheme pocket;
    protected String nl = System.getProperty("line.separator");

    public PocketData() {
        pocket = new PocketScheme();
    }

    public PocketData(PocketHeaders header) {
        pocket = new PocketScheme();
        setHeader(header);
    }

    public PocketData(String jsonPocket) {
        pocket = new Gson().fromJson(jsonPocket,PocketScheme.class);
    }

    public PocketData setHeader(PocketHeaders header) {
        pocket.header = header;
        return this;
    }

    public PocketHeaders getHeader() {
        return pocket.header;
    }

    public String getNodeId() {
        return (pocket.metaData == null) ? "" : pocket.metaData.get(NODE_ID_MAPKEY);
    }

    public PocketData setNodeId(String nodeId) {
        if (pocket.metaData == null) {
            pocket.metaData = new HashMap<>();
        }
        if (pocket.metaData.containsKey(NODE_ID_MAPKEY)) {
            pocket.metaData.replace(NODE_ID_MAPKEY,nodeId);
        } else {
            pocket.metaData.put(NODE_ID_MAPKEY,nodeId);
        }
        return this;
    }

    public PocketData setMetaData(HashMap<String,String> metaData) {
        if (pocket.header != null) {
            pocket.metaData = metaData;
        }
        return this;
    }


    public HashMap<String, String> getMetaData() {
        return pocket.metaData;
    }

    public PocketData setStructData(ArrayList<String> structData) {
        if ((pocket.header != null) && (pocket.header == DATA)) {
            pocket.structData = structData;
        }
        return this;
    }

    public ArrayList<String> getStructData() {
        return pocket.structData;
    }

    public PocketData setValuesData(HashMap<String,ArrayList<HashMap<String,String>>> valuesData) {
        if ((pocket.header != null) && (pocket.header == DATA)) {
            pocket.valuesData = valuesData;
        }
        return this;
    }

    public HashMap<String,ArrayList<HashMap<String,String>>> getValuesData() {
        return pocket.valuesData;
    }

    public PocketData setKeysMapData(HashMap<String,String> keysMapData) {
        if ((pocket.header != null) && (pocket.header == DATA)) {
            pocket.keysMapData = keysMapData;
        }
        return this;
    }

    public HashMap<String, String> getKeysMapData() {
        return pocket.keysMapData;
    }

    public PocketData setBinaryFrame(String frame) {
        if ((pocket.header != null) && (pocket.header == DATA)) {
            pocket.binaryData.frame = frame;
        }
        return this;
    }

    public String getBinaryFrame() {
        return pocket.binaryData.frame;
    }

    public PocketData setBinaryResources(HashMap<String,String> resources) {
        if ((pocket.header != null) && (pocket.header == DATA)) {
            pocket.binaryData.resources = resources;
        }
        return this;
    }

    public HashMap<String,String> getBinaryResources() {
        return pocket.binaryData.resources;
    }

    public PocketData clear() {
        pocket = new PocketScheme();
        return this;
    }

    public PocketData setJson(String jsonPocket) {
        pocket = new Gson().fromJson(jsonPocket,PocketScheme.class);
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(pocket) + nl;
    }

    public class PocketScheme {
        PocketHeaders header = null;
        ArrayList<String> structData = null;
        HashMap<String,String> metaData = null;
        HashMap<String,ArrayList<HashMap<String,String>>> valuesData = null;
        HashMap<String,String> keysMapData = null;
        BinaryScheme binaryData = new BinaryScheme();

        public class BinaryScheme {
            String frame = null;
            HashMap<String,String> resources = null;
        }
    }

}
