package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public class TupleExpr implements Expr{
    private Expr[] elems;

    @Override
    public Object eval(Scope scope) {
        var list = new ArrayList<>();
        for( var elem : elems ){
            elem.collect(scope, list::add);
        }
        return list.toArray(Object[]::new);
    }

    @Override
    public void spread(Scope scope, Consumer<Object> list) {
        for( var elem : elems ){
            elem.collect(scope, list);
        }
    }
}
