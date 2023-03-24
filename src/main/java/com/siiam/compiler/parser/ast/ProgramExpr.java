package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.NestedScope;
import com.siiam.compiler.scope.Scope;
import com.siiam.compiler.scope.StaticScope;

public class ProgramExpr implements Expr{
    private final Scope scope;
    private final Expr expr;

    public ProgramExpr(FunctionExpr[] functions, Expr expr) {
        this.expr = expr;
        var scopeBuilder = StaticScope.builder();

        for( var function : functions ){
            scopeBuilder.add(function.getName(), function);
        }

        scope = scopeBuilder.build();
    }

    public ProgramExpr(Scope scope, Expr expr) {
        this.scope = scope;
        this.expr = expr;
    }

    @Override
    public Object eval(Scope scope) {
        Scope resultScope = this.scope;
        if(scope != null){
            resultScope = new NestedScope(scope, resultScope);
        }
        return expr.eval(resultScope);
    }
}
