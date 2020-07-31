package data;

import javafx.scene.control.ScrollPane;
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

    int ID;

    String path = Main.outDir;
    String actionDir = path + "data/actions/";

    ArrayList<ActionTask> actionTasks = new ArrayList<>();
    ArrayList<Integer> actionIDs;
    JSONObject indexObject = new JSONObject();
    File indexFile = new File(path + "data/actionStreamIndex.json");
    Templator actionTemplate = new Templator(new File(path + "data/ActionFXML_1.2.7.11.modt"));

    public ActionStream(int ID) throws IOException, ParseException, java.text.ParseException {

        this.ID = ID;

        // Temporarily get the object storing all stream IDs
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

    public ScrollPane load() throws IOException {

        ScrollPane out = new ScrollPane();
        VBox actionTaskBox = new VBox();

        for (ActionTask i : actionTasks) {
            actionTaskBox.getChildren().add(i.load());
        }

        out.setContent(actionTaskBox);
        return out;

    }

    public void save() throws IOException {

        for (ActionTask action : actionTasks) {
            action.save();
        }

        BufferedWriter actionStreamWriter = new BufferedWriter(new FileWriter(indexFile));
        indexObject.replace(ID, actionIDs);
        actionStreamWriter.write(indexObject.toJSONString());

    }

    public void delete() throws IOException {

        purge();
        indexObject.remove(ID);
        save(); // to register changes to outside sources

    }

    public boolean add(ActionTask action) {
        return actionIDs.add(action.ID) & actionTasks.add(action);
    }

    public boolean add(int ID) throws IOException, ParseException {
        return add(new ActionTask(ID, actionTemplate));
    }

    public boolean addAll(ActionTask ... actions) {
        boolean out = true;
        for (ActionTask i: actions) {
            if (add(i)) {
                out = false;
            }
        }
        return out;
    }

    public boolean addAll(int ... IDs) throws IOException, ParseException {
        ActionTask[] tasks = new ActionTask[IDs.length];
        for (int i = 0; i < IDs.length; i++) {
            tasks[i] = new ActionTask(IDs[i], actionTemplate);
        }
        return addAll(tasks);
    }

    public boolean remove(ActionTask action) {
        return actionTasks.remove(action) & actionIDs.remove(Integer.valueOf(action.ID));
    }

    public boolean remove(int ID) throws IOException, ParseException {
        return remove(new ActionTask(ID, actionTemplate));
    }

    public boolean purge() throws IOException {
        boolean out = true;
        for (ActionTask action : actionTasks) {
            if (action.delete()) {
                out = false;
            }
        }
        return out;
    }

}
