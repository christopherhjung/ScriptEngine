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
    private boolean optional = false;

    @Override
    public Object eval(Scope scope) {
        var callee = this.callee.eval(scope);

        if(callee == null){
            if(optional) return null;
            throw new InterpreterException("Null pointer exception");
        }

        var arg = (Object[]) this.arg.eval(scope);
        if(callee instanceof Expr){
            var expr = (Expr) callee;
            return expr.call(scope, arg);
        }else if(callee instanceof ObjectFunction){
            var fn = (ObjectFunction) callee;
            return fn.call(arg);
        }

        throw new InterpreterException("is not callable!");
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

