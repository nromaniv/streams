package ua.edu.ucu.stream;

import ua.edu.ucu.function.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class AsIntStream implements IntStream {

    private Iterator<Integer> iterator;
    private AsIntStream previous;
    private int size;

    // Constructor for the underlying stream
    private AsIntStream(int[] values) {
        iterator = new Iterator<Integer>() {
            private int position = -1;

            @Override
            public boolean hasNext() {
                return position < values.length - 1;
            }

            @Override
            public Integer next() {
                return values[++position];
            }
        };
        size = values.length;
    }

    // Constructor for intermediary streams
    private AsIntStream(AsIntStream previous, Supplier<Integer> supplier) {
        this.previous = previous;
        this.iterator = new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return previous.iterator.hasNext();
            }

            @Override
            public Integer next() {
                return supplier.get();
            }
        };
    }

    private AsIntStream(AsIntStream previous, Iterator<Integer> iterator) {
        this.iterator = iterator;
        this.previous = previous;
    }

    public static IntStream of(int... values) {
        return new AsIntStream(values);
    }

    @Override
    public Double average() {
        int i = 0;
        int sum = 0;
        while (iterator.hasNext()) {
            i++;
            sum += iterator.next();
        }
        return (double) sum / i;
    }

    @Override
    public Integer max() {
        return reduce(Integer.MIN_VALUE, (a, b) -> a > b ? a : b);
    }

    @Override
    public Integer min() {
        return reduce(Integer.MAX_VALUE, (a, b) -> a < b ? a : b);
    }

    @Override
    public long count() {
        int i = 0;
        while (iterator.hasNext())
            i++;
        return i;
    }

    @Override
    public Integer sum() {
        return reduce(0, (a, b) -> a + b);
    }

    @Override
    public IntStream filter(IntPredicate predicate) {
        return new AsIntStream(this,
                () -> {
                    int value;
                    while (true)
                        if (iterator.hasNext()) {
                            if (predicate.test(value = iterator.next()))
                                return value;
                        } else {
                            throw new NoSuchElementException();
                        }
                });
    }

    @Override
    public void forEach(IntConsumer action) {
        while (iterator.hasNext())
            action.accept(iterator.next());
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
        return new AsIntStream(this,
                () -> mapper.apply(iterator.next()));
    }

    @Override
    public IntStream flatMap(IntToIntStreamFunction func) {
        return new AsIntStream(this, new Iterator<Integer>() {
            AsIntStream stream = getStream();

            @Override
            public boolean hasNext() {
                return (iterator.hasNext() || stream.iterator.hasNext());
            }

            @Override
            public Integer next() {
                if (stream.iterator.hasNext()) {
                    return stream.iterator.next();
                } else {
                    stream = getStream();
                }
                if (stream == null) {
                    throw new NoSuchElementException();
                } else {
                    return next();
                }
            }

            private AsIntStream getStream() {
                return iterator.hasNext() ?
                        (AsIntStream) func.applyAsIntStream(iterator.next()) :
                        null;
            }
        });
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
        while (iterator.hasNext()) {
            identity = op.apply(identity, iterator.next());
        }
        return identity;
    }

    @Override
    public int[] toArray() {
        int[] result = new int[size];
        for (int i = 0; iterator.hasNext(); i++)
            result[i] = iterator.next();
        return result;
    }
}
