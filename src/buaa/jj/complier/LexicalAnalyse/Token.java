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
        LiftL,
        RightL,
        LiftM,
        RightM,
        LiftB,
        RightB,
        Comma,
        Semicolon,
        Assignment,
        Constant,
        Identifier
    }

    public Type type;
}
