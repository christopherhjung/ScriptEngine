package com.siiam.compiler.lexer;

import org.apache.commons.lang3.CharUtils;


public class Lexer {
    private final char[] chars;
    private int idx;
    private int mark = 0;
    private Token.Enter enter;

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

    public int ahead(int offset){
        if(idx + offset + 1 >= chars.length){
            return -1;
        }
        return chars[idx];
    }

    public boolean is(int cha, int offset){
        return ahead(offset) == cha;
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

    private Token token(Token.Kind kind){
        return token(kind, null);
    }
    
    private Token token(Token.Kind kind, String symbol){
        return new Token(kind, enter, symbol);
    }
    
    public Token next(){
        while(!isEOL()){
            enter = Token.Enter.Token;
            while(true){
                if(accept(' ') || accept('\t')){
                    if(enter == Token.Enter.Token){
                        enter = Token.Enter.Space;
                    }
                }else if(accept('\n') || accept('\r')){
                    enter = Token.Enter.NL;
                }else{
                    break;
                }
            }

            if(accept('=')){
                if(accept('=')){
                    return token(Token.Kind.Eq);
                }else{
                    return token(Token.Kind.Assign);
                }
            }

            if(accept('!')){
                if(accept('=')){
                    return token(Token.Kind.Ne);
                }else{
                    return token(Token.Kind.Not);
                }
            }

            if(accept('<')){
                if(accept('=')){
                    return token(Token.Kind.Le);
                }else{
                    return token(Token.Kind.Lt);
                }
            }

            if(accept('>')){
                if(accept('=')){
                    return token(Token.Kind.Ge);
                }else{
                    return token(Token.Kind.Gt);
                }
            }

            if(accept('?')){
                if(accept('?')){
                    return token(Token.Kind.Nullish);
                }else if(accept('.')){
                    return token(Token.Kind.Chain);
                }else{
                    return token(Token.Kind.Quest);
                }
            }

            if(accept('&')){
                return token(Token.Kind.BitAnd);
            }

            if(accept('|')){
                return token(Token.Kind.BitOr);
            }

            if(accept('^')){
                return token(Token.Kind.BitXor);
            }

            if(accept('(')){
                return token(Token.Kind.LeftParen);
            }

            if(accept(')')){
                return token(Token.Kind.RightParen);
            }

            if(accept('{')){
                return token(Token.Kind.LeftBrace);
            }

            if(accept('}')){
                return token(Token.Kind.RightBrace);
            }

            if(accept('+')){
                if(accept('+')){
                    return token(Token.Kind.Inc);
                }else if(accept('=')){
                    return token(Token.Kind.AssignPlus);
                }
                return token(Token.Kind.Plus);
            }

            if(accept('-')){
                if(accept('-')){
                    return token(Token.Kind.Dec);
                }else if(accept('>')){
                    return token(Token.Kind.Arrow);
                }else if(accept('=')){
                    return token(Token.Kind.AssignMinus);
                }
                return token(Token.Kind.Minus);
            }

            if(accept('*')){
                if(accept('*')){
                    return token(Token.Kind.Pow);
                }else if(accept('=')){
                    return token(Token.Kind.AssignStar);
                }

                return token(Token.Kind.Star);
            }


            if( accept('/') ){
                if(accept('=')){
                    return token(Token.Kind.AssignSlash);
                }

                if(accept('*') ){ // arbitrary comment
                    var depth = 1;
                    while(true) {
                        if(isEOL()){
                            return token(Token.Kind.Error);
                        }

                        if(accept('/')){
                            if(accept('*')){
                                depth += 1;
                            }
                        } else if(accept('*')){
                            if(accept('/')){
                                depth -= 1;
                                if(depth == 0 ){
                                    break;
                                }
                            }

                            continue;
                        }

                        next();
                    }
                    continue;
                }
                if(accept('/')) {
                    while(true) {
                        if(isEOL()){
                            return token(Token.Kind.Error);
                        }

                        if(accept('\n')){
                            break;
                        } else {
                            next();
                        }
                    }
                    continue;
                }
                return token(Token.Kind.Slash);
            }

            if(accept('.')){
                if(!accept('.')){
                    return token(Token.Kind.Dot);
                }

                if(!accept('.')){
                    return token(Token.Kind.Range);
                }

                return token(Token.Kind.Ellipsis);
            }

            if(accept(',')){
                return token(Token.Kind.Comma);
            }

            if(accept(';')){
                return token(Token.Kind.Semi);
            }

            if(isNumeric()){
                mark();
                shift();

                while(isNumeric()){
                    shift();
                }

                return token(Token.Kind.Number, getString());
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
                    case "false": return token(Token.Kind.Boolean, value);
                    case "fn": return token(Token.Kind.Fn);
                    case "if": return token(Token.Kind.If);
                    case "else": return token(Token.Kind.Else);
                    case "break": return token(Token.Kind.Break);
                    case "continue": return token(Token.Kind.Continue);
                    case "while": return token(Token.Kind.While);
                    case "return": return token(Token.Kind.Return);
                    case "null": return token(Token.Kind.Null);
                    case "and": return token(Token.Kind.And);
                    case "or": return token(Token.Kind.Or);
                    case "let": return token(Token.Kind.Let);
                    case "for": return token(Token.Kind.For);
                    case "in": return token(Token.Kind.In);
                }

                return token(Token.Kind.Ident, value);
            }

            if(accept('\"')){
                mark();
                while(!accept('\"')){
                    if(isEOL()){
                        return token(Token.Kind.Error);
                    }
                    shift();
                }

                return token(Token.Kind.String, getString(idx - mark - 1 ));
            }

            if(isEOL()){
                return token(Token.Kind.EOL);
            }

            return token(Token.Kind.Error);
        }

        return token(Token.Kind.EOL);
    }
}













