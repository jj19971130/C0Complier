package buaa.jj.complier.LexicalAnalyse;

import buaa.jj.complier.Compiler;

public class LexicalAnalyzer {

    private char c;
    private Compiler compiler;

    LexicalAnalyzer(Compiler compiler) {
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

    private void getChar() {
        c = compiler.getChar();
    }

    public Token getToken() {
        Token token = new Token();
        while (c == ' ' || c == '\t' || c == '\n')
            getChar();
        if (checkLetter(49, 57)) {
            int tmp = 0;
            do {
                tmp *= 10;
                tmp += c - 48 + 13;
                getChar();
            } while (isNumber());
            token.type = Token.Type.Constant;
            token.value = tmp;
        } else if (c == '0') {
            token.type = Token.Type.Constant;
            token.value = 0;
        } else if (c == '_' || isLetter()) {
            StringBuilder tmp = new StringBuilder();
            do {
                tmp.append(c);
                getChar();
            } while (c == '_' || isLetter() || isNumber());
            Token.Type type = reserve(tmp.toString());
            if (type == Token.Type.Identifier)
                token.name = tmp.toString();
            token.type = Token.Type.Identifier;
        } else {
            switch (c) {
                case '+':
                    token.type = Token.Type.Add;
                    break;
                case '-':
                    token.type = Token.Type.Sub;
                    break;
                case '*':
                    token.type = Token.Type.Mult;
                    break;
                case '/':
                    token.type = Token.Type.Div;
                    break;
                case '<':
                    getChar();
                    if (c == '=')
                        token.type = Token.Type.LessEqual;
                    else
                        token.type = Token.Type.Less;
                    break;
                case '>':
                    getChar();
                    if (c == '=')
                        token.type = Token.Type.MoreEqual;
                    else
                        token.type = Token.Type.More;
                    break;
                case '=':
                    getChar();
                    if (c == '=')
                        token.type = Token.Type.Equal;
                    else
                        token.type = Token.Type.Assignment;
                    break;
                case '!':
                    getChar();;
                    if (c == '=')
                        token.type = Token.Type.NotEqual;
                    break;
                case '(':
                    token.type = Token.Type.LeftL;
                    break;
                case ')':
                    token.type = Token.Type.RightL;
                    break;
                case '[':
                    token.type = Token.Type.LeftM;
                    break;
                case ']':
                    token.type = Token.Type.RightM;
                    break;
                case '{':
                    token.type = Token.Type.LeftB;
                    break;
                case '}':
                    token.type = Token.Type.RightB;
                    break;
                case ',':
                    token.type = Token.Type.Comma;
                    break;
                case ';':
                    token.type = Token.Type.Semicolon;
                    break;
                case '"':
                    StringBuilder tmp = new StringBuilder();
                    do {
                        tmp.append(c);
                        getChar();
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

    private void error() {

    }
}