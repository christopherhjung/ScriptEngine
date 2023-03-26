package com.siiam.compiler;

import com.siiam.compiler.parser.Parser;
import com.siiam.compiler.scope.MutualScope;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

public class ParserTest {
    public String load(String fileName){
        try{
            var filePath = ParserTest.class.getClassLoader().getResource(fileName).getPath();
            var bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes, StandardCharsets.UTF_8);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void run(){
        var parser = Parser.parse(load("file.siiam"));
        var arr  = parser.eval(new MutualScope());
        System.out.println(arr);
        //System.out.println(Arrays.deepToString(arr));
        //assertArrayEquals(new Object[]{1,1,2,3,3},  arr);
    }
}
