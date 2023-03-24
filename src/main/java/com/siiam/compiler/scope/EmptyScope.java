package com.siiam.compiler.scope;

public class EmptyScope implements Scope {
    @Override
    public Value getValue(String key) {
        return null;
    }
}
