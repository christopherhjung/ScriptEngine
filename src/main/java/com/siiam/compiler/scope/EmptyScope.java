package com.siiam.compiler.scope;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyScope implements Scope {
    @Override
    public Value getValue(String key) {
        return null;
    }

}
