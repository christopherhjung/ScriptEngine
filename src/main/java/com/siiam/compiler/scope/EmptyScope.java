package com.siiam.compiler.scope;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyScope implements Scope {
    private final Scope global;
    @Override
    public Value getValue(String key) {
        return null;
    }

}
