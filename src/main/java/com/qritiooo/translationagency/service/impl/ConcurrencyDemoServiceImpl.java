package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.dto.response.RaceConditionDemoResponse;
import com.qritiooo.translationagency.service.ConcurrencyDemoService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class ConcurrencyDemoServiceImpl implements ConcurrencyDemoService {

    private static final int THREAD_COUNT = 50;
    private static final int INCREMENTS_PER_THREAD = 2_000;
    private static final int EXPECTED_VALUE = THREAD_COUNT * INCREMENTS_PER_THREAD;
    private static final int UNSAFE_ATTEMPTS = 5;

    @Override
    public RaceConditionDemoResponse demonstrateUnsafeCounter() {
        for (int attempt = 0; attempt < UNSAFE_ATTEMPTS; attempt++) {
            int actualValue = runUnsafeScenario();
            if (actualValue != EXPECTED_VALUE) {
                return buildResponse("unsafe", actualValue);
            }
        }
        return buildResponse("unsafe", EXPECTED_VALUE);
    }

    @Override
    public RaceConditionDemoResponse demonstrateAtomicCounter() {
        int actualValue = runAtomicScenario();
        return buildResponse("atomic", actualValue);
    }

    private RaceConditionDemoResponse buildResponse(String mode, int actualValue) {
        return new RaceConditionDemoResponse(
                mode,
                THREAD_COUNT,
                INCREMENTS_PER_THREAD,
                EXPECTED_VALUE,
                actualValue,
                EXPECTED_VALUE - actualValue,
                actualValue != EXPECTED_VALUE
        );
    }

    private int runUnsafeScenario() {
        UnsafeCounter counter = new UnsafeCounter();
        executeInParallel(counter::increment);
        return counter.getValue();
    }

    private int runAtomicScenario() {
        AtomicCounter counter = new AtomicCounter();
        executeInParallel(counter::increment);
        return counter.getValue();
    }

    private void executeInParallel(IncrementAction incrementAction) {
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
                future.get();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to execute concurrency demo", ex);
        } finally {
            executorService.shutdown();
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Concurrency demo interrupted", ex);
            }
        }
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
