package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IfExpr implements Expr{
    private Expr condition;
    private Expr trueBranch;
    private Expr falseBranch;

    @Override
    public Object eval(Scope scope) {
        if(condition.evalBoolean(scope)){
            return trueBranch.eval(scope);
        }else if(falseBranch != null){
            return falseBranch.eval(scope);
        }

        return null;
    }
}
