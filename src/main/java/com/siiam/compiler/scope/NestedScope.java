package com.siiam.compiler.scope;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class NestedScope implements Scope {
    private final Scope parent;
    private final Scope child;

    @Override
    public Value getValue(String key) {
        var value = child.getValue(key);

        if(value != null){
            return value;
        }

        return parent.getValue(key);
    }

    @Override
    public boolean setObject(String key, Object value, boolean local) {
        if(parent.setObject(key, value, false)){
            return true;
        }

        return child.setObject(key, value, local);
    }

    public static Scope wrapMutual(Scope parent){
        return wrapMutual(parent, new HashMap<>());
    }

    public static Scope wrapMutual(Scope parent, Map<String, Value> map){
        Scope child = new MutualScope(map);
        if(parent != null){
            child = new NestedScope(parent, child);
        }
        return child;
    }

    public static Scope wrapReadonly(Scope scope){
        return new ReadonlyScope(scope);
    }
}
