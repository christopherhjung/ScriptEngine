package com.siiam.engine.scope;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReadonlyScope implements Scope {
    private Scope scope;

    @Override
    public Slot getValue(String key) {
        return scope.getValue(key);
    }
}
