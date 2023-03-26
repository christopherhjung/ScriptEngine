package com.siiam.compiler.lexer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Token {
    private Kind kind;
    private Enter enter;
    private String symbol;

    public enum Kind{
        Ident, String, Boolean, Number, Null,
        Assign, AssignPlus, AssignMinus, AssignStar, AssignSlash,
        Eq, Ne,
        Lt, Le, Gt, Ge,
        Not, And, Or,
        BitAnd, BitOr, BitXor,
        Plus, Minus, Star, Slash,
        Inc, Dec,
        LeftParen, RightParen,
        LeftBrace, RightBrace,
        Dot, Range, Ellipsis,
        Comma, Semi, Colon,
        Fn, If, While, Else, Break, Continue, Return, For, In,
        Quest, Nullish, Chain, Pow,
        Arrow, Let,
        EOL, Error
    }

    public enum Enter{
        Token, Space, NL
    }
}
