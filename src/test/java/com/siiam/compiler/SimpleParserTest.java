package com.siiam.compiler;

import com.siiam.compiler.exception.ParseException;
import com.siiam.compiler.parser.Op;
import com.siiam.compiler.parser.Parser;
import com.siiam.compiler.parser.ast.*;
import com.siiam.compiler.scope.MutualScope;
import com.siiam.compiler.scope.StaticScope;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class SimpleParserTest {

    @Test
    public void simpleParserTest(){
        var actualExpr = Parser.parse("Result == \"2\"");
        var expectedExpr = eq(id("Result"), str("2"));

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void precAddMul(){
        var actualExpr = Parser.parse("A + B * C");
        var expectedExpr = add(
            id("A"),
            mul(id("B"), id("C"))
        );

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void precMulAdd(){
        var actualExpr = Parser.parse("A * B + C");
        var expectedExpr = add(
            mul(id("A"), id("B")),
            id("C")
        );

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void precParent(){
        var actualExpr = Parser.parse("A * (B + C)");
        var expectedExpr = mul(
                id("A"),
                add(id("B"), id("C"))
        );

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void unaryMinus(){
        var actualExpr = Parser.parse("-A + B");
        var expectedExpr = add(
            sub(id("A")),
            id("B")
        );

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void callExpr(){
        var actualExpr = Parser.parse("test(1, 2 + 3)");
        var expectedExpr = fn(id("test"), number(1), add(number(2), number(3)));

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void blockWithSemi(){
        var actualExpr = Parser.parse("{;;i;;}");
        var expectedExpr = block(id("i"));

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void numberAssign(){
        assertThrows(ParseException.class, () -> {
            Parser.parse("let 1 = 10");
        });
    }

    private Expr id(String name){
        return new IdentExpr(name);
    }

    private Expr str(String name){
        return new StringExpr(name);
    }

    private Expr number(int number){
        return new IntExpr(number);
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
