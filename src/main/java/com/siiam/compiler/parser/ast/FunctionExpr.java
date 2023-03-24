package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.NestedScope;
import com.siiam.compiler.scope.Scope;
import com.siiam.compiler.parser.controlflow.ReturnException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FunctionExpr implements Expr{
    private String name;
    private String[] params;
    private Expr body;

    @Override
    public Object eval(Scope scope) {
        scope = NestedScope.wrapMutual(scope);

        try {
            return body.eval(scope);
        }catch (ReturnException e){
            return e.getReturnValue();
        }
    }
}
