package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BooleanExpr implements Expr{
    private final boolean value;
    @Override
    public Object eval(Scope scope) {
        return value;
    }
}
