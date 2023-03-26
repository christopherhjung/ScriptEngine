package com.siiam.compiler.parser.ast;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IdentExpr implements Expr{
    private final String key;

    @Override
    public Object eval(Scope scope) {
        return scope.getObject(key);
    }

    @Override
    public Object assign(Scope scope, Object obj, boolean define) {
        if(!scope.setObject(key, obj, define)){
            throw new InterpreterException("Assign was not successful");
        }
        return obj;
    }

    @Override
    public Expr reduce(Scope scope) {
        var value = scope.getValue(key);
        if(value == null){
            return this;
        }
        return new ValueExpr(value);
    }

    @Override
    public Expr bind(Scope scope) {
        scope.setObject(key, null, true);
        return reduce(scope);
    }
}
