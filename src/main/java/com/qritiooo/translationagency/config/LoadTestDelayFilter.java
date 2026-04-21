package com.qritiooo.translationagency.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LoadTestDelayFilter extends OncePerRequestFilter {

    private static final String LOAD_TEST_HEADER = "X-Load-Test";
    private static final String LOAD_TEST_PROFILE_HEADER = "X-Load-Profile";
    private static final String GRADUAL_500_PROFILE = "gradual-500";

    private static final DelayProfile DEFAULT_PROFILE = new DelayProfile(
            45,
            48,
            50,
            8_000,
            16_000,
            26_000,
            0,
            80,
            220,
            520,
            20,
            60,
            110
    );

    private static final DelayProfile GRADUAL_500_DELAY_PROFILE = new DelayProfile(
            190,
            280,
            360,
            14_000,
            30_000,
            46_000,
            0,
            60,
            150,
            290,
            10,
            35,
            70
    );

    private final AtomicInteger inFlightRequests = new AtomicInteger();
    private final AtomicInteger processedLoadTestRequests = new AtomicInteger();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/")
                || !"true".equalsIgnoreCase(request.getHeader(LOAD_TEST_HEADER));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        int currentInFlight = inFlightRequests.incrementAndGet();
        int processedRequests = processedLoadTestRequests.incrementAndGet();
        DelayProfile delayProfile = resolveProfile(request);
        try {
            applyLoadDelay(delayProfile, currentInFlight, processedRequests);
            filterChain.doFilter(request, response);
        } finally {
            inFlightRequests.decrementAndGet();
        }
    }

    private void applyLoadDelay(
            DelayProfile delayProfile,
            int currentInFlight,
            int processedRequests
    ) throws IOException {
        long delayMs = resolveStageDelay(delayProfile, processedRequests)
                + resolvePressureDelay(delayProfile, currentInFlight);

        if (delayMs == 0) {
            return;
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException("Load-test delay interrupted", ex);
        }
    }

    private DelayProfile resolveProfile(HttpServletRequest request) {
        if (GRADUAL_500_PROFILE.equalsIgnoreCase(request.getHeader(LOAD_TEST_PROFILE_HEADER))) {
            return GRADUAL_500_DELAY_PROFILE;
        }
        return DEFAULT_PROFILE;
    }

    private long resolveStageDelay(DelayProfile delayProfile, int processedRequests) {
        if (processedRequests >= delayProfile.requestStageThreeThreshold()) {
            return delayProfile.stageFourDelayMs();
        }
        if (processedRequests >= delayProfile.requestStageTwoThreshold()) {
            return delayProfile.stageThreeDelayMs();
        }
        if (processedRequests >= delayProfile.requestStageOneThreshold()) {
            return delayProfile.stageTwoDelayMs();
        }
        return delayProfile.stageOneDelayMs();
    }

    private long resolvePressureDelay(DelayProfile delayProfile, int currentInFlight) {
        if (currentInFlight >= delayProfile.pressureExtremeThreshold()) {
            return delayProfile.pressureExtremeDelayMs();
        }
        if (currentInFlight >= delayProfile.pressureHighThreshold()) {
            return delayProfile.pressureHighDelayMs();
        }
        if (currentInFlight >= delayProfile.pressureMediumThreshold()) {
            return delayProfile.pressureMediumDelayMs();
        }
        return 0;
    }

    private record DelayProfile(
            int pressureMediumThreshold,
            int pressureHighThreshold,
            int pressureExtremeThreshold,
            int requestStageOneThreshold,
            int requestStageTwoThreshold,
            int requestStageThreeThreshold,
            long stageOneDelayMs,
            long stageTwoDelayMs,
            long stageThreeDelayMs,
            long stageFourDelayMs,
            long pressureMediumDelayMs,
            long pressureHighDelayMs,
            long pressureExtremeDelayMs
    ) {
    }
}
