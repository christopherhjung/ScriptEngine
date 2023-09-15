package com.siiam.engine.scope;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyScope implements Scope {
    @Override
    public Slot getValue(String key) {
        return null;
    }

}
