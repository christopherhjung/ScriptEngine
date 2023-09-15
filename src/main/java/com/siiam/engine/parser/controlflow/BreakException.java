package com.siiam.engine.parser.controlflow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BreakException extends ControlFlowException {
    private String label;
}
