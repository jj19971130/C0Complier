package buaa.jj.complier;

import buaa.jj.complier.Events.CompileInformationEvent;
import buaa.jj.complier.Events.EOFEvent;
import buaa.jj.complier.LexicalAnalyse.LexicalAnalyzer;
import buaa.jj.complier.LexicalAnalyse.Token;
import buaa.jj.complier.SyntaxAnalysis.SyntaxAnalyzer;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.EventListener;
import java.util.Queue;

public class Compiler extends Application implements EventListener {

    private LexicalAnalyzer lexicalAnalyzer;
    private SyntaxAnalyzer syntaxAnalyzer;

    private File currentFile;
    private int i;
    private int errorNum;
    private String code;
    private boolean finish;
    private boolean started;

    public static Logger logger = new Logger();

    @FXML
    Button button_open;

    @FXML
    Button button_save;

    @FXML
    TextArea codearea;

    @FXML
    TextArea compileInformation;

    @FXML
    TextField lineColumn;

    public char getChar() {
        int n = code.length();
        if (i >= n) {
            lexicalAnalyzer.handleEOFEvent(new EOFEvent(this));
            return 27;
        }
        return code.charAt(i++);
    }

    public int[] getPos() {
        return getLineColumnNum(i - 1);
    }

    public synchronized void handleCompileInformationEvent(CompileInformationEvent event) {
        if (event.getError()) {
            errorNum++;
        }
        if (errorNum >= 20) {
            lexicalAnalyzer.interrupt();
            syntaxAnalyzer.interrupt();
        }
        compileInformation.setText(compileInformation.getText() + event.getMessage() + '\n');
        if (event.getMessage().equals("语法分析结束") && errorNum == 0) {
            finish = true;
        }
    }

    public static void main(String args[]) {
        System.setOut(logger);
        launch(args);
    }

    //打开文件
    public void onButtonOpenClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开文件");
        currentFile = fileChooser.showOpenDialog(null);
        if (currentFile != null) {
            codearea.setText("");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(currentFile));
                String s = reader.readLine();
                while (s != null) {
                    codearea.setText(codearea.getText(0,codearea.getLength()) + s + "\n");
                    s = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //保存文件
    public void onButtonSaveClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存文件");
        currentFile = fileChooser.showSaveDialog(null);
        if (currentFile != null) {
            try {
                if (!currentFile.exists())
                    currentFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
                writer.write(codearea.getText(),0,codearea.getLength());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //更新行列数
    public void updateLineColumnNum(Event event) {
        int i = codearea.getAnchor();
        String s = codearea.getText(0,i);
        int num = -1, a = 1;
        while ((num = s.indexOf('\n',num + 1)) != -1) {
            a++;
        }
        int b = s.length() - s.lastIndexOf('\n');
        StringBuilder sb = new StringBuilder();
        sb.append(a);
        sb.append(":");
        sb.append(b);
        lineColumn.setText(sb.toString());
    }

    private int[] getLineColumnNum(int i) {
        int[] ret = new int[2];
        String s = code.substring(0,i);
        int num = -1, a = 1;
        while ((num = s.indexOf('\n',num + 1)) != -1) {
            a++;
        }
        int b = s.length() - s.lastIndexOf('\n');
        ret[0] = a;
        ret[1] = b;
        return ret;
    }

    //开始编译
    public void onButtonCompileClicked(MouseEvent event) throws InterruptedException {
        lexicalAnalyzer = new LexicalAnalyzer(this);
        syntaxAnalyzer = lexicalAnalyzer.syntaxAnalyzer;
        //清空编译信息
        compileInformation.setText("");
        i = 0;
        errorNum = 0;
        if (codearea.getLength() != 0) {
            //初始化词法分析器
            lexicalAnalyzer.clearState();
            logger.clearState();
            code = codearea.getText();
            lexicalAnalyzer.start();
            syntaxAnalyzer.start();
            while (true) {
                if (errorNum > 0) {
                    break;
                }
                if (!finish) {
                    Thread.sleep(100);
                    continue;
                }
                finish = false;
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("保存P-code");
                currentFile = fileChooser.showSaveDialog(null);
                if (currentFile != null) {
                    try {
                        if (!currentFile.exists())
                            currentFile.createNewFile();
                        BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
                        writer.write(logger.finalCode.toString());
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    public void onKeyboardTyped(KeyEvent event) {

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
