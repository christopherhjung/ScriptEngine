package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LiteralExpr implements Expr{
    private final Object value;
    @Override
    public Object eval(Scope scope) {
        return value;
    }

    @Override
    public boolean isConst() {
        return true;
    }
}
