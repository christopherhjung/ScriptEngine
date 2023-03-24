package com.siiam.compiler.parser.controlflow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReturnException extends RuntimeException {
    private Object returnValue;
}
