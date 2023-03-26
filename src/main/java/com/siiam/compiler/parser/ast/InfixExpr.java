package com.siiam.compiler.parser.ast;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.parser.Op;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.IntStream;

@AllArgsConstructor
@Getter
public class InfixExpr implements Expr{
    private Expr lhs;
    private Expr rhs;
    private Op op;

    private int compare(Object lhs, Object rhs){
        if(lhs instanceof String && rhs instanceof String){
            return ((String)lhs).compareTo((String)rhs);
        }else if(lhs instanceof Integer && rhs instanceof Integer){
            return ((Integer)lhs).compareTo((Integer)rhs);
        }

        throw new InterpreterException("Expected two String or Integer for comparison!");
    }

    private Object add(Object lhs, Object rhs){
        if(lhs instanceof Integer && rhs instanceof Integer){
            return ((Integer)lhs) + ((Integer)rhs);
        }else if(lhs instanceof String){
            return lhs.toString() + rhs.toString();
        }else if(rhs instanceof String){
            return lhs.toString() + rhs.toString();
        }

        throw new InterpreterException("Expected two Integer or any String for addition!");
    }

    private Object sub(Object lhs, Object rhs){
        if(lhs instanceof Integer && rhs instanceof Integer){
            return ((Integer)lhs) - ((Integer)rhs);
        }

        throw new InterpreterException("Expected two Integer for subtraction!");
    }

    private Object mul(Object lhs, Object rhs){
        if(lhs instanceof Integer && rhs instanceof Integer){
            return ((Integer)lhs) * ((Integer)rhs);
        }

        throw new InterpreterException("Expected two Integer for multiplication!");
    }

    private Object div(Object lhs, Object rhs){
        if(lhs instanceof Integer && rhs instanceof Integer){
            return ((Integer)lhs) / ((Integer)rhs);
        }

        throw new InterpreterException("Expected two Integer for division!");
    }

    private Object pow(Object lhs, Object rhs){
        if(lhs instanceof Integer && rhs instanceof Integer){
            return (int)Math.pow(((Integer)lhs), ((Integer)rhs));
        }

        throw new InterpreterException("Expected two Integer for division!");
    }

    private Object and(Object lhs, Object rhs){
        if(lhs instanceof Integer && rhs instanceof Integer){
            return ((Integer)lhs) & ((Integer)rhs);
        }

        throw new InterpreterException("Expected two Integer for division!");
    }

    private Object or(Object lhs, Object rhs){
        if(lhs instanceof Integer && rhs instanceof Integer){
            return ((Integer)lhs) | ((Integer)rhs);
        }

        throw new InterpreterException("Expected two Integer for division!");
    }

    private Object xor(Object lhs, Object rhs){
        if(lhs instanceof Integer && rhs instanceof Integer){
            return ((Integer)lhs) ^ ((Integer)rhs);
        }

        throw new InterpreterException("Expected two Integer for division!");
    }

    private Object range(Object lhs, Object rhs){
        if(lhs instanceof Integer && rhs instanceof Integer){
            return IntStream.range((Integer)lhs, (Integer)rhs).boxed();
        }

        throw new InterpreterException("Expected two Integer for range!");
    }

    @Override
    public Object eval(Scope scope) {
        if (op == Op.Assign) {
            var rhsVal = rhs.eval(scope);
            return lhs.assign(scope, rhsVal, false);
        }

        var lhsVal = lhs.eval(scope);

        switch (op){
            case And: return Boolean.TRUE.equals(lhsVal) && Boolean.TRUE.equals(rhs.eval(scope));
            case Or: return Boolean.TRUE.equals(lhsVal) || Boolean.TRUE.equals(rhs.eval(scope));
            case Nullish: return lhsVal == null ? rhs.eval(scope) : lhsVal;
        }

        var rhsVal = rhs.eval(scope);

        switch (op){
            case Eq: return Objects.equals(lhsVal, rhsVal);
            case Ne: return !Objects.equals(lhsVal, rhsVal);
            case Lt: return compare(lhsVal, rhsVal) == -1;
            case Le: return compare(lhsVal, rhsVal) != 1;
            case Gt: return compare(lhsVal, rhsVal) == 1;
            case Ge: return compare(lhsVal, rhsVal) != -1;
            case Add: return add(lhsVal, rhsVal);
            case Sub: return sub(lhsVal, rhsVal);
            case Mul: return mul(lhsVal, rhsVal);
            case Div: return div(lhsVal, rhsVal);
            case Pow: return pow(lhsVal, rhsVal);
            case BitAnd: return and(lhsVal, rhsVal);
            case BitOr: return or(lhsVal, rhsVal);
            case BitXor: return xor(lhsVal, rhsVal);
            case Range: return range(lhsVal, rhsVal);
            case AssignAdd: return lhs.assign(scope, add(lhsVal, rhsVal), false);
            case AssignSub: return lhs.assign(scope, sub(lhsVal, rhsVal), false);
            case AssignMul: return lhs.assign(scope, mul(lhsVal, rhsVal), false);
            case AssignDiv: return lhs.assign(scope, div(lhsVal, rhsVal), false);
        }

        throw new InterpreterException("Not implemented " + op + " operation!");
    }

    @Override
    public Expr bind(Scope scope, boolean define) {
        var newLhs = lhs.bind(scope, false);
        var newRhs = rhs.bind(scope, false);
        var newExpr = new InfixExpr(newLhs, newRhs, op);

        if(newLhs instanceof LiteralExpr && rhs instanceof LiteralExpr){
            var newValue = newExpr.eval(scope);
            return new LiteralExpr(newValue);
        }

        return newExpr;
    }
}
