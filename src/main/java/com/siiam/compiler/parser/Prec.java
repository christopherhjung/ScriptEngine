package com.siiam.compiler.parser;

public enum Prec {
    Bottom,
    Assign,
    Spread,
    Or,
    And,
    Rel,
    Nullish,
    BitOr,
    BitXor,
    BitAnd,
    Shift,
    Range,
    Add,
    Mul,
    Pow,
    Prefix,
    Postfix,
    Top;

    public Prec next(){
        return Prec.values()[Math.min(ordinal() + 1, Prec.Top.ordinal())];
    }

    public boolean largerThan(Prec other){
        return ordinal() > other.ordinal();
    }
}