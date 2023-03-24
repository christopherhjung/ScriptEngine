package com.siiam.compiler.scope;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class MutualScope implements Scope {
    private final Map<String, Value> map;

    @Override
    public Value getValue(String key) {
        return map.get(key);
    }

    @Override
    public boolean setObject(String key, Object value, boolean local) {
        if(local || map.containsKey(key)){
            map.put(key, new Value(value));
            return true;
        }

        return false;
    }
}
