package com.siiam.compiler.parser;

import com.siiam.compiler.exception.ParseException;
import com.siiam.compiler.lexer.Lexer;
import com.siiam.compiler.lexer.Token;
import com.siiam.compiler.parser.ast.*;
import com.siiam.compiler.scope.MutualScope;
import com.siiam.compiler.scope.StaticScope;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private static final int LOOKAHEAD_SIZE = 2;

    public static Expr parse(String expr){
        var lex = new Lexer(expr);
        var parser = new Parser(lex);
        var ast = parser.parse();
        var bindScope = new MutualScope();
        ast = ast.bind(bindScope, false);
        return ast;
    }

    private Op lastOp;
    private final Lexer lexer;
    private final Token[] lookahead = new Token[LOOKAHEAD_SIZE];
    private int currIdx = 0;
    private String label;

    private Parser(Lexer lexer){
        this.lexer = lexer;
        for( var i = 0 ; i < LOOKAHEAD_SIZE ; i++ ){
            lookahead[i] = lexer.next();
        }
    }

    private Token peek(){
        return lookahead[currIdx];
    }

    private Token ahead(int idx){
        if(idx > LOOKAHEAD_SIZE){
            throw new ParseException("Out of Lookahead buffer");
        }
        return lookahead[(currIdx + idx) % LOOKAHEAD_SIZE];
    }

    private void shift(){
        lookahead[currIdx] = lexer.next();
        currIdx = (currIdx + 1) % LOOKAHEAD_SIZE;
    }

    private Token next(){
        var prev = lookahead[currIdx];
        shift();
        return prev;
    }

    private boolean is(Token.Kind kind){
        return peek().getKind() == kind;
    }

    private boolean accept(Token.Kind kind){
        if(is(kind)){
            shift();
            return true;
        }

        return false;
    }

    private Token expect(Token.Kind kind){
        var curr = peek();
        if(!accept(kind)){
            throw new ParseException("Expected " + kind);
        }
        return curr;
    }

    private boolean enter(Token.Enter enter){
        return peek().getEnter() == enter;
    }

    private void expectEnter(Token.Enter enter){
        if(!enter(enter)){
            throw new ParseException("Expected enter " + enter);
        }
    }

    private boolean follow(Token.Kind kind){
        if(enter(Token.Enter.Token)){
            return accept(kind);
        }

        return false;
    }

    private boolean isEOL(){
        return peek().getKind() == Token.Kind.EOL;
    }

    public Expr parse(){
        var result = parseItem();
        expect(Token.Kind.EOL);
        return result;
    }

    private String parseIdent(boolean expect){
        if(is(Token.Kind.Ident)){
            return next().getSymbol();
        }else if(expect){
            throw new ParseException("Expected identifier");
        }

        return null;
    }

    private IdentExpr parseIdentExpr(){
        return new IdentExpr(parseIdent(true));
    }

    private Expr parsePtrn(){
        if(accept(Token.Kind.LeftParen)){
            return parseTuple();
        }else{
            return parseIdentExpr();
        }
    }

    private Expr parseLetExpr(){
        expect(Token.Kind.Let);
        var ptrn = parsePtrn();
        Expr init = null;
        if(accept(Token.Kind.Assign)){
            init = parseExpr();
        }
        return new LetExpr(ptrn, init);
    }

    private Expr parseItem(){
        var functions = new ArrayList<FunctionExpr>();
        var exprs = new ArrayList<Expr>();
        while(!is(Token.Kind.EOL)){
            switch (peek().getKind()){
                case Semi: next(); continue;
                case Fn: {
                    functions.add(parseFunction());
                    break;
                }
                case Let: {
                    exprs.add(parseLetExpr());
                    break;
                }
                default:
                    exprs.add(parseExpr());
            }
        }

        var expr = exprs.size() == 1 ?
                exprs.get(0) :
                new BlockExpr(exprs.toArray(new Expr[0]));

        if(functions.isEmpty()){
            return expr;
        }else{
            var scopeBuilder = StaticScope.builder();
            for( var function : functions ){
                scopeBuilder.add(function.getName(), function);
            }
            return new ScopedExpr(scopeBuilder.build(), expr);
        }
    }

    private Expr parseStmt(){
        while(!is(Token.Kind.EOL)){
            switch (peek().getKind()){
                case Let: return parseLetExpr();
                default: return parseExpr();
            }
        }

        throw new ParseException("Expected let or expr");
    }

    private FunctionExpr parseFunction(){
        expect(Token.Kind.Fn);
        var name = parseIdent(true);
        expect(Token.Kind.LeftParen);
        var params = new ArrayList<Expr>();
        while(!accept(Token.Kind.RightParen)){
            if(!params.isEmpty()){
                expect(Token.Kind.Comma);
            }
            params.add(parseIdentExpr());
        }

        var body = parseBlock();
        var paramArr = params.toArray(new Expr[0]);
        return new FunctionExpr(name, new TupleExpr(paramArr), body);
    }

    private Expr parsePrefixExpr(Op op){
        if( op == Op.LeftParen ){
            var tuple = parseTuple();
            if(!accept(Token.Kind.Arrow)){
                return tuple;
            }

            var body = parseExpr();
            return new LambdaExpr(TupleExpr.asTuple(tuple), body);
        }else{
            var expr = parseExpr(op.prec().next());
            return new PrefixExpr(expr, op);
        }
    }

    private Expr parseInfixExpr(Expr lhs, Op op){
        if(op == Op.Chain){
            if(accept(Token.Kind.LeftParen)){
                var arg = TupleExpr.asTuple(parseTuple());
                return new CallExpr(lhs, arg, true);
            }else{
                return new FieldExpr(lhs, parseIdent(true), true);
            }
        }else if(op == Op.Dot){
            return new FieldExpr(lhs, parseIdent(true));
        }

        var rhs = parseExpr(op.prec().next());
        return new InfixExpr(lhs, rhs, op);
    }

    private Expr parseTuple(){
        if(!accept(Token.Kind.RightParen)){
            var expr = parseExpr();

            if(accept(Token.Kind.Comma)){
                if(accept(Token.Kind.RightParen)){
                    return new TupleExpr(new Expr[]{expr});
                }else{
                    var exprs = new ArrayList<Expr>();
                    exprs.add(expr);
                    do{
                        exprs.add(parseExpr());
                    }while(accept(Token.Kind.Comma));
                    expect(Token.Kind.RightParen);
                    return new TupleExpr(exprs.toArray(new Expr[0]));
                }
            }

            expect(Token.Kind.RightParen);
            return expr;
        }

        return new TupleExpr(new Expr[0]);
    }

    private Expr parseIf(){
        expect(Token.Kind.If);
        var condition = parseExpr();
        var trueBranch = parseBlock();
        var falseBranch = (Expr)null;
        if(accept(Token.Kind.Else)){
            falseBranch = parseBlock();
        }

        return new IfExpr(condition, trueBranch, falseBranch);
    }

    private Expr parseWhile(String label){
        expect(Token.Kind.While);
        var condition = parseExpr();
        var body = parseBlock();
        return new WhileExpr(condition, body, label);
    }

    private Expr parseFor(String label){
        expect(Token.Kind.For);
        var variable = parseExpr();
        expect(Token.Kind.In);
        var range = parseExpr();
        var body = parseBlock();
        return new ForExpr(variable, range, body, label);
    }

    private Expr parseBlock(){
        expect(Token.Kind.LeftBrace);
        var exprs = new ArrayList<Expr>();
        var valid = true;
        while(true){
            while(accept(Token.Kind.Semi)){
                valid = true;
            }
            if(accept(Token.Kind.RightBrace)) break;
            if(!enter(Token.Enter.NL) && !valid){
                throw new ParseException("Expected newline or semi to separate statements");
            }
            exprs.add(parseStmt());
            valid = false;
        }

        return new BlockExpr(exprs.toArray(new Expr[0]));
    }

    private Expr parsePostfixExpr(Expr lhs, Op op){
        switch (op){
            case LeftParen:
                var arg = parseTuple();
                return new CallExpr(lhs, TupleExpr.asTuple(arg));
            case Inc: return new InfixExpr(lhs, new InfixExpr(lhs, new LiteralExpr(1), Op.Add), Op.Assign);
            case Dec: return new InfixExpr(lhs, new InfixExpr(lhs, new LiteralExpr(1), Op.Sub), Op.Assign);
        }

        throw new ParseException("Postfix Expr not yet implemented");
    }

    private Expr parsePrimaryExpr(){
        var label = this.label;
        this.label = null;
        switch (peek().getKind()){
            case Ident: {
                var sym = next().getSymbol();
                if(accept(Token.Kind.Colon)){
                    this.label = sym;
                    return parseExpr();
                }
                return new IdentExpr(sym);
            }
            case String: return new LiteralExpr(next().getSymbol());
            case Boolean:  return new LiteralExpr(Boolean.parseBoolean(next().getSymbol()));
            case Number: return new LiteralExpr(Integer.parseInt(next().getSymbol()));
            case Null: {
                next();
                return new LiteralExpr(null);
            }
            case Return: {
                next();
                Expr expr = null;
                if(!is(Token.Kind.Semi)  && !is(Token.Kind.RightBrace)){
                    expr = parseExpr();
                }
                return new ReturnExpr(expr);
            }
            case Break: {
                next();
                return new BreakExpr(parseIdent(false));
            }
            case Continue: {
                next();
                return new ContinueExpr(parseIdent(false));
            }
            case If: return parseIf();
            case While: return parseWhile(label);
            case For: return parseFor(label);
            case LeftBrace: return parseBlock();
        }

        throw new ParseException("Expected Identifier, String, Boolean or Number");
    }

    private Expr parseExpr(){
        return parseExpr(Prec.Bottom);
    }

    private Expr parseExpr(Prec prec){
        var prefixOp = parseOp();
        var lhs = prefixOp != null ? parsePrefixExpr(prefixOp) : parsePrimaryExpr();

        while(!isEOL()){
            if(enter(Token.Enter.NL) ){
                break;
            }

            var op = parseOp();
            if(op == null){
                break;
            }else if(op.isInfix()){
                if(prec.largerThan(op.prec())){
                    lastOp = op;
                    break;
                }

                lhs = parseInfixExpr(lhs, op);
            }else if(op.isPostfix()){
                if(prec == Prec.Top){
                    lastOp = op;
                    break;
                }

                lhs = parsePostfixExpr(lhs, op);
            }else{
                throw new ParseException("Expr exception");
            }
        }

        return lhs;
    }

    private Op parseOp(){
        if(lastOp != null){
            var tmp = lastOp;
            lastOp = null;
            return tmp;
        }
        var op = parseOpImpl();
        if(op != null){
            next();
        }
        return op;
    }

    private Op parseOpImpl(){
        switch (peek().getKind()){
            case Eq: return Op.Eq;
            case Ne: return Op.Ne;
            case Le: return Op.Le;
            case Lt: return Op.Lt;
            case Ge: return Op.Ge;
            case Gt: return Op.Gt;
            case Plus: return Op.Add;
            case Minus: return Op.Sub;
            case Star: return Op.Mul;
            case Slash: return Op.Div;
            case Dot: return Op.Dot;
            case And: return Op.And;
            case Or: return Op.Or;
            case LeftParen: return Op.LeftParen;
            case Assign: return Op.Assign;
            case AssignMinus: return Op.AssignSub;
            case AssignPlus: return Op.AssignAdd;
            case AssignStar: return Op.AssignMul;
            case AssignSlash: return Op.AssignDiv;
            case Nullish: return Op.Nullish;
            case Chain: return Op.Chain;
            case Pow: return Op.Pow;
            case Not: return Op.Not;
            case BitAnd: return Op.BitAnd;
            case BitOr: return Op.BitOr;
            case BitXor: return Op.BitXor;
            case Dec: return Op.Dec;
            case Inc: return Op.Inc;
            case Range: return Op.Range;
            case Ellipsis: return Op.Spread;
            default: return null;
        }
    }
}
