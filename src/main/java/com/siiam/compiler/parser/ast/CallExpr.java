package com.siiam.compiler.parser.ast;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.parser.ObjectFunction;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;


@AllArgsConstructor
@RequiredArgsConstructor
public class CallExpr implements Expr{
    private final Expr callee;
    private final Expr arg;
    boolean optional = false;

    @Override
    public Object eval(Scope scope) {
        var argVal = (Object[])arg.eval(scope);
        return callee.call(scope, argVal, optional);
    }

    @Override
    public Expr bind(Scope scope, boolean define) {
        var newCallee = callee.bind(scope, false);
        var newArg = arg.bind(scope, false);
        return new CallExpr(newCallee, newArg, optional);
    }
}

