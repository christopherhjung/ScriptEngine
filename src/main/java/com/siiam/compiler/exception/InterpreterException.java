package com.siiam.compiler.exception;

public class InterpreterException extends RuntimeException{
    public InterpreterException(String msg){
        super(msg);
    }

    public InterpreterException(String message, Throwable cause) {
        super(message, cause);
    }
}
