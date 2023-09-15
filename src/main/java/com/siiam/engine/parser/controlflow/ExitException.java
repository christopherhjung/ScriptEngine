package com.siiam.engine.parser.controlflow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExitException extends ControlFlowException {
    private int value;
}
