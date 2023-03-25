package com.siiam.compiler.parser.ast;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.scope.Scope;
import com.siiam.compiler.parser.ObjectFunction;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;


@AllArgsConstructor
@RequiredArgsConstructor
public class CallExpr implements Expr{
    private final Expr callee;
    private final TupleExpr arg;
    boolean optional = false;

    @Override
    public Object eval(Scope scope) {
        var calleeVal = callee.eval(scope);

        if(calleeVal == null){
            if(optional) return null;
            throw new InterpreterException("Null pointer exception");
        }

        var argVal = (Object[])arg.eval(scope);

        if(calleeVal instanceof Expr){
            var expr = (Expr) calleeVal;
            return expr.call(scope, argVal);
        }

        if(calleeVal instanceof ObjectFunction){
            var fn = (ObjectFunction) calleeVal;
            return fn.call(argVal);
        }

        throw new InterpreterException("Expected Object Function Callee");
    }
}

