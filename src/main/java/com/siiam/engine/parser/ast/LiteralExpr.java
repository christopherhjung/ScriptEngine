package com.siiam.engine.parser.ast;

import com.siiam.engine.scope.Scope;
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
