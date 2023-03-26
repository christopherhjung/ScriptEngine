package com.siiam.compiler.parser.ast;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.parser.controlflow.ReturnException;
import com.siiam.compiler.scope.NestedScope;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
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
    public Expr reduce(Scope scope) {
        var newParams = params.bind(scope);
        var newBody = body.reduce(scope);
        return new FunctionExpr(name, newParams, newBody);
    }
}
