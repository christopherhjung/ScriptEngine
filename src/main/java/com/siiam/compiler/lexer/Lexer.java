package com.siiam.compiler.lexer;

import org.apache.commons.lang3.CharUtils;


public class Lexer {
    private final char[] chars;
    private int idx;
    private int mark = 0;

    public Lexer(String code){
        this.chars = code.toCharArray();
    }

    private int curr(){
        return isEOL() ? -1 : chars[idx];
    }

    private char currChar(){
        assert !isEOL();
        return chars[idx];
    }

    private char eat(){
        assert !isEOL();
        var result =  chars[idx];
        shift();
        return result;
    }

    private void shift(){
        idx++;
    }

    private boolean isEOL(){
        return idx >= chars.length;
    }

    public boolean accept(int cha){
        if(curr() == cha){
            idx++;
            return true;
        }

        return false;
    }

    private boolean isNumeric(){
        var curr = this.curr();
        if(curr == -1){
            return false;
        }else{
            return CharUtils.isAsciiNumeric((char) curr);
        }
    }

    private boolean isAlpha(){
        var curr = this.curr();
        if(curr == -1){
            return false;
        }else{
            return CharUtils.isAsciiAlpha((char) curr);
        }
    }

    private boolean isAlphaNumeric(){
        var curr = this.curr();
        if(curr == -1){
            return false;
        }else{
            return CharUtils.isAsciiAlphanumeric((char) curr);
        }
    }

    private void mark(){
        mark = idx;
    }

    private String getString(){
        return getString(idx - mark);
    }

    private String getString(int length){
        return new String(chars, mark, length);
    }

    public Token next(){
        while(!isEOL()){
            if(accept(' ') || accept('\n') || accept('\r') || accept('\n')){
                continue;
            }
            if(accept('=')){
                if(accept('=')){
                    return new Token(Token.Kind.Eq, null);
                }else{
                    return new Token(Token.Kind.Assign, null);
                }
            }

            if(accept('!')){
                if(accept('=')){
                    return new Token(Token.Kind.Ne, null);
                }else{
                    return new Token(Token.Kind.Not, null);
                }
            }

            if(accept('<')){
                if(accept('=')){
                    return new Token(Token.Kind.Le, null);
                }else{
                    return new Token(Token.Kind.Lt, null);
                }
            }

            if(accept('>')){
                if(accept('=')){
                    return new Token(Token.Kind.Ge, null);
                }else{
                    return new Token(Token.Kind.Gt, null);
                }
            }

            if(accept('?')){
                if(accept('?')){
                    return new Token(Token.Kind.Nullish, null);
                }else if(accept('.')){
                    return new Token(Token.Kind.Chain, null);
                }else{
                    return new Token(Token.Kind.Quest, null);
                }
            }

            if(accept('&')){
                return new Token(Token.Kind.BitAnd, null);
            }

            if(accept('|')){
                return new Token(Token.Kind.BitOr, null);
            }

            if(accept('^')){
                return new Token(Token.Kind.BitXor, null);
            }

            if(accept('(')){
                return new Token(Token.Kind.LeftParen, null);
            }

            if(accept(')')){
                return new Token(Token.Kind.RightParen, null);
            }

            if(accept('{')){
                return new Token(Token.Kind.LeftBrace, null);
            }

            if(accept('}')){
                return new Token(Token.Kind.RightBrace, null);
            }

            if(accept('+')){
                if(accept('+')){
                    return new Token(Token.Kind.Inc, null);
                }
                return new Token(Token.Kind.Plus, null);
            }

            if(accept('-')){
                if(accept('-')){
                    return new Token(Token.Kind.Dec, null);
                }
                return new Token(Token.Kind.Minus, null);
            }

            if(accept('*')){
                if(accept('*')){
                    return new Token(Token.Kind.Pow, null);
                }

                return new Token(Token.Kind.Star, null);
            }

            if(accept('/')){
                return new Token(Token.Kind.Slash, null);
            }

            if(accept('.')){
                return new Token(Token.Kind.Dot, null);
            }

            if(accept(',')){
                return new Token(Token.Kind.Comma, null);
            }

            if(accept(';')){
                return new Token(Token.Kind.Semi, null);
            }

            if(isNumeric()){
                mark();
                shift();

                while(isNumeric()){
                    shift();
                }

                return new Token(Token.Kind.Number, getString());
            }

            if(isAlpha()){
                mark();
                shift();

                while(isAlphaNumeric()){
                    shift();
                }

                var value = getString();
                switch (value) {
                    case "true":
                    case "false": return new Token(Token.Kind.Boolean, value);
                    case "fn": return new Token(Token.Kind.Fn, null);
                    case "if": return new Token(Token.Kind.If, null);
                    case "else": return new Token(Token.Kind.Else, null);
                    case "break": return new Token(Token.Kind.Break, null);
                    case "continue": return new Token(Token.Kind.Continue, null);
                    case "while": return new Token(Token.Kind.While, null);
                    case "return": return new Token(Token.Kind.Return, null);
                    case "null": return new Token(Token.Kind.Null, null);
                    case "and": return new Token(Token.Kind.And, null);
                    case "or": return new Token(Token.Kind.Or, null);
                }

                return new Token(Token.Kind.Ident, value);
            }

            if(accept('\"')){
                mark();
                while(!accept('\"')){
                    if(isEOL()){
                        return new Token(Token.Kind.Error, null);
                    }
                    shift();
                }

                return new Token(Token.Kind.String, getString(idx - mark - 1 ));
            }

            return new Token(Token.Kind.Error, null);
        }

        return new Token(Token.Kind.EOL, null);
    }
}













