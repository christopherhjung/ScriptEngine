package com.siiam.engine.parser.ast;

import com.siiam.engine.parser.controlflow.BreakException;
import com.siiam.engine.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BreakExpr implements Expr{
    private String label;

    @Override
    public Object eval(Scope scope) {
        throw new BreakException(label);
    }
}
