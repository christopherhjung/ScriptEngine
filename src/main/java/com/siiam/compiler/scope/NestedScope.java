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
    public boolean setObject(String key, Object value, boolean define) {
        if(parent.setObject(key, value, false)){
            return true;
        }

        return child.setObject(key, value, define);
    }

    public static Scope mutual(Scope parent){
        return mutual(parent, new HashMap<>());
    }

    public static Scope mutual(Scope parent, Map<String, Value> map){
        Scope child = new MutualScope(map);
        if(parent != null){
            child = new NestedScope(parent, child);
        }
        return child;
    }

    public static Scope nest(Scope parent, Scope child){
        if(parent != null){
            if(child != null){
                return new NestedScope(parent, child);
            }

            return parent;
        }

        return child;
    }




    public static Scope readonly(Scope scope){
        return new ReadonlyScope(scope);
    }
}
