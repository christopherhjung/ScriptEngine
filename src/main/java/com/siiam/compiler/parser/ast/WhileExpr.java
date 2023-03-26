package com.siiam.compiler.parser.ast;

import com.siiam.compiler.parser.controlflow.BreakException;
import com.siiam.compiler.parser.controlflow.ContinueException;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
public class WhileExpr implements Expr{
    private final Expr condition;
    private final Expr body;
    private String label = null;

    @Override
    public Object eval(Scope scope) {
        while(condition.evalBoolean(scope)){
            try{
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
        var newCondition = condition.bind(scope, false);
        var newBody = body.bind(scope, false);
        return new WhileExpr(newCondition, newBody, label);
    }
}
