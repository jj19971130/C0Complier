package buaa.jj.complier.LexicalAnalyse;

import buaa.jj.complier.Compiler;
import buaa.jj.complier.Exceptions.LexicalException;

public class LexicalAnalyzer {

    private char c;
    private boolean eof = false;
    private Compiler compiler;

    public LexicalAnalyzer(Compiler compiler) {
        this.compiler = compiler;
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

    private Token.Type reserve(String s) {
        Token.Type type;
        switch (s) {
            case "const":
                type = Token.Type.Const;
                break;
            case "int":
                type = Token.Type.Int;
                break;
            case "void":
                type = Token.Type.Void;
                break;
            case "if":
                type = Token.Type.If;
                break;
            case "else":
                type = Token.Type.Else;
                break;
            case "while":
                type = Token.Type.While;
                break;
            case "main":
                type = Token.Type.Main;
                break;
            case "return":
                type = Token.Type.Return;
                break;
            case "printf":
                type = Token.Type.Printf;
                break;
            case "scanf":
                type = Token.Type.Scanf;
                break;
            default :
                type = Token.Type.Identifier;
        }
        return type;
    }

    public void getChar() {
        c = compiler.getChar();
        if (c == 27)
            eof = true;
    }

    public void clearState() {
        eof = false;
        getChar();
    }

    public Token getToken() throws LexicalException {
        Token token = new Token();
        while (c == ' ' || c == '\t' || c == '\n')
            getChar();
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
            getChar();
            token.type = Token.Type.Constant;
            token.value = 0;
        } else if (c == '_' || isLetter()) {
            StringBuilder tmp = new StringBuilder();
            do {
                tmp.append(c);
                getChar();
            } while ((c == '_' || isLetter() || isNumber()) && !eof);
            Token.Type type = reserve(tmp.toString());
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
                        error();
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
                    do {
                        tmp.append(c);
                        getChar();
                        if (eof)
                            error();
                    } while (c == '"');
                    token.type = Token.Type.String;
                    token.s = tmp.toString();
                    break;
                default :
                    error();
            }
        }

        return token;
    }

    private void error() throws LexicalException {
        getChar();
        throw new LexicalException();
    }
}