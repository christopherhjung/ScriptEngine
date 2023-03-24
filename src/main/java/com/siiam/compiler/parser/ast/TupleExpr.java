package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class TupleExpr implements Expr{
    private Expr[] elem;

    @Override
    public Object eval(Scope scope) {
        return Arrays.stream(elem)
                .map(it -> it.eval(scope))
                .toArray();
    }
}
