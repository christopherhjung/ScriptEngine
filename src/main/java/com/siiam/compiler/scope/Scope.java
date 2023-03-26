package com.siiam.compiler.scope;

import com.siiam.compiler.exception.InterpreterException;

public interface Scope {
    default Object getObject(String key){
        var value = getValue(key);
        if(value == null){
            throw new InterpreterException("Expected value of " + key);
        }

        return value.getContent();
    }
    Value getValue(String key);

    default boolean setObject(String key, Object value){
        return setObject(key, value, true);
    }

    default boolean setObject(String key, Object value, boolean define){
        if(define){
            throw new InterpreterException("Store of " + key + " not supported");
        }

        return false;
    }
}
