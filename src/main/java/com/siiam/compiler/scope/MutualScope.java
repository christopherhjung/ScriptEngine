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
        if(define){
            map.compute(key, (k, v) -> {
                if(v == null){
                    v = new Value(value);
                }else{
                    v.setContent(value);
                }

                return v;
            });
            return true;
        }

        var wrapper = map.get(key);
        if(wrapper == null){
            return false;
        }

        wrapper.setContent(value);
        return true;
    }
}
