package com.siiam.engine.parser.ast;

import com.siiam.engine.parser.controlflow.ReturnException;
import com.siiam.engine.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReturnExpr implements Expr{
    private Expr expr;

    @Override
    public Object eval(Scope scope) {
        Object returnValue = null;
        if(expr != null){
            returnValue = expr.eval(scope);
        }

        throw new ReturnException(returnValue);
    }

    @Override
    public Expr bind(Scope scope, boolean define) {
        var newExpr = expr.bind(scope, define);
        return new ReturnExpr(newExpr);
    }
}
