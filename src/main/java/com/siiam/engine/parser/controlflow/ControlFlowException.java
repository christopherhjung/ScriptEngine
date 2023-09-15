package com.siiam.engine.parser.controlflow;

public class ControlFlowException extends RuntimeException{
    public ControlFlowException(){
        super(null, null, true, false);
    }
}
