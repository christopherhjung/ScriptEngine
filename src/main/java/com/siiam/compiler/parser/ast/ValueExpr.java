package com.siiam.compiler.parser.ast;

import com.siiam.compiler.scope.Scope;
import com.siiam.compiler.scope.Value;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValueExpr implements Expr{
    private final Value value;

    @Override
    public Object eval(Scope scope) {
        return value.getContent();
    }

    @Override
    public Object assign(Scope scope, Object obj) {
        value.setContent(obj);
        return obj;
    }
}
