package com.siiam.engine.parser.controlflow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReturnException extends ControlFlowException {
    private Object returnValue;
}
