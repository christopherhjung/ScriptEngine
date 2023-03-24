package com.siiam.compiler.parser.ast;


import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.scope.Scope;

public interface Expr {
    Object eval(Scope scope);
    default Object assign(Scope scope, Object obj){
        throw new InterpreterException("Assign not implemented for " + getClass().getSimpleName());
    }

    default boolean evalBoolean(Scope scope){
        return Boolean.TRUE.equals(eval(scope));
    }
}
