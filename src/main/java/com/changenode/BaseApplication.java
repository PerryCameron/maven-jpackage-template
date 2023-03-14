package com.changenode;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BaseApplication extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage = primaryStage;
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(new Controller().getViewBuilder(),800, 600));
        primaryStage.show();
    }

    public static Stage getMainStage() {
        return primaryStage;
    }



//

//
//        statusLabel.setText("Ready.");
//
//        stage.show();
//        //put window to front to avoid it to be hide behind other.
//        stage.setAlwaysOnTop(true);
//        stage.requestFocus();
//        stage.toFront();
//        stage.setAlwaysOnTop(false);
//    }


}
