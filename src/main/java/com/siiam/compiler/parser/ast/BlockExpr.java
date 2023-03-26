package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.NestedScope;
import com.siiam.compiler.scope.Scope;
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
    public Expr reduce(Scope scope) {
        var newExprs = new Expr[exprs.length];
        for( var idx = 0; idx < exprs.length ; idx++ ){
            newExprs[idx] = exprs[idx].reduce(scope);
        }
        return new BlockExpr(newExprs);
    }
}
