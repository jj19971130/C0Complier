package buaa.jj.complier.SyntaxAnalysis;

import buaa.jj.complier.Compiler;
import buaa.jj.complier.Events.CompileInformationEvent;
import buaa.jj.complier.Events.EOFEvent;
import buaa.jj.complier.LexicalAnalyse.LexicalAnalyzer;
import buaa.jj.complier.LexicalAnalyse.Token;

import java.util.EventListener;

public class SyntaxAnalyzer extends Thread implements EventListener {
    private Token token;
    private Compiler compiler;
    private LexicalAnalyzer lexicalAnalyzer;
    private boolean eof;

    public SyntaxAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        compiler = lexicalAnalyzer.compiler;
    }

    public void handleEOFEvent(EOFEvent event) {
        eof = true;
    }

    private void getToken() {
        try {
            token = lexicalAnalyzer.getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        getToken();
        A();
        compiler.handleCompileInformationEvent(new CompileInformationEvent(this,"语法分析结束",false));
    }

    private void A() { //程序（入口处）
        if (token.type == Token.Type.Const) { //常量说明部分
            B();
        }
        if (token.type == Token.Type.Int) {
            getToken();
            if (token.type == Token.Type.Main) { //直接进入主函数
                getToken();
                J();
                I();
            } else if (token.type == Token.Type.Identifier){ //区分是函数声明还是变量声明
                getToken();
                if (token.type == Token.Type.Comma) {
                    do {
                        getToken();
                        if (token.type != Token.Type.Identifier) {
                            error(token);
                        }
                        getToken();
                    } while (token.type == Token.Type.Comma);
                    if (token.type == Token.Type.Semicolon) {
                        getToken();
                    } else {
                        error(token);
                    }
                    if (token.type == Token.Type.Void || token.type == Token.Type.Int) { //变量声明结束，函数声明开始
                        A1();
                    } else {
                        error(token);
                    }
                } else if (token.type == Token.Type.LeftL) {
                    J();
                    I();
                    if (token.type == Token.Type.Void || token.type == Token.Type.Int) { //处理后序函数声明
                        A1();
                    } else {
                        error(token);
                    }
                } else {
                    error(token);
                }
            }
        } else if (token.type == Token.Type.Void) { //处理函数声明
            A1();
        } else {
            error(token);
        }
    }

    private void A1() {
        getToken();
        if (token.type == Token.Type.Main) { //判断是否为主函数，是则处理后结束
            getToken();
            J();
            I();
        } else if (token.type == Token.Type.Identifier) { //非主函数进入循环直到主函数处理完后退出
            getToken();
            J();
            I();
            do {
                if (token.type == Token.Type.Int || token.type == Token.Type.Void) {
                    getToken();
                    if (token.type == Token.Type.Main) { //主函数处理完后跳出
                        getToken();
                        J();
                        I();
                        break;
                    } else if (token.type == Token.Type.Identifier) { //非主函数继续循环
                        getToken();
                        J();
                        I();
                    } else {
                        error(token);
                    }
                } else {
                    error(token);
                }
            } while (true);
        } else {
            error(token);
        }
    }

    private void B() {//常量说明部分
        if (token.type == Token.Type.Const) {
            do {
                getToken();
                C();
            } while (token.type == Token.Type.Comma);
            if (token.type == Token.Type.Semicolon) {
                getToken();
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void C() {//常量定义
        if (token.type == Token.Type.Identifier) {
            getToken();
            if (token.type == Token.Type.Assignment) {
                getToken();
                D();
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void D() {//整数
        if (token.type == Token.Type.Sub || token.type == Token.Type.Add) {
            getToken();
            if (token.type != Token.Type.Constant) {
                error(token);
            }
        }
        getToken();
    }

    /*private void E() {//标识符

    }*/

    private void F() {//声明头部
        if (token.type == Token.Type.Int) {
            getToken();
            if (token.type == Token.Type.Identifier) {
                getToken();
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void G() {//变量说明部分
        F();
        while (token.type == Token.Type.Comma) {
            getToken();
            if (token.type == Token.Type.Identifier) {
                getToken();
            } else {
                error(token);
            }
        }
        if (token.type == Token.Type.Semicolon) {
            getToken();
        } else {
            error(token);
        }
    }

    private void H() {//函数定义部分

    }

    private void I() {//复合语句
        if (token.type == Token.Type.LeftB) {
            getToken();
            if (token.type == Token.Type.Const) {
                B();
            }
            if (token.type == Token.Type.Int) {
                G();
            }
            W();
            if (token.type == Token.Type.RightB) {
                getToken();
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void J() {
        if (token.type == Token.Type.LeftL) {
            getToken();
            K();
            if (token.type == Token.Type.RightL) {
                getToken();
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void K() {
        if (token.type == Token.Type.Int) {
            getToken();
            if (token.type == Token.Type.Identifier) {
                getToken();
                while (token.type == Token.Type.Comma) {
                    getToken();
                    if (token.type == Token.Type.Int) {
                        getToken();
                        if (token.type == Token.Type.Identifier) {
                            getToken();
                        } else {
                            error(token);
                            break;
                        }
                    } else {
                        error(token);
                        break;
                    }
                }
            } else {
                error(token);
            }
        }
    }

    private void L() {

    }

    private void M() {
        if (token.type == Token.Type.Add || token.type == Token.Type.Sub) {
            getToken();
        }
        N();
        while (token.type == Token.Type.Add || token.type == Token.Type.Sub) {
            getToken();
            N();
        }
    }

    private void N() {
        O();
        while (token.type == Token.Type.Mult || token.type == Token.Type.Div) {
            getToken();
            O();
        }
    }

    private void O() {
        if (token.type == Token.Type.Identifier) {
            getToken();
            if (token.type == Token.Type.LeftL) {
                getToken();
                V();
                if (token.type == Token.Type.RightL) {
                    getToken();
                } else {
                    error(token);
                }
            }
        } else if (token.type == Token.Type.LeftL) {
            M();
            if (token.type == Token.Type.RightL) {
                getToken();
            } else {
                error(token);
            }
        } else if (token.type == Token.Type.Add || token.type == Token.Type.Sub || token.type == Token.Type.Constant) {
            D();
        }

    }

    private void P() {
        switch (token.type) {
            case If:
                R();
                break;
            case While:
                T();
                break;
            case LeftB:
                getToken();
                W();
                if (token.type == Token.Type.RightB) {
                    getToken();
                } else {
                    error(token);
                }
                break;
            case Identifier:
                getToken();
                if (token.type == Token.Type.LeftL) {
                    getToken();
                    V();
                    if (token.type == Token.Type.RightL) {
                        getToken();
                    } else {
                        error(token);
                    }
                } else if (token.type == Token.Type.Assignment) {
                    getToken();
                    M();
                } else {
                    error(token);
                }
                if (token.type == Token.Type.Semicolon) {
                    getToken();
                } else {
                    error(token);
                }
                break;
            case Return:
                Z();
                if (token.type == Token.Type.Semicolon) {
                    getToken();
                } else {
                    error(token);
                }
                break;
            case Scanf:
                X();
                if (token.type == Token.Type.Semicolon) {
                    getToken();
                } else {
                    error(token);
                }
                break;
            case Printf:
                Y();
                if (token.type == Token.Type.Semicolon) {
                    getToken();
                } else {
                    error(token);
                }
                break;
        }
    }

    private void Q() {

    }

    private void R() {
        if (token.type == Token.Type.If) {
            getToken();
            if (token.type == Token.Type.LeftL) {
                getToken();
                S();
                if (token.type == Token.Type.RightL) {
                    getToken();
                    P();
                    if (token.type == Token.Type.Else) {
                        getToken();
                        P();
                    }
                } else {
                    error(token);
                }
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void S() {
        M();
        if (token.type == Token.Type.Less || token.type == Token.Type.LessEqual || token.type == Token.Type.More || token.type == Token.Type.MoreEqual
        || token.type == Token.Type.NotEqual || token.type == Token.Type.Equal) {
            getToken();
            M();
        }
    }

    private void T() {
        if (token.type == Token.Type.While) {
            getToken();
            if (token.type == Token.Type.LeftL) {
                getToken();
                S();
                if (token.type == Token.Type.RightL) {
                    getToken();
                    P();
                } else {
                    error(token);
                }
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void U() {
        if (token.type == Token.Type.Identifier) {
            getToken();
            if (token.type == Token.Type.LeftL) {
                getToken();
                V();
                if (token.type == Token.Type.RightL) {
                    getToken();
                } else {
                    error(token);
                }
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void V() {
        if (token.type == Token.Type.Add || token.type == Token.Type.Sub || token.type == Token.Type.Constant
                || token.type == Token.Type.LeftL || token.type == Token.Type.Identifier) {
            M();
            while (token.type == Token.Type.Comma) {
                getToken();
                M();
            }
        }
    }

    private void W() {
        while (token.type == Token.Type.If || token.type == Token.Type.While || token.type == Token.Type.LeftB || token.type == Token.Type.Identifier ||
        token.type == Token.Type.Return || token.type == Token.Type.Scanf || token.type == Token.Type.Printf) {
            P();
        }
    }

    private void X() {
        if (token.type == Token.Type.Scanf) {
            getToken();
            if (token.type == Token.Type.LeftL) {
                getToken();
                if (token.type == Token.Type.Identifier) {
                    getToken();
                    if (token.type == Token.Type.RightL) {
                        getToken();
                    } else {
                        error(token);
                    }
                } else {
                    error(token);
                }
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void Y() {
        if (token.type == Token.Type.Printf) {
            getToken();
            if (token.type == Token.Type.LeftL) {
                getToken();
                if (token.type == Token.Type.String) {
                    getToken();
                    if (token.type == Token.Type.Comma) {
                        getToken();
                        if (token.type == Token.Type.Add || token.type == Token.Type.Sub || token.type == Token.Type.Constant
                                || token.type == Token.Type.LeftL || token.type == Token.Type.Identifier) {
                            M();
                        } else {
                            error(token);
                        }
                    }
                }
                if (token.type == Token.Type.Add || token.type == Token.Type.Sub || token.type == Token.Type.Constant
                        || token.type == Token.Type.LeftL || token.type == Token.Type.Identifier) {
                    M();
                }
                if (token.type == Token.Type.RightL) {
                    getToken();
                } else {
                    error(token);
                }
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void Z() {
        if (token.type == Token.Type.Return) {
            getToken();
            if (token.type == Token.Type.LeftL) {
                getToken();
                M();
                if (token.type == Token.Type.RightL) {
                    getToken();
                } else {
                    error(token);
                }
            }
        } else {
            error(token);
        }
    }

    private void error(Token token) {
        compiler.handleCompileInformationEvent(new CompileInformationEvent(this,"语法分析在" + token.x + "行" + token.y + "列处出现错误",true));
    }
}
