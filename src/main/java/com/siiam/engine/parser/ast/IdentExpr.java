package com.siiam.engine.parser.ast;

import com.siiam.engine.exception.InterpreterException;
import com.siiam.engine.scope.Scope;
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
    public Expr bind(Scope scope, boolean define) {
        if(define){
            scope.setObject(key, null, true);
        }

        var value = scope.getValue(key);
        if(value == null){
            return this;
        }
        var content = value.getValue();
        if(content instanceof FunctionExpr){
            return new LiteralExpr(content);
        }

        return new RefExpr(value);
    }
}
