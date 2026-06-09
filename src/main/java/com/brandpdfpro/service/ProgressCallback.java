package com.brandpdfpro.service;

/**
 * Functional callback interface used to propagate task status updates.
 * <p>
 * Implemented by UI controllers or tracking components to safely receive
 * execution progress metrics from asynchronous processing engines without
 * tightly coupling background services to the presentation layer.
 * </p>
 */
@FunctionalInterface
public interface ProgressCallback {

    /**
     * Dispatches runtime progression indices and message details back to the subscriber.
     *
     * @param current the current index number of tasks processed so far
     * @param total   the maximum bound total count of tasks scheduled for processing
     * @param message a descriptive context label detailing the active sub-operation state
     */
    void updateProgress(int current, int total, String message);
}