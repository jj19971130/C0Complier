package buaa.jj.complier.SyntaxAnalysis;

import buaa.jj.complier.Compiler;
import buaa.jj.complier.Events.CompileInformationEvent;
import buaa.jj.complier.Events.EOFEvent;
import buaa.jj.complier.LexicalAnalyse.Identifier;
import buaa.jj.complier.LexicalAnalyse.LexicalAnalyzer;
import buaa.jj.complier.LexicalAnalyse.Token;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class SyntaxAnalyzer extends Thread implements EventListener {
    private Token token;
    private Compiler compiler;
    private LexicalAnalyzer lexicalAnalyzer;
    private IdentifierList identifierList;
    private boolean eof;

    public SyntaxAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        compiler = lexicalAnalyzer.compiler;
        identifierList = new IdentifierList();
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
        identifierList.init();
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
                {//函数名插入符号表
                    Identifier identifier = new Identifier();
                    identifier.name = "main";
                    identifier.type = 2;
                    identifier.delcare = token.x;
                    identifier.ret = true;
                    identifierList.addLevel();
                    getToken();
                    J(identifier);
                }
                I();
            } else if (token.type == Token.Type.Identifier){ //区分是函数声明还是变量声明
                int tmpx = token.x;
                String tmpname = token.name;
                getToken();
                if (token.type == Token.Type.Comma) {
                    {//变量插入符号表
                        Identifier identifier = new Identifier();
                        identifier.name = tmpname;
                        identifier.type = 1;
                        identifier.delcare = tmpx;
                        if (!identifierList.insertIdentifier(identifier,false)) {
                            error(token);
                        }
                    }
                    do {
                        getToken();
                        if (token.type != Token.Type.Identifier) {
                            error(token);
                        }
                        {//变量插入符号表
                            Identifier identifier = new Identifier();
                            identifier.name = token.name;
                            identifier.type = 1;
                            identifier.delcare = token.x;
                            if (!identifierList.insertIdentifier(identifier,false)) {
                                error(token);
                            }
                        }
                        getToken();
                    } while (token.type == Token.Type.Comma);
                    if (token.type == Token.Type.Semicolon) {
                        getToken();
                    } else {
                        error(token);
                    }
                    if (token.type == Token.Type.Void || token.type == Token.Type.Int) { //变量声明结束，函数声明开始
                        A1(token.type == Token.Type.Int);
                    } else {
                        error(token);
                    }
                } else if (token.type == Token.Type.LeftL) {
                    {//函数插入符号表
                        Identifier identifier = new Identifier();
                        identifier.name = tmpname;
                        identifier.type = 2;
                        identifier.delcare = tmpx;
                        identifier.ret = true;
                        identifierList.addLevel();
                        getToken();
                        J(identifier);
                    }
                    I();
                    if (token.type == Token.Type.Void || token.type == Token.Type.Int) { //处理后序函数声明
                        A1(token.type == Token.Type.Int);
                    } else {
                        error(token);
                    }
                } else {
                    error(token);
                }
            }
        } else if (token.type == Token.Type.Void) { //处理函数声明
            A1(false);
        } else {
            error(token);
        }
    }

    private void A1(boolean type) {
        getToken();
        if (token.type == Token.Type.Main) { //判断是否为主函数，是则处理后结束
            {//函数名插入符号表
                String s = "make ide happy";
                Identifier identifier = new Identifier();
                identifier.name = "main";
                identifier.type = 2;
                identifier.delcare = token.x;
                identifier.ret = type;
                identifierList.addLevel();
                getToken();
                J(identifier);
            }
            I();
        } else if (token.type == Token.Type.Identifier) { //非主函数进入循环直到主函数处理完后退出
            {//函数插入符号表
                Identifier identifier = new Identifier();
                identifier.name = token.name;
                identifier.type = 2;
                identifier.delcare = token.x;
                identifier.ret = type;
                identifierList.addLevel();
                getToken();
                J(identifier);
            }
            I();
            do {
                if (token.type == Token.Type.Int || token.type == Token.Type.Void) {
                    type = token.type == Token.Type.Int;
                    getToken();
                    if (token.type == Token.Type.Main) { //主函数处理完后跳出
                        {//函数名插入符号表
                            String s = "make ide happier";
                            Identifier identifier = new Identifier();
                            identifier.name = "main";
                            identifier.type = 2;
                            identifier.delcare = token.x;
                            identifier.ret = type;
                            identifierList.addLevel();
                            getToken();
                            J(identifier);
                        }
                        I();
                        break;
                    } else if (token.type == Token.Type.Identifier) { //非主函数继续循环
                        {//函数插入符号表
                            String s = "make ide happy";
                            Identifier identifier = new Identifier();
                            identifier.name = token.name;
                            identifier.type = 2;
                            identifier.delcare = token.x;
                            identifier.ret = type;
                            identifierList.addLevel();
                            getToken();
                            J(identifier);
                        }
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
            Identifier identifier = new Identifier();
            {//常量插入符号表
                identifier.name = token.name;
                identifier.type = 0;
                identifier.delcare = token.x;
            }
            getToken();
            if (token.type == Token.Type.Assignment) {
                getToken();
                identifier.value = D(false);
                if (!identifierList.insertIdentifier(identifier,false)) {
                    error(token);
                }
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private int D(boolean type) {//整数
        boolean state = false;
        if (token.type == Token.Type.Sub || token.type == Token.Type.Add) {
            state = token.type == Token.Type.Sub;
            getToken();
        }
        if (token.type == Token.Type.Constant) {
            int ret = token.value;
            if (state) {
                ret = -ret;
            }
            if (type)
                System.out.println("LIT 0," + ret);
            getToken();
            return ret;
        } else {
            error(token);
            return 0;
        }
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
        if (token.type == Token.Type.Int) {
            getToken();
            if (token.type == Token.Type.Identifier) {
                {//变量插入符号表
                    Identifier identifier = new Identifier();
                    identifier.name = token.name;
                    identifier.type = 1;
                    identifier.delcare = token.x;
                    if (!identifierList.insertIdentifier(identifier,false)) {
                        error(token);
                    }
                }
                getToken();
            } else {
                error(token);
            }
        } else {
            error(token);
        }
        while (token.type == Token.Type.Comma) {
            getToken();
            if (token.type == Token.Type.Identifier) {
                {//变量插入符号表
                    String s = "make ide happy";
                    Identifier identifier = new Identifier();
                    identifier.name = token.name;
                    identifier.type = 1;
                    identifier.delcare = token.x;
                    if (!identifierList.insertIdentifier(identifier,false)) {
                        error(token);
                    }
                }
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
                System.out.println("OPR 0,0\n");
                identifierList.removeLevel();
                getToken();
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void J(Identifier identifier) {
        if (token.type == Token.Type.LeftL) {
            getToken();
            List<String> names = new ArrayList<String>();
            K(identifier,names);
            for (int i = 0; i < identifier.param; i++) {
                Identifier identifier1 = identifierList.findIdentifier(names.get(identifier.param - i - 1),0);
                if (identifier1 == null) {
                    error(token);
                } else {
                    System.out.println("STO 0," + identifier1.address);
                }
            }
            if (token.type == Token.Type.RightL) {
                identifierList.insertIdentifier(identifier,true);
                getToken();
            } else {
                error(token);
            }
        } else {
            error(token);
        }
    }

    private void K(Identifier identifier, List names) {
        if (token.type == Token.Type.Int) {
            getToken();
            if (token.type == Token.Type.Identifier) {
                names.add(token.name);
                identifier.param++;
                {//变量插入符号表
                    Identifier identifier1 = new Identifier();
                    identifier1.name = token.name;
                    identifier1.type = 1;
                    identifier1.delcare = token.x;
                    if (!identifierList.insertIdentifier(identifier1,false)) {
                        error(token);
                    }
                }
                getToken();
                while (token.type == Token.Type.Comma) {
                    getToken();
                    if (token.type == Token.Type.Int) {
                        getToken();
                        if (token.type == Token.Type.Identifier) {
                            names.add(token.name);
                            identifier.param++;
                            {//变量插入符号表
                                Identifier identifier1 = new Identifier();
                                identifier1.name = token.name;
                                identifier1.type = 1;
                                identifier1.delcare = token.x;
                                if (!identifierList.insertIdentifier(identifier1,false)) {
                                    error(token);
                                }
                            }
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
        boolean state = true;
        if (token.type == Token.Type.Add || token.type == Token.Type.Sub) {
            getToken();
            state = token.type == Token.Type.Add;
        }
        N();
        if (!state) {
            System.out.println("OPR 0,1");
            System.out.println("LIT 0,1");
            System.out.println("OPR 0,2");
        }
        while (token.type == Token.Type.Add || token.type == Token.Type.Sub) {
            state = token.type == Token.Type.Add;
            getToken();
            N();
            System.out.println("OPR 0," + (state ? '2' : '3'));
        }
    }

    private void N() {
        O();
        while (token.type == Token.Type.Mult || token.type == Token.Type.Div) {
            boolean state = token.type == Token.Type.Mult;
            getToken();
            O();
            System.out.println("OPR 0," + (state ? '4' : '5'));
        }
    }

    private void O() {
        if (token.type == Token.Type.Identifier) {
            String name = token.name;
            getToken();
            if (token.type == Token.Type.LeftL) {
                Integer layer = 0;
                Identifier identifier = identifierList.findIdentifier(name, layer);
                getToken();
                V();
                if (token.type == Token.Type.RightL) {
                    if (identifier == null) {
                        error(token);
                    } else
                        System.out.println("CALL " + layer + "," + identifier.address);
                    getToken();
                } else {
                    error(token);
                }
            } else {
                Integer layer = 0;
                Identifier identifier= identifierList.findIdentifier(name, layer);
                if (identifier == null) {
                    error(token);
                } else
                    System.out.println("LOD " + layer + "," + identifier.address);
            }
        } else if (token.type == Token.Type.LeftL) {
            M();
            if (token.type == Token.Type.RightL) {
                getToken();
            } else {
                error(token);
            }
        } else if (token.type == Token.Type.Add || token.type == Token.Type.Sub || token.type == Token.Type.Constant) {
            D(true);
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
                Integer layer = 0;
                Identifier identifier = identifierList.findIdentifier(token.name,layer);
                getToken();
                if (token.type == Token.Type.LeftL) {
                    getToken();
                    V();
                    if (token.type == Token.Type.RightL) {
                        String s = "make ide happy";
                        if (identifier == null) {
                            error(token);
                        } else
                            System.out.println("CALL " + layer + "," + identifier.address);
                        getToken();
                    } else {
                        error(token);
                    }
                } else if (token.type == Token.Type.Assignment) {
                    getToken();
                    M();
                    System.out.println("STO " + layer + "," + identifier.address);
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
                    Compiler.logger.setBuffer(true);
                    System.out.println("JMC 0,0");
                    P();
                    if (token.type == Token.Type.Else)
                        System.out.println("JMP 0,0");
                    Compiler.logger.insertJmc();
                    if (token.type == Token.Type.Else) {
                        getToken();
                        P();
                        Compiler.logger.insertJmp();
                    }
                    Compiler.logger.setBuffer(false);
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
            int i = 0;
            switch (token.type) {
                case Equal:
                    i = 8;
                    break;
                case NotEqual:
                    i = 9;
                    break;
                case Less:
                    i = 10;
                    break;
                case MoreEqual:
                    i = 11;
                    break;
                case More:
                    i = 12;
                    break;
                case LessEqual:
                    i = 13;
                    break;
            }
            getToken();
            M();
            System.out.println("OPR 0," + i);
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
                    Compiler.logger.setBuffer(true);
                    P();
                    Compiler.logger.insertJmc();
                    Compiler.logger.setBuffer(false);
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
                    Integer layer = 0;
                    Identifier identifier = identifierList.findIdentifier(token.name,layer);
                    if (identifier == null) {
                        error(token);
                    } else
                        System.out.println("RED " + layer + "," + identifier.address);
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
                    System.out.println("LIT 0," + token.s);
                    System.out.println("WRT 0,0");
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
