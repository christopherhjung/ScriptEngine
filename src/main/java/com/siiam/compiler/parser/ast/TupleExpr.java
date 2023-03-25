package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public class TupleExpr implements Expr{
    private Expr[] elems;

    @Override
    public Object eval(Scope scope) {
        return Arrays.stream(elems)
                .map(it -> it.eval(scope))
                .toArray();
    }
}
