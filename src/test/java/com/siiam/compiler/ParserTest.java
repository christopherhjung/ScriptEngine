package com.siiam.compiler;

import com.siiam.compiler.parser.Parser;
import com.siiam.compiler.scope.MutualScope;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(55, parser.eval(new MutualScope(new HashMap<>())));
    }
}
