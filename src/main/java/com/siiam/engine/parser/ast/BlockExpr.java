package com.siiam.engine.parser.ast;

import com.siiam.engine.scope.NestedScope;
import com.siiam.engine.scope.Scope;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class BlockExpr implements Expr{
    private Expr[] exprs;

    @Override
    public Object eval(Scope scope) {
        scope = NestedScope.mutual(scope);

        var result = (Object)null;
        for(var expr : exprs){
            result = expr.eval(scope);
        }
        return result;
    }

    @Override
    public Expr bind(Scope scope, boolean define) {
        scope = NestedScope.mutual(scope);
        var newExprs = new Expr[exprs.length];
        var idx = 0;
        for( var expr : exprs ){
            newExprs[idx++] = expr.bind(scope, false);
        }
        return new BlockExpr(newExprs);
    }
}
