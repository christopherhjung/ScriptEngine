package com.siiam.engine.parser;

@FunctionalInterface
public interface ObjectFunction {
    Object call(Object[] args);
}