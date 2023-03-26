package com.siiam.compiler.parser.ast;


import com.siiam.compiler.Utils;
import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.parser.ObjectFunction;
import com.siiam.compiler.scope.Scope;

import java.util.function.Consumer;

public interface Expr {
    Object eval(Scope scope);
    default Object call(Scope scope, Object[] args){
        return call(scope, args, false);
    }
    default Object call(Scope scope, Object[] args, boolean optional){
        var callee = eval(scope);

        if(callee == null){
            if(optional) return null;
            throw new InterpreterException("Null pointer exception");
        }

        if(callee instanceof Expr){
            var expr = (Expr) callee;
            return expr.call(scope, args);
        }

        if(callee instanceof ObjectFunction){
            var fn = (ObjectFunction) callee;
            return fn.call(args);
        }

        throw new InterpreterException("Expr is not callable ");
    }

    default Object assign(Scope scope, Object obj, boolean define){
        throw new InterpreterException("Assign not implemented for " + getClass().getSimpleName());
    }

    default void spread(Scope scope, Consumer<Object> sink){
        Utils.getIterator(eval(scope)).forEachRemaining(sink);
    }

    default void collect(Scope scope, Consumer<Object> sink){
        sink.accept(this.eval(scope));
    }

    default boolean evalBoolean(Scope scope){
        return Boolean.TRUE.equals(eval(scope));
    }

    default Expr bind(Scope scope, boolean define){
        return this;
    }

    default boolean isConst(){
        return false;
    }
}
