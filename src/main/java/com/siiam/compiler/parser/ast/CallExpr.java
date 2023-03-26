package com.siiam.compiler.parser.ast;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.parser.ObjectFunction;
import com.siiam.compiler.scope.Scope;
import com.siiam.compiler.scope.Value;
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
        var newCallee = callee.bind(scope, define);
        /*if(arg.isConst()){
            var argVal = arg.eval(scope);
            var result = newCallee.call(scope, (Object[]) argVal, optional);
            return new LiteralExpr(result);
        }*/
        var newArg = arg.bind(scope, define);
        return new CallExpr(newCallee, newArg, optional);
    }
}

