package com.siiam.engine.parser.ast;

import com.siiam.engine.scope.Scope;
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
    public Expr bind(Scope scope, boolean define) {
        var newCondition = condition.bind(scope, false);
        var newTrueBranch = trueBranch.bind(scope, false);
        var newFalseBranch = falseBranch == null ? null : falseBranch.bind(scope,false);

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
