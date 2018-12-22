package buaa.jj.complier.LexicalAnalyse;

import buaa.jj.complier.Compiler;
import buaa.jj.complier.Events.CompileInformationEvent;
import buaa.jj.complier.Events.EOFEvent;
import buaa.jj.complier.SyntaxAnalysis.SyntaxAnalyzer;

import java.util.ArrayDeque;
import java.util.Queue;

import static java.lang.Math.pow;

public class LexicalAnalyzer extends Thread {

    private char c;
    private boolean eof = false;
    private boolean finish = false;
    public Compiler compiler;
    private Queue<Token> tokens = new ArrayDeque<Token>();
    public SyntaxAnalyzer syntaxAnalyzer;

    public LexicalAnalyzer(Compiler compiler) {
        this.compiler = compiler;
        syntaxAnalyzer = new SyntaxAnalyzer(this);
    }

    private boolean checkLetter(int a, int b) {
        return c >= a && c <=b;
    }

    private boolean isLetter() {
        return checkLetter(65, 90) || checkLetter(97, 122);
    }

    private boolean isNumber() {
        return checkLetter(48, 57);
    }

    private void getChar() {
        c = compiler.getChar();
        if (c == 27)
            eof = true;
    }

    public void clearState() {
        eof = false;
        finish = false;
    }

    public Token getToken() throws InterruptedException {
        while (tokens.isEmpty()) {
            if (finish) {
                syntaxAnalyzer.handleEOFEvent(new EOFEvent(this));
                Token token = new Token();
                int[] xy = compiler.getPos();
                token.x = xy[0];
                token.y = xy[1] + 1;
                return token;
            }
            Thread.sleep(300);
        }
        return tokens.poll();
    }

    @Override
    public void run() {
        getChar();
        while (!eof) {
            Token token = new Token();
            while (c == ' ' || c == '\t' || c == '\n')
                getChar();
            int[] xy = compiler.getPos();
            token.x = xy[0];
            token.y = xy[1];
            if (checkLetter(49, 57)) {
                int tmp = 0;
                do {
                    tmp *= 10;
                    tmp += c - 48;
                    getChar();
                } while (isNumber() && !eof);
                token.type = Token.Type.Constant;
                token.value = tmp;
            } else if (c == '0') {
                double tmp = 0;
                getChar();
                if (c == '.') {
                    getChar();
                    if (isNumber()) {
                        int i = 1;
                        do {
                            tmp += (c - 48) / pow(10,i++);
                            getChar();
                        } while (isNumber());
                        token.type = Token.Type.Float;
                        token.d = tmp;
                    } else {
                        error(token);
                        continue;
                    }
                } else {
                    token.type = Token.Type.Constant;
                    token.value = 0;
                }


            } else if (c == '_' || isLetter()) {
                StringBuilder tmp = new StringBuilder();
                do {
                    tmp.append(c);
                    getChar();
                } while ((c == '_' || isLetter() || isNumber()) && !eof);
                Token.Type type = Token.reserve(tmp.toString());
                if (type == Token.Type.Identifier)
                    token.name = tmp.toString();
                token.type = type;
            } else {
                switch (c) {
                    case '+':
                        token.type = Token.Type.Add;
                        getChar();
                        break;
                    case '-':
                        token.type = Token.Type.Sub;
                        getChar();
                        break;
                    case '*':
                        token.type = Token.Type.Mult;
                        getChar();
                        break;
                    case '/':
                        token.type = Token.Type.Div;
                        getChar();
                        break;
                    case '<':
                        if (!eof) {
                            getChar();
                            if (c == '=') {
                                getChar();
                                token.type = Token.Type.LessEqual;
                                break;
                            }
                        }
                        token.type = Token.Type.Less;
                        break;
                    case '>':
                        if (!eof) {
                            getChar();
                            if (c == '=') {
                                getChar();
                                token.type = Token.Type.MoreEqual;
                                break;
                            }
                        }
                        token.type = Token.Type.More;
                        break;
                    case '=':
                        if (!eof) {
                            getChar();
                            if (c == '=') {
                                getChar();
                                token.type = Token.Type.Equal;
                                break;
                            }
                        }
                        token.type = Token.Type.Assignment;
                        break;
                    case '!':
                        if (eof) {
                            error(token);
                            continue;
                        }
                        getChar();
                        if (c == '=')
                            getChar();
                        token.type = Token.Type.NotEqual;
                        break;
                    case '(':
                        getChar();
                        token.type = Token.Type.LeftL;
                        break;
                    case ')':
                        getChar();
                        token.type = Token.Type.RightL;
                        break;
                    case '[':
                        getChar();
                        token.type = Token.Type.LeftM;
                        break;
                    case ']':
                        getChar();
                        token.type = Token.Type.RightM;
                        break;
                    case '{':
                        getChar();
                        token.type = Token.Type.LeftB;
                        break;
                    case '}':
                        getChar();
                        token.type = Token.Type.RightB;
                        break;
                    case ',':
                        getChar();
                        token.type = Token.Type.Comma;
                        break;
                    case ';':
                        getChar();
                        token.type = Token.Type.Semicolon;
                        break;
                    case '"':
                        StringBuilder tmp = new StringBuilder();
                        getChar();
                        while (c != '"') {
                            if (eof) {
                                error(token);
                                break;
                            }
                            tmp.append(c);
                            getChar();
                        }
                        token.type = Token.Type.String;
                        token.s = tmp.toString();
                        getChar();
                        break;
                    default :
                        error(token);
                        continue;
                }
            }
            tokens.add(token);
        }
        finish = true;
        compiler.handleCompileInformationEvent(new CompileInformationEvent(this,"词法分析结束",false));
    }

    public void handleEOFEvent(EOFEvent event) {
        eof = true;
    }

    private void error(Token token) {
        getChar();
        compiler.handleCompileInformationEvent(new CompileInformationEvent(this,"词法分析在" + token.x + "行" + token.y + "列处出现错误",true));
    }
}