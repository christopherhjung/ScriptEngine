package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.*;

import java.util.HashMap;

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

    @Override
    public Expr bind(Scope scope, boolean define) {
        scope = NestedScope.nest(scope, this.scope);
        scope = NestedScope.readonly(scope);
        var collector = new MutualScope();
        scope = NestedScope.nest(scope, collector);
        for( var value :  this.scope.values() ){
            var content = value.getContent();
            if(content instanceof FunctionExpr){
                var funcExpr = (FunctionExpr) content;
                funcExpr.bindFunction(scope);
            }
        }

        for( var value : this.scope.values()  ){
            var content = value.getContent();
            if(content instanceof FunctionExpr){
                var funcExpr = (FunctionExpr) content;
                funcExpr.bind(scope, false);
            }
        }

        var newScope = collector.toStatic();
        var newExpr = expr.bind(newScope, define);
        return new ScopedExpr(newScope, newExpr);
    }
}
