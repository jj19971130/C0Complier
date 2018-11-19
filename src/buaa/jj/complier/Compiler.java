package buaa.jj.complier;

import buaa.jj.complier.LexicalAnalyse.LexicalAnalyzer;
import buaa.jj.complier.LexicalAnalyse.Token;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Compiler extends Application {

    LexicalAnalyzer lexicalAnalyzer;

    public char getChar() {
        return 'a';
    }

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Compiler.fxml"));
        Scene scene = new Scene(root,1024,576);
        primaryStage.setTitle("C0编译器");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
