package com.siiam.compiler.scope;

import com.siiam.compiler.exception.InterpreterException;

import java.util.Collection;
import java.util.Collections;

public interface Scope {
    default Object getObject(String key){
        var value = getValue(key);
        if(value == null){
            throw new InterpreterException("Expected value of " + key);
        }

        return value.getValue();
    }
    Slot getValue(String key);
    default Collection<Slot> values(){
        return Collections.emptyList();
    }

    default boolean setObject(String key, Object value, boolean define){
        if(define){
            throw new InterpreterException("Store of " + key + " not supported");
        }

        return false;
    }
}
