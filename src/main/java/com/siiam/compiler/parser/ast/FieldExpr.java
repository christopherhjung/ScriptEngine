package com.siiam.compiler.parser.ast;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@RequiredArgsConstructor
public class FieldExpr implements Expr{
    private final Expr objExpr;
    private final String name;
    private boolean optional = false;

    @Override
    public Object eval(Scope scope) {
        var value = objExpr.eval(scope);

        if(value == null){
            if(optional) return null;
            throw new InterpreterException("Null pointer exception");
        }

        if( value instanceof Map ){
            var map = (Map<String, Object>) value;
            return map.get(name);
        }

        var valClass = value.getClass();
        try {
            var field = valClass.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new InterpreterException("Expected map or valid object in field expr!", e);
        }
    }

    @Override
    public Object assign(Scope scope, Object value, boolean define) {
        var obj = this.objExpr.eval(scope);

        if(obj == null){
            if(optional) return null;
            throw new InterpreterException("Null pointer exception");
        }

        if( obj instanceof Map ){
            var map = (Map<String, Object>) obj;
            return map.put(name, value);
        }

        var valClass = obj.getClass();
        try {
            var field = valClass.getDeclaredField(name);
            field.setAccessible(true);
            field.set(value, obj);
            return obj;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new InterpreterException("Expected map or valid object in field expr!", e);
        }
    }
}
