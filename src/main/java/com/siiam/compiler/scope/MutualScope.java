package com.siiam.compiler.scope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MutualScope implements Scope {
    private final Map<String, Slot> map;

    public MutualScope(Map<String, Slot> map) {
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
    public Collection<Slot> values(){
        return map.values();
    }

    @Override
    public Slot getValue(String key) {
        return map.get(key);
    }

    @Override
    public boolean setObject(String key, Object value, boolean define) {
        var wrapper = map.get(key);
        if(wrapper == null){
            if(define){
                wrapper = new Slot(value);
                map.put(key, wrapper);
                return true;
            }else return false;
        }

        wrapper.setValue(value);
        return true;
    }
}
