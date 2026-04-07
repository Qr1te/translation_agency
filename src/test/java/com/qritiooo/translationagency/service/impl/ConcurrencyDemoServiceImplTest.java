package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConcurrencyDemoServiceImplTest {

    private final ConcurrencyDemoServiceImpl service = new ConcurrencyDemoServiceImpl();

    @Test
    void demonstrateAtomicCounter_ShouldReturnExpectedValueWithoutRaceCondition() {
        var response = service.demonstrateAtomicCounter();

        assertEquals("atomic", response.mode());
        assertEquals(response.expectedValue(), response.actualValue());
        assertEquals(0, response.lostUpdates());
        assertFalse(response.raceConditionDetected());
    }

    @Test
    void demonstrateUnsafeCounter_ShouldReturnUnsafeResponseShape() {
        var response = service.demonstrateUnsafeCounter();

        assertEquals("unsafe", response.mode());
        assertEquals(50, response.threadCount());
        assertEquals(2_000, response.incrementsPerThread());
        assertEquals(response.expectedValue() - response.actualValue(), response.lostUpdates());
    }

    @Test
    void demonstrateUnsafeCounter_ShouldReturnExpectedValueWhenNoRaceConditionDetected() {
        ConcurrencyDemoServiceImpl deterministicService = new ConcurrencyDemoServiceImpl() {
            @Override
            int runUnsafeScenario() {
                return ConcurrencyDemoServiceImpl.EXPECTED_VALUE;
            }
        };

        var response = deterministicService.demonstrateUnsafeCounter();

        assertEquals("unsafe", response.mode());
        assertEquals(ConcurrencyDemoServiceImpl.EXPECTED_VALUE, response.actualValue());
        assertEquals(0, response.lostUpdates());
        assertFalse(response.raceConditionDetected());
    }

    @Test
    void executeInParallel_ShouldWrapInterruptedException() {
        Thread.currentThread().interrupt();
        try {
            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> service.executeInParallel(() -> {
                    })
            );

            assertTrue(ex.getMessage().contains("interrupted"));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void executeInParallel_ShouldWrapTaskFailure() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.executeInParallel(() -> {
                    throw new RuntimeException("boom");
                })
        );

        assertTrue(ex.getMessage().contains("Failed to execute concurrency demo"));
        assertNotNull(ex.getCause());
    }
}
