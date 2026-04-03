package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.response.RaceConditionDemoResponse;

public interface ConcurrencyDemoService {
    RaceConditionDemoResponse demonstrateUnsafeCounter();

    RaceConditionDemoResponse demonstrateAtomicCounter();
}
