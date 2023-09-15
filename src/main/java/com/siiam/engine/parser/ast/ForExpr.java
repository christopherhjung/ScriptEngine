package com.siiam.engine.parser.ast;

import com.siiam.engine.Utils;
import com.siiam.engine.parser.controlflow.BreakException;
import com.siiam.engine.parser.controlflow.ContinueException;
import com.siiam.engine.scope.NestedScope;
import com.siiam.engine.scope.Scope;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class ForExpr implements Expr{
    private final Expr variable;
    private final Expr range;
    private final Expr body;
    private final String label;

    @Override
    public Object eval(Scope scope) {
        var iterable = Utils.getIterator(range.eval(scope));

        scope = NestedScope.mutual(scope);
        while(iterable.hasNext()){
            try{
                var value = iterable.next();
                variable.assign(scope, value, true);
                body.eval(scope);
            }catch (ContinueException e){
                if(!Objects.equals(e.getLabel(), label)){
                    throw e;
                }
            }catch (BreakException e){
                if(Objects.equals(e.getLabel(), label)){
                    break;
                }else{
                    throw e;
                }
            }
        }

        return null;
    }

    @Override
    public Expr bind(Scope scope, boolean define) {
        var newVariable = variable.bind(scope, true);
        var newRange = range.bind(scope, false);
        var newBody = body.bind(scope, false);
        return new ForExpr(newVariable, newRange, newBody, label);
    }
}
