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
    private Expr[] params;
    private Expr body;

    @Override
    public Object call(Scope scope, Object[] args) {
        if(args.length != params.length){
            throw new InterpreterException("Mismatch arg length");
        }

        scope = NestedScope.wrapReadonly(scope);
        scope = NestedScope.wrapMutual(scope);
        var idx = 0;
        for(var param : params){
            param.assign(scope, args[idx++], true);
        }

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
}
