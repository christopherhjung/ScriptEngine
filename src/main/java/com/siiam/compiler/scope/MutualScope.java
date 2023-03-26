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
