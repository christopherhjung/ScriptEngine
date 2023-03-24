package com.siiam.compiler;

import com.siiam.compiler.parser.Op;
import com.siiam.compiler.parser.Parser;
import com.siiam.compiler.parser.ast.*;
import com.siiam.compiler.scope.StaticScope;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void simpleParserTest(){
        var actualExpr = Parser.parse("Result == \"2\"");
        var expectedExpr = prog(eq(id("Result"), str("2")));

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void simpleEvalTest(){
        var expr = Parser.parse("Result == \"2\"");
        var scope = StaticScope.builder().add("Result", "2").build();
        assertSame(Boolean.TRUE, expr.eval(scope));
    }

    @Test
    public void simpleEvalFailTest(){
        var expr = Parser.parse("Result == \"2\"");
        var scope = StaticScope.builder().add("Result", "3").build();
        assertSame(Boolean.FALSE, expr.eval(scope));
    }

    @Test
    public void nullEvalTest(){
        var expr = Parser.parse(null);
        var scope = StaticScope.builder().add("Result", "3").build();
        assertSame(Boolean.TRUE, expr.eval(scope));
    }

    @Test
    public void trueBoolExpr(){
        var expr = Parser.parse("true");
        assertSame(Boolean.TRUE, expr.eval(null));
    }

    @Test
    public void falseBoolExpr(){
        var expr = Parser.parse("false");
        assertSame(Boolean.FALSE, expr.eval(null));
    }

    @Test
    public void precAddMul(){
        var actualExpr = Parser.parse("A + B * C");
        var expectedExpr = prog(add(
            id("A"),
            mul(id("B"), id("C"))
        ));

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void precMulAdd(){
        var actualExpr = Parser.parse("A * B + C");
        var expectedExpr = prog(add(
            mul(id("A"), id("B")),
            id("C")
        ));

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void precParent(){
        var actualExpr = Parser.parse("A * (B + C)");
        var expectedExpr = prog(mul(
                id("A"),
                add(id("B"), id("C"))
        ));

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void unaryMinus(){
        var actualExpr = Parser.parse("-A + B");
        var expectedExpr = prog(add(
            sub(id("A")),
            id("B")
        ));

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void callExpr(){
        var actualExpr = Parser.parse("test(1, 2 + 3)");
        var expectedExpr = prog(fn(id("test"), number(1), add(number(2), number(3))));

        assertIdentical(expectedExpr, actualExpr);
    }

    @Test
    public void addition(){
        var actualExpr = Parser.parse("2 + 3");
        assertEquals(5, actualExpr.eval(null));
    }

    @Test
    public void concat(){
        var actualExpr = Parser.parse("\"2\" + \"3\"");
        assertEquals("23", actualExpr.eval(null));
    }

    private Object testFn(Object[] arg){
        assertNotNull(arg);
        assertEquals(2, arg.length);
        assertEquals(arg[0], arg[1]);
        return Objects.equals(arg[0], arg[1]);
    }

    @Test
    public void callExprRun(){
        var actualExpr = Parser.parse("test(5, 2 + 3)");
        var scope = StaticScope.builder().addMethod("test", this::testFn).build();
        assertSame(Boolean.TRUE, actualExpr.eval(scope));
    }

    @Test
    public void utilsReflect(){
        var actualExpr = Parser.parse("StringUtils.isBlank(\"     \")");
        var scope = StaticScope.builder().addStaticMethods(StringUtils.class).build();
        assertEquals(Boolean.TRUE, actualExpr.eval(scope));
    }

    @Test
    public void fieldExprMap(){
        var actualExpr = Parser.parse("param.min + \"!\"");
        var scope = StaticScope.builder().add("param", Map.of("min", "3")).build();
        assertEquals("3!", actualExpr.eval(scope));
    }

    @Test
    public void functionExpr(){
        var actualExpr = Parser.parse("test(1,2)");
        var func = new FunctionExpr("test", new String[]{"a", "b"}, Parser.parse("a + b"));
        var scope = StaticScope.builder().add("test", func).build();
        assertEquals(3, actualExpr.eval(scope));
    }

    @Test
    public void functionParserExpr(){
        var actualExpr = Parser.parse(
                "fn test(a,b){a+b}" +
                        "test(1,2)"
        );
        assertEquals(3, actualExpr.eval(null));
    }

    @Test
    public void controlFlowIf(){
        var actualExpr = Parser.parse(
                "fn max(a,b){if(a > b){a}else{b}}" +
                        "max(1,2)"
        );
        assertEquals(2, actualExpr.eval(null));
    }

    @Test
    public void controlFlowReturn(){
        var actualExpr = Parser.parse(
                "fn test(a,b){if(a > b){return a}; b}" +
                        "test(1,2)"
        );
        assertEquals(2, actualExpr.eval(null));
    }

    @Test
    public void controlFlowFibonacci(){
        var actualExpr = Parser.parse(
                "fn fib(a){if(a <= 2){1}else{fib(a-1) + fib(a-2)}}" +
                        "fib(10)"
        );
        assertEquals(55, actualExpr.eval(null));
    }

    @Test(timeout = 1000)
    public void controlFlowLoop(){
        var actualExpr = Parser.parse(
                "fn count(n){i = 1; while(i < n){ i++ }; i}" +
                        "count(10)"
        );
        assertEquals(10, actualExpr.eval(null));
    }

    @Test(timeout = 1000)
    public void controlFlowFibonacciLoop(){
        var actualExpr = Parser.parse(
                "fn fib(n){i = 1; prev = 0; curr = 1; while(i < n){ next = prev + curr; prev = curr; curr = next;  i++}; curr}" +
                        "fib(10)"
        );
        assertEquals(55, actualExpr.eval(null));
    }

    @Test
    public void readonlyFunctionCall(){
        var actualExpr = Parser.parse(
                "fn fib(){i = 100}" +
                        "{i = 42; fib(); i}"
        );
        assertEquals(42, actualExpr.eval(null));
    }

    @Test
    public void nullish(){
        var actualExpr = Parser.parse(
            "null ?? 0"
        );
        assertEquals(0, actualExpr.eval(null));
    }

    @Test
    public void chaining(){
        var actualExpr = Parser.parse(
                "null?.test?.lol ?? 2"
        );
        assertEquals( 2, actualExpr.eval(null));
    }

    @Test
    public void chainingCall(){
        var actualExpr = Parser.parse(
            "null?.test?.() ?? 2"
        );
        assertEquals( 2, actualExpr.eval(null));
    }

    @Test
    public void pow(){
        var actualExpr = Parser.parse(
            "3 ** 4"
        );
        assertEquals( 81, actualExpr.eval(null));
    }

    @Test
    public void not(){
        var actualExpr = Parser.parse(
                "!false"
        );
        assertEquals( true, actualExpr.eval(null));
    }

    @Test
    public void and(){
        var actualExpr = Parser.parse(
                "1 & 3"
        );
        assertEquals( 1, actualExpr.eval(null));
    }

    @Test
    public void or(){
        var actualExpr = Parser.parse(
                "1 | 2"
        );
        assertEquals( 3, actualExpr.eval(null));
    }

    @Test
    public void xor(){
        var actualExpr = Parser.parse(
                "3 ^ 1"
        );
        assertEquals( 2, actualExpr.eval(null));
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

    private Expr prog(Expr expr){
        return new ProgramExpr(new FunctionExpr[0], new BlockExpr(new Expr[]{expr}));
    }

    private void assertIdentical(Object expected, Object actual){
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
