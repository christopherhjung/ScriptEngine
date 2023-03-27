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
        return new CallExpr(this, new LiteralExpr(args), optional)
                .eval(scope);
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
