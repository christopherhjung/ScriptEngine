package com.siiam.compiler.scope;

import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MutualScope implements Scope {
    private final Map<String, Value> map;

    public MutualScope(Map<String, Value> map) {
        this.map = map;
    }

    public MutualScope(Scope global) {
        this(new HashMap<>());
    }
    public MutualScope() {
        this(new HashMap<>());
    }

    public StaticScope toStatic(){
        return new StaticScope(new HashMap<>(map));
    }

    @Override
    public Collection<Value> values(){
        return map.values();
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
