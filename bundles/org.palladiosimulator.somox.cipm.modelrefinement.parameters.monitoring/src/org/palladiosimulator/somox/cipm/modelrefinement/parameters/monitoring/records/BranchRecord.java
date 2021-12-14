package org.palladiosimulator.somox.cipm.modelrefinement.parameters.monitoring.records;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.io.IValueSerializer;
import kieker.common.util.registry.IRegistry;

/**
 * @author Generic Kieker API compatibility: Kieker 1.10.0
 * 
 * @since 1.10
 */
public class BranchRecord extends AbstractMonitoringRecord
        implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory, ServiceContextRecord {
    /** Descriptive definition of the serialization size of the record. */
    public static final int SIZE = TYPE_SIZE_STRING // RecordWithSession.sessionId
            + TYPE_SIZE_STRING // ServiceContextRecord.serviceExecutionId
            + TYPE_SIZE_STRING // BranchRecord.branchId
            + TYPE_SIZE_STRING; // BranchRecord.executedBranchId

    public static final Class<?>[] TYPES = {
            String.class, // RecordWithSession.sessionId
            String.class, // ServiceContextRecord.serviceExecutionId
            String.class, // BranchRecord.branchId
            String.class, // BranchRecord.executedBranchId
    };

    /** default constants. */
    public static final String SESSION_ID = "<not set>";
    public static final String SERVICE_EXECUTION_ID = "<not set>";
    public static final String BRANCH_ID = "<not set>";
    public static final String EXECUTED_BRANCH_ID = "<not set>";
    private static final long serialVersionUID = 2435150336822263830L;

    /** property name array. */
    private static final String[] PROPERTY_NAMES = {
            "sessionId",
            "serviceExecutionId",
            "branchId",
            "executedBranchId",
    };

    /** property declarations. */
    private final String sessionId;
    private final String serviceExecutionId;
    private final String branchId;
    private final String executedBranchId;

    /**
     * Creates a new instance of this class using the given parameters.
     * 
     * @param sessionId
     *            sessionId
     * @param serviceExecutionId
     *            serviceExecutionId
     * @param branchId
     *            branchId
     * @param executedBranchId
     *            executedBranchId
     */
    public BranchRecord(final String sessionId, final String serviceExecutionId, final String branchId,
            final String executedBranchId) {
        this.sessionId = sessionId == null ? SESSION_ID : sessionId;
        this.serviceExecutionId = serviceExecutionId == null ? SERVICE_EXECUTION_ID : serviceExecutionId;
        this.branchId = branchId == null ? BRANCH_ID : branchId;
        this.executedBranchId = executedBranchId == null ? EXECUTED_BRANCH_ID : executedBranchId;
    }

    /**
     * This constructor converts the given array into a record. It is recommended to use the array which is the result
     * of a call to {@link #toArray()}.
     * 
     * @param values
     *            The values for the record.
     *
     * @deprecated to be removed 1.15
     */
    @Deprecated
    public BranchRecord(final Object[] values) { // NOPMD (direct store of values)
        AbstractMonitoringRecord.checkArray(values, TYPES);
        this.sessionId = (String) values[0];
        this.serviceExecutionId = (String) values[1];
        this.branchId = (String) values[2];
        this.executedBranchId = (String) values[3];
    }

    /**
     * This constructor uses the given array to initialize the fields of this record.
     * 
     * @param values
     *            The values for the record.
     * @param valueTypes
     *            The types of the elements in the first array.
     *
     * @deprecated to be removed 1.15
     */
    @Deprecated
    protected BranchRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
        AbstractMonitoringRecord.checkArray(values, valueTypes);
        this.sessionId = (String) values[0];
        this.serviceExecutionId = (String) values[1];
        this.branchId = (String) values[2];
        this.executedBranchId = (String) values[3];
    }

    /**
     * This constructor converts the given buffer into a record.
     * 
     * @param buffer
     *            The bytes for the record
     * @param stringRegistry
     *            The string registry for deserialization
     * 
     * @throws BufferUnderflowException
     *             if buffer not sufficient
     *
     * @deprecated to be removed in 1.15
     */
    @Deprecated
    public BranchRecord(final ByteBuffer buffer, final IRegistry<String> stringRegistry)
            throws BufferUnderflowException {
        this.sessionId = stringRegistry.get(buffer.getInt());
        this.serviceExecutionId = stringRegistry.get(buffer.getInt());
        this.branchId = stringRegistry.get(buffer.getInt());
        this.executedBranchId = stringRegistry.get(buffer.getInt());
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated to be removed in 1.15
     */
    @Override
    @Deprecated
    public Object[] toArray() {
        return new Object[] {
                this.getSessionId(),
                this.getServiceExecutionId(),
                this.getBranchId(),
                this.getExecutedBranchId(),
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerStrings(final IRegistry<String> stringRegistry) { // NOPMD (generated code)
        stringRegistry.get(this.getSessionId());
        stringRegistry.get(this.getServiceExecutionId());
        stringRegistry.get(this.getBranchId());
        stringRegistry.get(this.getExecutedBranchId());
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated since 1.13 to be removed in 1.15
     */
    @Deprecated
    public void writeBytes(final ByteBuffer buffer, final IRegistry<String> stringRegistry)
            throws BufferOverflowException {
        buffer.putInt(stringRegistry.get(this.getSessionId()));
        buffer.putInt(stringRegistry.get(this.getServiceExecutionId()));
        buffer.putInt(stringRegistry.get(this.getBranchId()));
        buffer.putInt(stringRegistry.get(this.getExecutedBranchId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?>[] getValueTypes() {
        return TYPES; // NOPMD
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getValueNames() {
        return PROPERTY_NAMES; // NOPMD
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return SIZE;
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated to be rmeoved in 1.15
     */
    @Override
    @Deprecated
    public void initFromArray(final Object[] values) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated to be rmeoved in 1.15
     */
    @Deprecated
    public void initFromBytes(final ByteBuffer buffer, final IRegistry<String> stringRegistry)
            throws BufferUnderflowException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final BranchRecord castedRecord = (BranchRecord) obj;
        if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
            return false;
        }
        if (!this.getSessionId().equals(castedRecord.getSessionId())) {
            return false;
        }
        if (!this.getServiceExecutionId().equals(castedRecord.getServiceExecutionId())) {
            return false;
        }
        if (!this.getBranchId().equals(castedRecord.getBranchId())) {
            return false;
        }
        if (!this.getExecutedBranchId().equals(castedRecord.getExecutedBranchId())) {
            return false;
        }

        return true;
    }

    public final String getSessionId() {
        return this.sessionId;
    }

    public final String getServiceExecutionId() {
        return this.serviceExecutionId;
    }

    public final String getBranchId() {
        return this.branchId;
    }

    public final String getExecutedBranchId() {
        return this.executedBranchId;
    }

    @Override
    public void serialize(IValueSerializer arg0) throws BufferOverflowException {
        arg0.putString(this.sessionId);
        arg0.putString(this.serviceExecutionId);
        arg0.putString(this.branchId);
        arg0.putString(this.executedBranchId);
    }

}
