package com.siiam.compiler.parser;

public enum Prec {
    Bottom,
    Assign,
    Or,
    And,
    Nullish,
    Rel,
    BitOr,
    BitXor,
    BitAnd,
    Shift,
    Add,
    Mul,
    Pow,
    Unary,
    Top;

    public Prec next(){
        return Prec.values()[Math.min(ordinal() + 1, Prec.Top.ordinal())];
    }

    public boolean largerThan(Prec other){
        return ordinal() > other.ordinal();
    }
}