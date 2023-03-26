package com.siiam.compiler.parser.ast;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LetExpr implements Expr{
    private final Expr ptrn;
    private final Expr init;

    @Override
    public Object eval(Scope scope) {
        Object initVal = null;
        if(init != null){
            initVal = init.eval(scope);
        }
        ptrn.assign(scope, initVal, true);
        return initVal;
    }
}
