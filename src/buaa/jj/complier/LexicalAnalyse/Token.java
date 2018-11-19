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
        Identifier
    }

    public Type type;
    public int value;
    public String name;
    public String s;
}
