package main;

import data.ActionStream;
import javafx.stage.StageStyle;
import templator.Templator;
import data.ActionTask;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class Main extends Application {

    public static String outDir = "out/production/Scheduler/";

    @Override
    public void start(Stage primaryStage) throws IOException, ParseException, org.json.simple.parser.ParseException {
        Templator actionTemplate = new Templator(new File(outDir + "data/ActionFXML_1.2.7.11.modt"));

        ActionTask[] testTasks = new ActionTask[5];

        for (int i = 0; i < 5; i++) {
            testTasks[i] = new ActionTask(-i, actionTemplate);
            testTasks[i].getActionData().put("actionTitle", "Action " + i);
            testTasks[i].save();
        }
        ActionStream testStream = new ActionStream(-1);
        testStream.addAll(testTasks);

        Parent root = testStream.load();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {

         launch(args);

    }

}
