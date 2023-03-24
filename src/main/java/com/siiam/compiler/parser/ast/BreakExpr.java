package com.siiam.compiler.parser.ast;

import com.siiam.compiler.parser.controlflow.BreakException;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BreakExpr implements Expr{
    private String label;

    @Override
    public Object eval(Scope scope) {
        throw new BreakException(label);
    }
}
