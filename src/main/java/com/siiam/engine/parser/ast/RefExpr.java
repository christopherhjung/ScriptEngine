package com.siiam.engine.parser.ast;

import com.siiam.engine.scope.Scope;
import com.siiam.engine.scope.Slot;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RefExpr implements Expr{
    private final Slot slot;

    @Override
    public Object eval(Scope scope) {
        return slot.getValue();
    }

    @Override
    public Object assign(Scope scope, Object value, boolean define) {
        this.slot.setValue(value);
        return value;
    }
}
