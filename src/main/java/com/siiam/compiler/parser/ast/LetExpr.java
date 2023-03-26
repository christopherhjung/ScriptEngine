package com.siiam.compiler.parser.ast;

import com.siiam.compiler.parser.Op;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
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

    @Override
    public Expr bind(Scope scope, boolean define) {
        var newInit = init == null ? null : init.bind(scope, false);
        return new InfixExpr(ptrn.bind(scope, true), newInit, Op.Assign);
    }
}
