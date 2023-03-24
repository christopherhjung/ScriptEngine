package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.NestedScope;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class BlockExpr implements Expr{
    private Expr[] exprs;

    @Override
    public Object eval(Scope scope) {
        scope = NestedScope.wrapMutual(scope);

        var result = (Object)null;
        for(var expr : exprs){
            result = expr.eval(scope);
        }
        return result;
    }
}
