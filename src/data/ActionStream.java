package data;

import javafx.scene.layout.VBox;
import main.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ActionStream {

    String path = Main.outDir;

    ArrayList<ActionTask> subActionTask;
    File indexFile = new File(path + "data/actionStreamIndex.json");

    // TODO: Finish constructor. Should load all Actions that are specified by the Index
    public ActionStream(int ID) throws IOException {

        if (!indexFile.exists()) {
            BufferedWriter w = new BufferedWriter(new FileWriter(indexFile));
            w.write("{}");
            w.close();
        }


    }

    // TODO: Make this method load all actions into a VBox and return it
    public VBox loadActionStream() {

        return new VBox();
    }

    // TODO: Get a good way to sequence this.
    public void add(ActionTask a) {



    }

}
