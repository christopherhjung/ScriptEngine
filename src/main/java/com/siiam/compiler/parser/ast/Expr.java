package com.siiam.compiler.parser.ast;


import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.scope.Scope;

import java.util.List;
import java.util.function.Consumer;

public interface Expr {
    Object eval(Scope scope);
    default Object call(Scope scope, Object[] args){
        throw new InterpreterException("Expr is not callable ");
    }

    default Object assign(Scope scope, Object obj, boolean define){
        throw new InterpreterException("Assign not implemented for " + getClass().getSimpleName());
    }

    default void spread(Scope scope, Consumer<Object> list){
        var result = eval(scope);
        if(result instanceof Object[]){
            var elems = (Object[]) result;
            for(var elem : elems){
                list.accept(elem);
            }
            return;
        }else if(result instanceof List<?>){
            var elems = (List<?>) result;
            elems.forEach(list::accept);
            return;
        }
        throw new InterpreterException("Expected array!");
    }

    default void collect(Scope scope, Consumer<Object> list){
        list.accept(this.eval(scope));
    }

    default boolean evalBoolean(Scope scope){
        return Boolean.TRUE.equals(eval(scope));
    }
}
