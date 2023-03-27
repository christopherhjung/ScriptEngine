package com.siiam.compiler;

import com.siiam.compiler.parser.Parser;
import com.siiam.compiler.scope.MutualScope;

public class Main {
    public static void main(String[] args) {
        run(100000000);
        runForLoop(100000000);
        runTuple(100000000);
        runNative(100000000);
    }

    public static void run(int n){
        var fib = Parser.parse(
                "fn fib(n){let i = 1; let prev = 0; let curr = 1; while(i < n){ let next = prev + curr; prev = curr; curr = next;  i++}; curr}" +
                        "fib"
        );
        long start = System.currentTimeMillis();
        var val = fib.call(null, new Object[]{n});
        long end = System.currentTimeMillis();
        System.out.printf("Time siiam: %d with value %d\n" ,end - start, val);
    }

    public static void runForLoop(int n){
        var fib = Parser.parse(
                "fn fib(n){let (prev, curr) = (0,1); for i in 1..n { let next = prev + curr; prev = curr; curr = next;}; curr}" +
                        "fib"
        );
        long start = System.currentTimeMillis();
        var val = fib.call(null, new Object[]{n});
        long end = System.currentTimeMillis();
        System.out.printf("Time siiam: %d with value %d\n",end - start, val);
    }

    public static void runTuple(int n){
        var fib = Parser.parse(
                "fn fib(n){let (prev, curr) = (0,1); for i in 1..n { (prev, curr) = (curr, prev + curr)}; curr}" +
                        "fib"
        );
        long start = System.currentTimeMillis();
        var val = fib.call(null, new Object[]{n});
        long end = System.currentTimeMillis();
        System.out.printf("Time siiam: %d with value %d\n",end - start, val);
    }

    private static int fib(int n){
        var i = 1;
        var prev = 0;
        var curr = 1;
        while(i < n){
            var next = prev + curr;
            prev = curr;
            curr = next;
            i++;
        }

        return curr;
    }

    public static void runNative(int n){
        long start = System.currentTimeMillis();
        var val = fib(n);
        long end = System.currentTimeMillis();
        System.out.printf("Time native: %d with value %d\n",end - start, val);
    }
}
