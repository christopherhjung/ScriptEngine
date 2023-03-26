package com.siiam.compiler.scope;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Value {
    //private Value old;
    private Object content;

    /*
    public void push(Object obj){
        old = new Value(old, content);
        content = obj;
    }*/
}
