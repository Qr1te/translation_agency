package com.qritiooo.translationagency.concurrency;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class RaceConditionDemoTest {

    private static final int THREAD_COUNT = 50;
    private static final int INCREMENTS_PER_THREAD = 2_000;
    private static final int EXPECTED_TOTAL = THREAD_COUNT * INCREMENTS_PER_THREAD;

    @Test
    void shouldDemonstrateRaceConditionWithUnsafeCounter() throws Exception {
        int actual = runUnsafeCounterScenario();

        assertNotEquals(EXPECTED_TOTAL, actual);
    }

    @Test
    void shouldSolveRaceConditionWithAtomicCounter() throws Exception {
        int actual = runAtomicCounterScenario();

        assertTrue(actual == EXPECTED_TOTAL);
    }

    private int runUnsafeCounterScenario() throws Exception {
        UnsafeCounter counter = new UnsafeCounter();
        executeInParallel(counter::increment);
        return counter.getValue();
    }

    private int runAtomicCounterScenario() throws Exception {
        AtomicCounter counter = new AtomicCounter();
        executeInParallel(counter::increment);
        return counter.getValue();
    }

    private void executeInParallel(IncrementAction incrementAction) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int threadIndex = 0; threadIndex < THREAD_COUNT; threadIndex++) {
                tasks.add(() -> {
                    for (int incrementIndex = 0; incrementIndex < INCREMENTS_PER_THREAD;
                            incrementIndex++) {
                        incrementAction.increment();
                    }
                    return null;
                });
            }

            List<Future<Void>> futures = executorService.invokeAll(tasks);
            for (Future<Void> future : futures) {
                awaitFuture(future);
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private void awaitFuture(Future<Void> future) throws InterruptedException, ExecutionException {
        future.get();
    }

    @FunctionalInterface
    private interface IncrementAction {
        void increment();
    }

    private static final class UnsafeCounter {
        private int value;

        private void increment() {
            value++;
        }

        private int getValue() {
            return value;
        }
    }

    private static final class AtomicCounter {
        private final AtomicInteger value = new AtomicInteger();

        private void increment() {
            value.incrementAndGet();
        }

        private int getValue() {
            return value.get();
        }
    }
}
