package com.siiam.compiler.parser.ast;

import com.siiam.compiler.parser.controlflow.ReturnException;
import com.siiam.compiler.scope.NestedScope;
import com.siiam.compiler.scope.Scope;
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
        scope = NestedScope.readonly(scope);
        scope = NestedScope.mutual(scope);
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
        var newParams = params.bind(scope, true);
        var newFunctionExpr = new FunctionExpr(name, newParams, null);
        scope.setObject(name, newFunctionExpr, true);
        scope = NestedScope.readonly(scope);
        scope = NestedScope.mutual(scope);
        var newBody = body.bind(scope, false);
        newFunctionExpr.setBody(newBody);
        return new FunctionExpr(name, newParams, newBody);
    }
}
