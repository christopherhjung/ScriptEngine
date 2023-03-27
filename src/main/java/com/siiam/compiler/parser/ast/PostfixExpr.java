package com.siiam.compiler.parser.ast;

import com.siiam.compiler.parser.Op;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostfixExpr implements Expr{
    private Expr expr;
    private Op op;

    @Override
    public Object eval(Scope scope) {
        var val = expr.eval(scope);
        if(val instanceof Integer){
            var valInt = (Integer) val;
            if(op == Op.Inc){
                valInt++;
            }else if(op == Op.Dec){
                valInt--;
            }
            expr.assign(scope, valInt, false);
        }
        return val;
    }

    @Override
    public Expr bind(Scope scope, boolean define) {
        var newExpr = expr.bind(scope, false);
        return new PostfixExpr(newExpr, op);
    }
}
