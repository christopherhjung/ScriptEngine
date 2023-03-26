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

    @Override
    public Expr reduce(Scope scope) {
        var newCondition = condition.reduce(scope);
        var newTrueBranch = trueBranch.reduce(scope);
        var newFalseBranch = falseBranch == null ? null : falseBranch.reduce(scope);

        if(newCondition instanceof LiteralExpr){
            if(newCondition.evalBoolean(null)){
                return newTrueBranch;
            }else{
                return newFalseBranch;
            }
        }

        return new IfExpr(newCondition, newTrueBranch, newFalseBranch);
    }
}
