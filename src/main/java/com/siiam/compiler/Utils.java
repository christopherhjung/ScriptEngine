package com.siiam.compiler;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.parser.ast.Expr;
import com.siiam.compiler.parser.controlflow.BreakException;
import com.siiam.compiler.parser.controlflow.ContinueException;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

@UtilityClass
public class Utils {
    public static Iterator<Object> getIterator(Object obj){
        if(obj instanceof Object[]){
            return Arrays.stream((Object[])obj).iterator();
        }else if(obj instanceof Iterable<?>){
            return (Iterator<Object>) obj;
        }

        throw new InterpreterException("Value not iterable");
    }

    public Object flatten(Object[] arr){
        if(arr.length == 1){
            return arr[0];
        }else{
            return arr;
        }
    }
}
