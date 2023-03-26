package com.siiam.compiler.parser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Op{
    Assign, AssignAdd, AssignSub, AssignMul, AssignDiv,
    Eq, Ne,
    Lt, Le, Gt, Ge,
    Range,
    Add, Sub, Mul, Div,
    Not, And, Or,
    BitAnd, BitOr, BitXor,
    Dot, Chain,
    Inc, Dec,
    Nullish, Pow,
    Spread,
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
            case Range: return Prec.Range;
            case Mul:
            case Div: return Prec.Mul;
            case And: return Prec.And;
            case Or: return Prec.Or;
            case Assign:
            case AssignAdd:
            case AssignSub:
            case AssignMul:
            case AssignDiv: return Prec.Assign;
            case BitAnd: return Prec.BitAnd;
            case BitOr: return Prec.BitOr;
            case BitXor: return Prec.BitXor;
            case Spread: return Prec.Spread;
            case Not: return Prec.Prefix;
            case Dec:
            case Inc: return Prec.Postfix;
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
            case AssignAdd:
            case AssignSub:
            case AssignMul:
            case AssignDiv:
            case Nullish:
            case Chain:
            case BitAnd:
            case BitOr:
            case BitXor:
            case Pow:
            case Dot:
            case Range: return true;
            default: return false;
        }
    }

    public boolean isPrefix(){
        switch (this){
            case Add:
            case Sub:
            case Not:
            case Spread: return true;
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