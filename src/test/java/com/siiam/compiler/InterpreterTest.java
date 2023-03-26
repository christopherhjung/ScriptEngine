package com.siiam.compiler;

import com.siiam.compiler.exception.InterpreterException;
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

public class InterpreterTest {

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
                "fn count(n){let i = 1; while(i < n){ i++ }; i}" +
                        "count(10)"
        );
        assertEquals(10, actualExpr.eval(null));
    }

    @Test(timeout = 1000)
    public void controlFlowFibonacciLoop(){
        var actualExpr = Parser.parse(
                "fn fib(n){let i = 1; let prev = 0; let curr = 1; while(i < n){ let next = prev + curr; prev = curr; curr = next;  i++}; curr}" +
                        "fib(10)"
        );
        assertEquals(55, actualExpr.eval(null));
    }

    @Test
    public void readonlyFunctionCall(){
        var actualExpr = Parser.parse(
                "fn fib(){let i = 100}" +
                        "{let i = 42; fib(); i}"
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

    @Test
    public void spread(){
        var parser = Parser.parse("(1, ...(2,3,4),5)");
        var arr  = (Object[])parser.eval(new MutualScope(new HashMap<>()));
        assertArrayEquals(new Object[]{1,2,3,4,5},  arr);
    }

    @Test
    public void spreadVariable(){
        var parser = Parser.parse("let a = (2,3,4); (1, ...a,5)");
        var arr  = (Object[])parser.eval(new MutualScope(new HashMap<>()));
        assertArrayEquals(new Object[]{1,2,3,4,5},  arr);
    }

    @Test
    public void spreadNested(){
        var parser = Parser.parse("(1, ...(2,...(3,4,5),6),7)");
        var arr  = (Object[])parser.eval(new MutualScope(new HashMap<>()));
        assertArrayEquals(new Object[]{1,2,3,4,5,6,7},  arr);
    }

    @Test
    public void tupleLetSpread(){
        var parser = Parser.parse("let (a,b,c) = (1,...(2,3)); (1,b,3)");
        var arr  = (Object[])parser.eval(new MutualScope(new HashMap<>()));
        assertArrayEquals(new Object[]{1,2,3},  arr);
    }

    @Test
    public void singleTupleItem(){
        var parser = Parser.parse("let (a,) = (42,); a");
        var val  = parser.eval(new MutualScope(new HashMap<>()));
        assertEquals(42,  val);
    }

    @Test
    public void numberAssign(){
        assertThrows(InterpreterException.class, () -> {
            var expr = Parser.parse("let (1,) = (10,)");
            expr.eval(null);
        });
    }

    @Test
    public void breakForLoop(){
        var expr = Parser.parse("let a = 0; for(i in 42..100){ a = i; break }; a");
        assertEquals(42 ,expr.eval(null));
    }

    @Test
    public void breakWhileLoop(){
        var expr = Parser.parse("let a = 41; while(a < 100){ a++; break }; a");
        assertEquals(42 ,expr.eval(null));
    }

    @Test
    public void functionPairReturn(){
        var expr = Parser.parse("fn test(){ (40,2) }; let (a,b) = test(); a + b");
        assertEquals(42 ,expr.eval(null));
    }
}
