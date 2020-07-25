package data;

import javafx.scene.layout.VBox;
import main.Main;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import templator.Templator;

import java.io.*;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class ActionStream {

    String path = Main.outDir;
    String actionDir = path + "data/actions/";

    ArrayList<ActionTask> actionTasks;
    ArrayList<Integer> actionIDs;
    File indexFile = new File(path + "data/actionStreamIndex.json");
    Templator actionTemplate = new Templator(new File(path + "data/actions/"));

    public ActionStream(int ID) throws IOException, ParseException, java.text.ParseException {

        // Temporarily get the object storing all stream IDs
        JSONObject indexObject = new JSONObject();
        if (indexFile.exists()) {
            indexObject = (JSONObject) new JSONParser().parse(new BufferedReader(new FileReader(indexFile)));
        }

        // Get the array storing all ActionTask IDs bound to this stream
        actionIDs = (JSONArray) indexObject.get(ID);
        if (actionIDs == null) {
            actionIDs = new JSONArray();
            indexObject.put(ID, actionIDs);
        }

        // Load all actionTask classes into an Array
        for (int actionID : actionIDs) {
            actionTasks.add(new ActionTask(actionID, actionTemplate));
        }

    }

    // TODO: Make this method load all actions into a VBox and return it
    public VBox load() throws IOException {

        VBox out = new VBox();

        for (ActionTask i : actionTasks) {
            out.getChildren().add(i.load());
        }

        return out;

    }

}
