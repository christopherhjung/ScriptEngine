package com.siiam.compiler.parser.ast;

import com.siiam.compiler.parser.controlflow.ContinueException;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ContinueExpr implements Expr{
    private String label;

    @Override
    public Object eval(Scope scope) {
        throw new ContinueException(label);
    }
}
