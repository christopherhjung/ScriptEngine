package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StringExpr implements Expr{
    private final String value;
    @Override
    public Object eval(Scope scope) {
        return value;
    }
}
