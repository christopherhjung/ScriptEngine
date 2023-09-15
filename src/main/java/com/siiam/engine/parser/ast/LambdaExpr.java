package com.siiam.engine.parser.ast;

import com.siiam.engine.parser.controlflow.ReturnException;
import com.siiam.engine.scope.NestedScope;
import com.siiam.engine.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LambdaExpr implements Expr{
    private Expr param;
    private Expr body;

    @Override
    public Object call(Scope scope, Object[] args) {
        scope = NestedScope.mutual(scope);
        param.assign(scope, args, true);

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
        scope = NestedScope.readonly(scope);
        scope = NestedScope.mutual(scope);
        var newParam = param.bind(scope, true);
        var newBody = body.bind(scope, false);
        return new LambdaExpr(newParam, newBody);
    }
}
