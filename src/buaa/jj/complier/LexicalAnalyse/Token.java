package buaa.jj.complier.LexicalAnalyse;

public class Token {
    public enum Type{
        Const,
        Int,
        Void,
        If,
        Else,
        While,
        Main,
        Return,
        Printf,
        Scanf,
        Add,
        Sub,
        Mult,
        Div,
        Less,
        LessEqual,
        More,
        MoreEqual,
        Equal,
        NotEqual,
        LeftL,
        RightL,
        LeftM,
        RightM,
        LeftB,
        RightB,
        Comma,
        Semicolon,
        Assignment,
        Constant,
        String,
        Identifier,
        Float
    }

    public Type type;
    public int value;
    public String name;
    public String s;
    public double d;
    public int x;
    public int y;

    public static Type reserve(String s) {
        Type type;
        switch (s) {
            case "const":
                type = Type.Const;
                break;
            case "int":
                type = Type.Int;
                break;
            case "void":
                type = Type.Void;
                break;
            case "if":
                type = Type.If;
                break;
            case "else":
                type = Type.Else;
                break;
            case "while":
                type = Type.While;
                break;
            case "main":
                type = Type.Main;
                break;
            case "return":
                type = Type.Return;
                break;
            case "printf":
                type = Type.Printf;
                break;
            case "scanf":
                type = Type.Scanf;
                break;
            default :
                type = Type.Identifier;
        }
        return type;
    }
}
