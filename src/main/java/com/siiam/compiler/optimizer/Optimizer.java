package com.siiam.compiler.optimizer;

import com.siiam.compiler.parser.ast.Expr;
import com.siiam.compiler.parser.ast.InfixExpr;

public class Optimizer {

    public Expr optimize(Expr expr){
        if( expr instanceof InfixExpr ){
            var infix = (InfixExpr)expr;

            var lhs = infix.getLhs();
            var rhs = infix.getRhs();

        }

        return null;
    }
}
