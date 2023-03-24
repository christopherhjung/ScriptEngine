package com.siiam.compiler;

import com.siiam.compiler.lexer.Lexer;
import com.siiam.compiler.lexer.Token;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LexerTest {
    @Test
    public void simpleLexTest(){
        var lexer = new Lexer("Result == \"2\"");

        var first = lexer.next();
        assertEquals(Token.Kind.Ident, first.getKind());
        assertEquals("Result", first.getSymbol());

        var second = lexer.next();
        assertEquals(Token.Kind.Eq, second.getKind());
        assertNull(second.getSymbol());

        var third = lexer.next();
        assertEquals(Token.Kind.String, third.getKind());
        assertEquals("2", third.getSymbol());
    }


}
