package com.siiam.compiler.optimizer;

import com.siiam.compiler.parser.ast.Expr;
import com.siiam.compiler.parser.ast.InfixExpr;
import com.siiam.compiler.parser.ast.LetExpr;
import com.siiam.compiler.scope.MutualScope;
import com.siiam.compiler.scope.Scope;

public class Optimizer {
    private Scope scope = new MutualScope();

    public Expr optimize(Expr expr){
        if( expr instanceof LetExpr){
            var letExpr = (LetExpr)expr;

        }


        return null;
    }
}
