package com.siiam.compiler.parser.controlflow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ContinueException extends RuntimeException {
    private String label;
}
