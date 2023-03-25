package com.siiam.compiler.parser.ast;


import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.scope.Scope;

import java.util.Arrays;
import java.util.List;

public interface Expr {
    Object eval(Scope scope);
    default Object call(Scope scope, Object[] args){
        throw new InterpreterException("Expr is not callable ");
    }
    default Object assign(Scope scope, Object obj){
        throw new InterpreterException("Assign not implemented for " + getClass().getSimpleName());
    }
    default void spread(Scope scope, List<Object> list){
        var result = eval(scope);
        if(result instanceof Object[]){
            var elems = (Object[]) result;
            list.addAll(Arrays.asList(elems));
            return;
        }else if(result instanceof List<?>){
            var elems = (List<?>) result;
            list.addAll(elems);
            return;
        }
        throw new InterpreterException("Expected array!");
    }
    default void collect(Scope scope, List<Object> list){
        list.add(this.eval(scope));
    }

    default boolean evalBoolean(Scope scope){
        return Boolean.TRUE.equals(eval(scope));
    }
}
