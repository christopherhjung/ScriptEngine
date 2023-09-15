package com.siiam.engine.parser.ast;

import com.siiam.engine.parser.controlflow.ReturnException;
import com.siiam.engine.scope.MutualScope;
import com.siiam.engine.scope.NestedScope;
import com.siiam.engine.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FunctionExpr implements Expr{
    private String name;
    private Expr params;
    private Expr body;

    @Override
    public Object call(Scope scope, Object[] args) {
        scope = new MutualScope();
        params.assign(scope, args, true);

        try {
            return body.eval(scope);
        }catch (ReturnException e){
            return e.getReturnValue();
        }
    }

    @Override
    public Object eval(Scope scope) {
        return new ScopedExpr(scope, this);
    }

    @Override
    public Expr bind(Scope scope, boolean define) {
        var newFunctionExpr = (FunctionExpr)scope.getObject(name);
        var fnScope = NestedScope.mutual(scope);
        var newParams = params.bind(fnScope, true);
        var newBody = body.bind(fnScope, false);
        newFunctionExpr.setParams(newParams);
        newFunctionExpr.setBody(newBody);
        return newFunctionExpr;
    }

    public void bindFunction(Scope scope) {
        var newFunctionExpr = new FunctionExpr(name, null, null);
        scope.setObject(name, newFunctionExpr, true);
    }
}
