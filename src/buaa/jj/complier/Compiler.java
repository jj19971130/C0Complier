package buaa.jj.complier;

import buaa.jj.complier.LexicalAnalyse.LexicalAnalyzer;
import buaa.jj.complier.LexicalAnalyse.Token;
import javafx.application.Application;
import javafx.event.ActionEvent;
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
import java.util.LinkedList;
import java.util.Queue;

public class Compiler extends Application {

    LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(this);

    private File currentFile;
    private int i;
    private Queue<Token> tokens;

    @FXML
    Button button_open;

    @FXML
    Button button_save;

    @FXML
    TextArea code;

    @FXML
    TextArea compileInformation;

    @FXML
    TextField lineColumn;

    public char getChar() {
        int n = code.getLength();
        if (i >= n) {
            i++;
            return 27;
        }
        return code.getText().charAt(i++);
    }

    private String buildString() {
        StringBuilder s = new StringBuilder();
        while (!tokens.isEmpty()) {
            Token token = tokens.poll();
            switch (token.type) {
                case Identifier:
                    s.append(token.name + ' ');
                    s.append("关键字 ");
                    s.append(token.name + ' ');
                    break;
                case Constant:
                    s.append(token.value);
                    s.append(' ');
                    s.append("常数 ");
                    s.append(Integer.toBinaryString(token.value));
                    break;
                case String:
                    s.append(token.s + ' ');
                    s.append("字符串 ");
                    s.append(token.s);
                    break;
                case If:
                    s.append("if ");
                    s.append("关键字 ");
                    s.append("if ");
                    break;
                case Else:
                    s.append("else ");
                    s.append("关键字 ");
                    s.append("else ");
                    break;
                case Int:
                    s.append("int ");
                    s.append("关键字 ");
                    s.append("int ");
                    break;
                case Main:
                    s.append("main ");
                    s.append("关键字 ");
                    s.append("main ");
                    break;
                case Void:
                    s.append("void ");
                    s.append("关键字 ");
                    s.append("void ");
                    break;
                case Const:
                    s.append("const ");
                    s.append("关键字 ");
                    s.append("const ");
                    break;
                case Scanf:
                    s.append("scanf ");
                    s.append("关键字 ");
                    s.append("scanf ");
                    break;
                case While:
                    s.append("printf ");
                    s.append("关键字 ");
                    s.append("printf ");
                    break;
                case Printf:
                    s.append("printf ");
                    s.append("关键字 ");
                    s.append("printf ");
                    break;
                case Return:
                    s.append("return ");
                    s.append("关键字 ");
                    s.append("return ");
                    break;
                case Add:
                    s.append("+ ");
                    s.append("运算符 ");
                    s.append("+ ");
                    break;
                case Sub:
                    s.append("- ");
                    s.append("运算符 ");
                    s.append("- ");
                    break;
                case Mult:
                    s.append("* ");
                    s.append("运算符 ");
                    s.append("* ");
                    break;
                case Div:
                    s.append("/ ");
                    s.append("运算符 ");
                    s.append("/ ");
                    break;
                case Less:
                    s.append("< ");
                    s.append("运算符 ");
                    s.append("< ");
                    break;
                case LessEqual:
                    s.append("<= ");
                    s.append("运算符 ");
                    s.append("<= ");
                    break;
                case More:
                    s.append("> ");
                    s.append("运算符 ");
                    s.append("> ");
                    break;
                case MoreEqual:
                    s.append(">= ");
                    s.append("运算符 ");
                    s.append(">= ");
                    break;
                case Equal:
                    s.append("== ");
                    s.append("运算符 ");
                    s.append("== ");
                    break;
                case NotEqual:
                    s.append("!= ");
                    s.append("运算符 ");
                    s.append("!= ");
                    break;
                case Assignment:
                    s.append("= ");
                    s.append("运算符 ");
                    s.append("= ");
                    break;
                case Comma:
                    s.append(", ");
                    s.append("分隔符 ");
                    s.append(", ");
                    break;
                case Semicolon:
                    s.append("; ");
                    s.append("分隔符 ");
                    s.append("; ");
                    break;
                case LeftL:
                    s.append("( ");
                    s.append("分隔符 ");
                    s.append("( ");
                    break;
                case RightL:
                    s.append(") ");
                    s.append("分隔符 ");
                    s.append(") ");
                    break;
                case LeftM:
                    s.append("[ ");
                    s.append("分隔符 ");
                    s.append("[ ");
                    break;
                case RightM:
                    s.append("] ");
                    s.append("分隔符 ");
                    s.append("] ");
                    break;
                case LeftB:
                    s.append("{ ");
                    s.append("分隔符 ");
                    s.append("{ ");
                    break;
                case RightB:
                    s.append("} ");
                    s.append("分隔符 ");
                    s.append("} ");
                    break;
            }
            s.append('\n');
        }
        return s.toString();
    }

    public static void main(String args[]) {
        launch(args);
    }

    public void onButtonOpenClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开文件");
        currentFile = fileChooser.showOpenDialog(null);
        if (currentFile != null) {
            code.setText("");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(currentFile));
                String s = reader.readLine();
                while (s != null) {
                    code.setText(code.getText(0,code.getLength()) + s + "\n");
                    s = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onButtonSaveClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存文件");
        currentFile = fileChooser.showSaveDialog(null);
        if (currentFile != null) {
            try {
                if (!currentFile.exists())
                    currentFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
                writer.write(code.getText(),0,code.getLength());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateLineColumnNum(Event event) {
        int i = code.getAnchor();
        String s = code.getText(0,i);
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

    public void onButtonCompileClicked(MouseEvent event) {
        compileInformation.setText("");
        tokens = new LinkedList<Token>();
        i = 0;
        if (code.getLength() != 0) {
            lexicalAnalyzer.clearState();
            int lenth = code.getLength();
            do {
                try {
                    tokens.add(lexicalAnalyzer.getToken());
                } catch (Exception e) {
                    String s = code.getText(0,i - 1);
                    int num = -1, a = 1;
                    while ((num = s.indexOf('\n',num + 1)) != -1) {
                        a++;
                    }
                    int last = s.lastIndexOf('\n');
                    int b = s.length() - (last == -1 ? 0 : last + 1);
                    compileInformation.setText(compileInformation.getText() + a + "行" + b + "列处出现词法错误\n");
                }
            } while (i <= lenth);
            compileInformation.setText(compileInformation.getText() + buildString());
        }
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
