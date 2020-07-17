package main;

import javafx.stage.StageStyle;
import templator.Templator;
import data.ActionTask;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

public class Main extends Application {

    public static String outDir = "out/production/Scheduler/";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Templator actionTemplate = new Templator(new File(outDir + "data/ActionFXML_1.2.7.11.modt"));
        ActionTask testActionTask = new ActionTask(-1, actionTemplate);

        testActionTask.getActionDataJSON().put("actionTitle", "TestTitle");


        Parent root = testActionTask.load();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException, ParseException {

         launch(args);

        // testing methods

    }

}
