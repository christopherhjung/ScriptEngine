package com.siiam.engine.parser.ast;

import com.siiam.engine.exception.InterpreterException;
import com.siiam.engine.parser.Op;
import com.siiam.engine.scope.Scope;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

@AllArgsConstructor
public class PrefixExpr implements Expr{
    private Expr expr;
    private Op op;

    private Object sub(Object value){
        if(value instanceof Integer){
            return -((Integer) value);
        }

        throw new InterpreterException("Expected integer for unary sub!");
    }

    @Override
    public Object eval(Scope scope) {
        var val = expr.eval(scope);

        switch (op){
            case Sub: return sub(val);
            case Add: return val;
            case Not: return Boolean.FALSE.equals(val);
        }

        throw new InterpreterException("Not implemented " + op + " operation!");
    }

    @Override
    public void collect(Scope scope, Consumer<Object> sink) {
        if(op == Op.Spread){
            expr.spread(scope, sink);
        }else{
            Expr.super.collect(scope, sink);
        }
    }

    @Override
    public Expr bind(Scope scope, boolean define) {
        var newExpr = expr.bind(scope, false);
        return new PrefixExpr(newExpr, op);
    }
}
