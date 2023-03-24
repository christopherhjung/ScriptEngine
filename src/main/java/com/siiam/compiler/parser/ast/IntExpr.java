package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IntExpr implements Expr{
    private final int value;
    @Override
    public Object eval(Scope scope) {
        return value;
    }
}
