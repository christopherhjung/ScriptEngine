package com.siiam.compiler.parser.ast;


import com.siiam.compiler.scope.Scope;

public class NullExpr implements Expr{
    @Override
    public Object eval(Scope scope) {
        return null;
    }
}
