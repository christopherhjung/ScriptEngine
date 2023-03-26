package com.siiam.compiler.scope;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public class MutualScope implements Scope {
    private final Map<String, Value> map;

    public MutualScope(Map<String, Value> map) {
        this.map = map;
    }

    public MutualScope() {
        this(new HashMap<>());
    }

    @Override
    public Value getValue(String key) {
        return map.get(key);
    }

    @Override
    public boolean setObject(String key, Object value, boolean define) {
        var wrapper = map.get(key);
        if(wrapper == null){
            if(define){
                wrapper = new Value(value);
                map.put(key, wrapper);
                return true;
            }else return false;
        }

        wrapper.setContent(value);
        return true;
    }
}
