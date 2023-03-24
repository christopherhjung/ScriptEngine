package com.siiam.compiler.parser.ast;

import com.siiam.compiler.parser.controlflow.ReturnException;
import com.siiam.compiler.scope.Scope;
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
}
