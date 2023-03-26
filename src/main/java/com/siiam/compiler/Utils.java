package com.siiam.compiler;

import com.siiam.compiler.exception.InterpreterException;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Iterator;

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
}
