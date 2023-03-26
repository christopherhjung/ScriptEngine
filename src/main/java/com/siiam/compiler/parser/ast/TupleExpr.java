package com.siiam.compiler.parser.ast;

import com.siiam.compiler.Utils;
import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public class TupleExpr implements Expr{
    private Expr[] elems;

    @Override
    public Object eval(Scope scope) {
        var list = new ArrayList<>();
        for( var elem : elems ){
            elem.collect(scope, list::add);
        }
        return list.toArray(Object[]::new);
    }

    @Override
    public Object assign(Scope scope, Object obj, boolean define) {
        var iter = Utils.getIterator(obj);
        for( var elem : elems ){
            if(iter.hasNext()){
                elem.assign(scope, iter.next(), true);
            }else{
                throw new InterpreterException("Expected " + elems.length);
            }
        }

        if(iter.hasNext()){
            throw new InterpreterException("Too many arguments to spread");
        }

        return null;
    }

    @Override
    public void spread(Scope scope, Consumer<Object> sink) {
        for( var elem : elems ){
            elem.collect(scope, sink);
        }
    }

    public static TupleExpr asTuple(Expr expr){
        if(expr instanceof TupleExpr){
            return (TupleExpr) expr;
        }else{
            return new TupleExpr(new Expr[]{expr});
        }
    }
}
