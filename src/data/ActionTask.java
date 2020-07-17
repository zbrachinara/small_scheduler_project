package data;

import main.Main;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import templator.Templator;

@SuppressWarnings("unchecked")
public class ActionTask {

    String path = Main.outDir; // path to project

    JSONObject actionDataJSON;
    Templator actionTemplate;
    Properties lookProperties = new Properties();
    File lookPropertiesFile  = new File(path + "looks.properties");
    File targetFile;
    File actionDataFile;

    // variables stored in JSON
    /*
    "finished" | Stores whether or not the action has been completed
    "actionTitle" | Stores the action title
    "actionBody" | Stores the action description
    "estElapse" | Stores the estimated time which will be elapsed in the completion of the action
    "endTime" | an optional argument which can be left blank, stores the action absolute end time
     */

    // use this getter to set the action's data
    public JSONObject getActionDataJSON() {
        return actionDataJSON;
    }

    // Constructor : Loads the properties file and JSON file for the action data, and creates a new JSON if necessary
    public ActionTask(int ID, Templator actionTemplate) throws IOException, ParseException {

        actionDataFile = new File(path + "data/actions/"+ID+"/action_"+ID+".json"); // path to action JSON
        targetFile = new File(path + "data/actions/"+ID+"/action_"+ID+".fxml"); // path to output

        if (actionDataFile.exists()) { // load all values into a JSONObject

            // cast to JSONObject because parse method returns Object
            this.actionDataJSON = (JSONObject) new JSONParser().parse(new BufferedReader(new FileReader(actionDataFile)));

        } else {

            // Creating an object and loading it with all default values
            JSONObject jo = new JSONObject();
            jo.put("finished", false);
            jo.put("actionTitle", "");
            jo.put("actionBody", "");
            jo.put("estElapse", "");
            jo.put("endTime", "");

            // Creating the new file with defaults
            actionDataFile.createNewFile();
            BufferedWriter w = new BufferedWriter(new FileWriter(actionDataFile));
            w.write(jo.toJSONString());
            w.close();

            // assign the JSONObject
            this.actionDataJSON = jo;

        }

        if (lookPropertiesFile.exists()) {
            lookProperties.load(new BufferedReader(new FileReader(lookPropertiesFile)));
        } else {

            // default properties, may need to be updated
            lookProperties.setProperty("actionHeight", "80.0");
            lookProperties.setProperty("actionWidth", "140.0");

            // store the properties
            lookProperties.store(new BufferedWriter(new FileWriter(lookPropertiesFile)), "");

        }

        // load the templator
        this.actionTemplate = actionTemplate;

    }

    private HashMap<String, String> generateModVariables() {

        // TODO: Be able to accept a ruleset for generating these actions

        // modifiable variables in modt:
        /*
        height | height of entire action window
        width | width of entire action window
        statusBoxWidth | width of box representing completion of action
        statusLineWidth | width of line representing completion of action
        statusCircleRadius | radius of circle representing completion of action
        statusCircleTopInset | how far down the circle is from the top of the statusBox
        actionTitle | title of the action
        actionTitleFontSize | font size of action title
        actionBody | description of the action -- not a necessary field
        actionBodyFontSize | font size of action body
        */

        HashMap<String, String> modVariables = new HashMap<>();

        // getting variables from Properties file
        double height = Double.parseDouble(lookProperties.getProperty("actionHeight"));
        double width = Double.parseDouble(lookProperties.getProperty("actionWidth"));

        // setting height
        modVariables.put("height", lookProperties.getProperty("actionHeight"));

        // setting width
        modVariables.put("width", lookProperties.getProperty("actionWidth"));

        // setting statusBoxWidth
        modVariables.put("statusBoxWidth", Double.toString(width * 6 / 35));

        // setting statusLineWidth
        modVariables.put("statusLineWidth", Double.toString(width / 70));

        // setting statusCircleRadius
        modVariables.put("statusCircleRadius", Double.toString(width * 2 / 35));

        // setting statusCircleTopInset
        modVariables.put("statusCircleTopInset", Double.toString(height / 35));

        // setting actionTitle
        modVariables.put("actionTitle", (String) actionDataJSON.get("actionTitle"));

        // setting actionTitleFontSize
        // the font size should not be less than 10
        modVariables.put("actionTitleFontSize", Double.toString((height / 8 < 10) ? 10 : (height / 8)));

        // setting actionBody
        // does not necessarily exist, so there will be an option to check for that
        String body;
        if ((body = (String) actionDataJSON.get("actionData")) == null) {
            modVariables.put("actionData", "");
        } else {
            modVariables.put("actionData", body);
        }

        // setting actionBodyFontSize
        // the font size should be no less than 5
        modVariables.put("actionBodyFontSize", Double.toString((height / 16 < 5) ? 5 : height / 16));

        return modVariables;

    }

    // placeholder to circumvent current rules
    public HBox load(HashMap<String, String> modVariables) throws IOException {

        actionTemplate.template(modVariables, targetFile);
        return (HBox) FXMLLoader.load(new URL(targetFile.getAbsolutePath()));


    }

    public HBox load() throws IOException {

        return load(generateModVariables());

    }

}
