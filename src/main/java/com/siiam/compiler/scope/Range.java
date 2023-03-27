package com.siiam.compiler.scope;

import lombok.AllArgsConstructor;

import java.util.Iterator;

@AllArgsConstructor
public class Range implements Iterable<Integer>{
    private int startIndex;
    private int endIndex;

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            int current = startIndex;

            @Override
            public boolean hasNext() {
                return current < endIndex;
            }

            @Override
            public Integer next() {
                return ++current;
            }
        };
    }
}
