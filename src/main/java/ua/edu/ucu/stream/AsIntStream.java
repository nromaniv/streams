package ua.edu.ucu.stream;

import ua.edu.ucu.function.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class AsIntStream implements IntStream {

    private Iterator<Integer> iterator;
    private AsIntStream previous;

    // Constructor for the underlying stream
    private AsIntStream(int[] values) {
        iterator = new Iterator<Integer>() {
            private int position = 0;

            @Override
            public boolean hasNext() {
                return position < values.length;
            }

            @Override
            public Integer next() {
                return values[position++];
            }
        };
    }

    // Constructor for intermediary streams
    private AsIntStream(AsIntStream previous, Iterator<Integer> iterator) {
        this.iterator = iterator;
        this.previous = previous;
    }

    public static IntStream of(int... values) {
        return new AsIntStream(values);
    }

    private IntStream newIntermediateOperation(Supplier<Integer> supplier) {
        Iterator<Integer> iterator = new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return previous.iterator.hasNext();
            }

            @Override
            public Integer next() {
                return supplier.get();
            }
        };
        return new AsIntStream(this, iterator);
    }

    @Override
    public Double average() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer max() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer min() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer sum() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IntStream filter(IntPredicate predicate) {
        return newIntermediateOperation(
                () -> {
                    int value;
                    while (true)
                        if (previous.iterator.hasNext()) {
                            if (predicate.test(value = previous.iterator.next()))
                                return value;
                        } else {
                            throw new NoSuchElementException();
                        }
                });
    }

    @Override
    public void forEach(IntConsumer action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
        return newIntermediateOperation(
                () -> mapper.apply(previous.iterator.next()));
    }

    @Override
    public IntStream flatMap(IntToIntStreamFunction func) {
        return newIntermediateOperation(
                () -> null);
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
