package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.NestedScope;
import com.siiam.compiler.scope.Scope;
import com.siiam.compiler.scope.StaticScope;

public class ScopedExpr implements Expr{
    private final Scope scope;
    private final Expr expr;

    public ScopedExpr(Scope scope, Expr expr) {
        this.scope = scope;
        this.expr = expr;
    }

    @Override
    public Object eval(Scope scope) {
        var nestedScope = NestedScope.nest(scope, this.scope);
        return expr.eval(nestedScope);
    }

    @Override
    public Object call(Scope scope, Object[] args) {
        var nestedScope = NestedScope.nest(scope, this.scope);
        return expr.call(nestedScope, args);
    }
}