package org.palladiosimulator.somox.cipm.modelrefinement.parameters.monitoring;

import kieker.common.record.IMonitoringRecord;

public interface MonitoringRecordsWriter {

    void write(IMonitoringRecord monitoringRecord);

}