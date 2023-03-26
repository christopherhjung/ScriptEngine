package com.siiam.compiler;

import com.siiam.compiler.exception.ParseException;
import com.siiam.compiler.parser.Op;
import com.siiam.compiler.parser.Parser;
import com.siiam.compiler.parser.ast.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class ReduceTest {
    @Test
    public void addReduce(){
        var actualExpr = Parser.parse("40 + 2");
        var expectedExpr = number(42);
        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void mulReduce(){
        var actualExpr = Parser.parse("21 * 2");
        var expectedExpr = number(42);
        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void deepReduce(){
        var actualExpr = Parser.parse("(20 + 1) * 2");
        var expectedExpr = number(42);
        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void zeroMul(){
        var actualExpr = Parser.parse("0 * A");
        var expectedExpr = number(0);
        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void oneMul(){
        var actualExpr = Parser.parse("1 * A");
        var expectedExpr = id("A");
        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void addZero(){
        var actualExpr = Parser.parse("0 + A");
        var expectedExpr = id("A");
        assertIdentical(expectedExpr, actualExpr);
    }

    private Expr id(String name){
        return new IdentExpr(name);
    }

    private Expr str(String name){
        return new LiteralExpr(name);
    }

    private Expr number(int number){
        return new LiteralExpr(number);
    }

    private Expr sub(Expr lhs){
        return new PrefixExpr(lhs, Op.Sub);
    }

    private Expr add(Expr lhs, Expr rhs){
        return new InfixExpr(lhs, rhs, Op.Add);
    }

    private Expr mul(Expr lhs, Expr rhs){
        return new InfixExpr(lhs, rhs, Op.Mul);
    }

    private Expr eq(Expr lhs, Expr rhs){
        return new InfixExpr(lhs, rhs, Op.Eq);
    }

    private Expr fn(Expr callee, Expr... arg){
        return new CallExpr(callee, new TupleExpr(arg));
    }

    private Expr block(Expr expr){
        return new BlockExpr(new Expr[]{expr});
    }

    private void assertIdentical(Object expected, Object actual){
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
