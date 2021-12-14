package org.palladiosimulator.somox.cipm.modelrefinement.parameters.monitoring;

import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.core.controller.IMonitoringController;

public class KiekerMonitoringRecordsWriter implements MonitoringRecordsWriter {

    private final IMonitoringController monitoringController;

    public KiekerMonitoringRecordsWriter(IMonitoringController monitoringController) {
        this.monitoringController = monitoringController;
    }

    @Override
    public synchronized void write(IMonitoringRecord monitoringRecord) {
        this.monitoringController.newMonitoringRecord(monitoringRecord);
    }
}
