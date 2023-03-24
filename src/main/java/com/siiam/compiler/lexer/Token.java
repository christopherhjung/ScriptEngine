package com.siiam.compiler.lexer;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Token {
    private Kind kind;
    private String symbol;

    public enum Kind{
        Ident, String, Boolean, Number, Null,
        Assign,
        Eq, Ne,
        Lt, Le, Gt, Ge,
        Not, And, Or,
        BitAnd, BitOr, BitXor,
        Plus, Minus, Star, Slash,
        Inc, Dec,
        LeftParen, RightParen,
        LeftBrace, RightBrace,
        Dot, Comma, Semi, Colon,
        Fn, If, While, Else, Break, Continue, Return,
        Quest, Nullish, Chain, Pow,
        EOL, Error
    }
}
