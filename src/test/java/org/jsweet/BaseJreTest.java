package org.jsweet;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class BaseJreTest {
    //@Test
    public void allTests() {
        Map<Method, Exception> failures = new HashMap<>();
        List<Method> good = new ArrayList<>();
        pickTestMethod(j4ts.test.Test.class)
                .forEach(m -> {
                    try {
                        m.invoke(null);
                        good.add(m);
                    } catch (Exception e) {
                        failures.put(m, e);
                    }
                });
        failures.forEach((m, e) -> {
            System.err.println("Failed method " + m.getName() + " with " + e.getCause());
            e.getCause().printStackTrace();
        });
        System.err.println("Failed " + failures.size() + " of " + good.size());
        j4ts.test.Test.assertTrue(failures.isEmpty());
    }

    private List<Method> pickTestMethod(Class c) {
        return asList(c.getDeclaredMethods()).stream()
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .filter(m -> m.getParameterCount() == 0)
                .collect(toList());
    }

    protected abstract <T> Stream<T> stream(Collection<T> c);

    @Test
    public void testStreamCollectList() {
        assertEquals(asList(1, 2, 3), stream(asList(1, 2, 3)).collect(Collectors.toList()));
        assertEquals(asList(1), stream(asList(1)).collect(Collectors.toList()));
        assertEquals(asList(), stream(asList()).collect(Collectors.toList()));
    }

    @Test
    public void testStreamCollectMap() {
        Map<Integer, Integer> m = new HashMap<>();
        m.put(1, 2);
        m.put(2, 4);
        assertEquals(m, stream(asList(1, 2)).collect(Collectors.toMap(a -> a, a -> a * 2)));
        assertEquals(new HashMap<>(), stream(asList())
                .collect(Collectors.toMap(a -> a, a -> a)));
    }

    @Test
    public void testStreamCollectSet() {
        assertEquals(new HashSet<>(asList(1, 2, 3)), stream(asList(1, 2, 1, 3, 3))
                .collect(Collectors.toSet()));
        assertEquals(new HashSet<>(asList(1)), stream(asList(1)).collect(Collectors.toSet()));
        assertEquals(new HashSet<>(), stream(asList()).collect(Collectors.toSet()));
    }

    @Test
    public void testStreamFilter() {
        assertEquals(asList(2), stream(asList(1, 2, 3))
                .filter(x -> (x % 2 == 0))
                .collect(Collectors.toList()));
        assertEquals(asList(), stream(asList(1))
                .filter(x -> (x % 2 == 0))
                .collect(Collectors.toList()));
        assertEquals(asList(), stream(asList()).filter(x -> ((Integer) x) % 2 == 0)
                .collect(Collectors.toList()));
    }

    @Test
    public void testStreamSortLiteralList() {
        assertEquals(asList(3, 2, 1), stream(asList(1, 2, 3))
                .sorted((a, b) -> b - a)
                .collect(Collectors.toList()));
        assertEquals(asList(1), stream(asList(1))
                .sorted((a, b) -> b - a)
                .collect(Collectors.toList()));
        assertEquals(asList(), stream(asList())
                .sorted((a, b) -> 0)
                .collect(Collectors.toList()));
    }

    @Test
    public void testStreamMap() {
        assertEquals(asList(2, 4, 6), stream(asList(1, 2, 3))
                .map(x -> x * 2)
                .collect(Collectors.toList()));
        assertEquals(asList(2), stream(asList(1))
                .map(x -> x * 2)
                .collect(Collectors.toList()));
        assertEquals(asList(), stream(asList())
                .map(x -> 1)
                .collect(Collectors.toList()));
    }

    @Test
    public void testStreamCount() {
        assertEquals(3, stream(asList(1, 2, 3)).count());
        assertEquals(1, stream(asList(1)).count());
        assertEquals(0, stream(asList()).count());
    }

    @Test
    public void testStreamLimit() {
        assertEquals(asList(1, 2), stream(asList(1, 2, 3, 4))
                .limit(2).collect(Collectors.toList()));
        assertEquals(asList(1), stream(asList(1)).limit(1).collect(Collectors.toList()));
        assertEquals(asList(), stream(asList()).limit(2).collect(Collectors.toList()));
    }

    @Test
    public void testStreamSkip() {
        assertEquals(asList(3, 4), stream(asList(1, 2, 3, 4))
                .skip(2).collect(Collectors.toList()));
        assertEquals(asList(), stream(asList(1)).skip(1).collect(Collectors.toList()));
        assertEquals(asList(), stream(asList()).skip(2).collect(Collectors.toList()));
    }

    @Test
    public void testStreamFilterAndMap() {
        assertEquals(asList(2, 6), stream(asList(1, 2, 3, 4))
                .filter(x -> x % 2 == 1)
                .map(x -> x * 2)
                .collect(Collectors.toList()));
        assertEquals(asList(), stream(asList(2))
                .filter(x -> x % 2 == 1)
                .map(x -> x * 2)
                .collect(Collectors.toList()));
    }

    @Test
    public void testStreamFlatMap() {
        assertEquals(asList(0, 0, 1, 0, 1, 2), stream(asList(0, 1, 2))
                .flatMap(x -> {
                    final List<Integer> r = new ArrayList();
                    for (int i = 0; i <= x; ++i) {
                        r.add(i);
                    }
                    return r.stream();
                })
                .collect(Collectors.toList()));
    }

    @Test
    public void testStreamForEach() {
        List<Integer> result = new ArrayList<>();
        asList(1, 2, 3).forEach(result::add);
        assertEquals(asList(1, 2, 3), result);
    }

    @Test
    public void testStreamOf() {
        List<Integer> result = new ArrayList<>();
        Stream.of(1, 2, 3).forEach(result::add);
        assertEquals(asList(1, 2, 3), result);
    }

    @Test
    public void testIntStreamRange() {
        List<String> result = new ArrayList<>();
        IntStream.range(0, 3)
                .mapToObj(String::valueOf)
                .forEach(result::add);
        assertEquals(asList("0", "1", "2"), result);
    }
}
