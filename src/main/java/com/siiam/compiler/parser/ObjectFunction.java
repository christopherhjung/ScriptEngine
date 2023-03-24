package com.siiam.compiler.parser;

@FunctionalInterface
public interface ObjectFunction {
    Object call(Object[] args);
}