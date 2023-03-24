package com.siiam.compiler.parser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Op{
    Assign,
    Eq, Ne,
    Lt, Le, Gt, Ge,
    Add, Sub, Mul, Div,
    Not, And, Or,
    BitAnd, BitOr, BitXor,
    Dot, Chain,
    Inc, Dec,
    Nullish, Pow,
    LeftParen;

    public Prec prec(){
        switch (this){
            case Eq:
            case Ne:
            case Lt:
            case Le:
            case Gt:
            case Ge: return Prec.Rel;
            case Add:
            case Sub: return Prec.Add;
            case Mul:
            case Div: return Prec.Mul;
            case And: return Prec.And;
            case Or: return Prec.Or;
            case Not: return Prec.Unary;
            case Assign: return Prec.Assign;
            case BitAnd: return Prec.BitAnd;
            case BitOr: return Prec.BitOr;
            case BitXor: return Prec.BitXor;
            default: return Prec.Bottom;
        }
    }

    public boolean isInfix(){
        switch (this){
            case Eq:
            case Ne:
            case Lt:
            case Le:
            case Gt:
            case Ge:
            case Add:
            case Sub:
            case Mul:
            case Div:
            case And:
            case Or:
            case Assign:
            case Nullish:
            case Chain:
            case BitAnd:
            case BitOr:
            case BitXor:
            case Pow:
            case Dot: return true;
            default: return false;
        }
    }

    public boolean isPrefix(){
        switch (this){
            case Add:
            case Sub:
            case Not: return true;
            default: return false;
        }
    }

    public boolean isPostfix(){
        switch (this){
            case LeftParen:
            case Inc:
            case Dec: return true;
            default: return false;
        }
    }
}