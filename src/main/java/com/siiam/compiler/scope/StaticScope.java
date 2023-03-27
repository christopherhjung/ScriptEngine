package com.siiam.compiler.scope;

import com.siiam.compiler.exception.InterpreterException;
import com.siiam.compiler.parser.ObjectFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class StaticScope implements Scope {
    private final Map<String, Slot> map;

    @Override
    public Slot getValue(String key) {
        return map.get(key);
    }

    @Override
    public Collection<Slot> values(){
        return map.values();
    }

    public static Builder builder(){
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder{
        Map<String, Slot> map = new HashMap<>();

        public Builder add(String key, Object value){
            assert !map.containsKey(key);
            map.put(key, new Slot(value));
            return this;
        }

        public Builder addMethod(String key, ObjectFunction function){
            add(key, function);
            return this;
        }

        private static ObjectFunction toFunc(Object obj, Method method ){
            return (arg) -> {
                try {
                    return method.invoke(obj, arg);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InterpreterException("Method reflect invocation failed!", e);
                }
            };
        }

        private static boolean isAssignableTo(Method method, Object[] arg){
            var types = method.getParameterTypes();
            if(types.length != arg.length){
                return false;
            }

            for( var idx = 0 ; idx < arg.length ; idx++ ){
                if(!types[idx].isAssignableFrom(arg[idx].getClass())){
                    return false;
                }
            }

            return true;
        }

        private static ObjectFunction toFunc(Object obj, Method[] methods){
            if(methods.length == 1){
                return toFunc(obj, methods[0]);
            }

            return (arg) -> {
                try {
                    for(var method : methods){
                        if(isAssignableTo(method, arg)){
                            return method.invoke(obj, arg);
                        }
                    }

                    throw new InterpreterException("No matching function was found");
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InterpreterException("Method reflect invocation failed!", e);
                }
            };
        }

        public Builder addMethod(String key, Object obj, String methodName){
            var objClass = obj.getClass();
            var methods = Arrays.stream(objClass.getDeclaredMethods())
                    .filter(it -> it.getName().equals(methodName))
                    .peek(method -> method.setAccessible(true))
                    .toArray(Method[]::new);

            return addMethodsByName(obj, key, methods);
        }

        public Builder addMethods(Object obj){
            var nonStatic = Arrays.stream(obj.getClass().getDeclaredMethods())
                    .filter(it -> (it.getModifiers() & Modifier.STATIC) == 0)
                    .toArray(Method[]::new);

            return addMethodsByName(obj, obj.getClass().getDeclaredMethods());
        }

        public Builder addStaticMethods(Class<?> clazz){
            return addStaticMethods(clazz.getSimpleName(), clazz);
        }

        public Builder addStaticMethods(String className, Class<?> clazz){
            var staticMethods = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(it -> (it.getModifiers() & Modifier.STATIC) != 0)
                    .toArray(Method[]::new);

            return addMethodsByClass(null, className, staticMethods);
        }

        private static Map<String, Object> collectMethods(Object obj, Method[] methods){
            var map = Arrays.stream(methods)
                    .collect(Collectors.groupingBy(Method::getName));

            var resultMap = new HashMap<String, Object>();

            for( var entry : map.entrySet() ){
                var name = entry.getKey();
                var overloadMethods = entry.getValue();
                var func = toFunc(obj, overloadMethods.toArray(new Method[0]));
                resultMap.put(name, func);
            }

            return resultMap;
        }

        public Builder addMethodsByClass(Object obj, String className, Method[] methods){
            var resultMap = collectMethods(obj, methods);
            if(className != null){
                add(className, resultMap);
            }else{
                resultMap.forEach(this::add);
            }
            return this;
        }

        public Builder addMethodsByName(Object obj, String methodName, Method[] methods){
            var func = toFunc(obj, methods);
            add(methodName, func);
            return this;
        }

        public Builder addMethodsByName(Object obj, Method[] methods){
            var resultMap = collectMethods(obj, methods);
            resultMap.forEach(this::add);
            return this;
        }

        public StaticScope build(){
            return new StaticScope(new HashMap<>(map));
        }
    }
}
