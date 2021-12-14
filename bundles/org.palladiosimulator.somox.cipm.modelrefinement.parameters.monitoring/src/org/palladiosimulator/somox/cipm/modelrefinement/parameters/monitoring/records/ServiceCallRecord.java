package org.palladiosimulator.somox.cipm.modelrefinement.parameters.monitoring.records;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.io.IValueSerializer;
import kieker.common.util.registry.IRegistry;

/***
 * 
 * @author Generic Kieker API compatibility:Kieker 1.10.0**@since 1.10
 */
public class ServiceCallRecord extends AbstractMonitoringRecord
        implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory, ServiceContextRecord {
    /** Descriptive definition of the serialization size of the record. */
    public static final int SIZE = TYPE_SIZE_STRING // RecordWithSession.sessionId
            + TYPE_SIZE_STRING // ServiceContextRecord.serviceExecutionId
            + TYPE_SIZE_STRING // ServiceCallRecord.serviceId
            + TYPE_SIZE_STRING // ServiceCallRecord.parameters
            + TYPE_SIZE_STRING // ServiceCallRecord.callerServiceExecutionId
            + TYPE_SIZE_STRING // ServiceCallRecord.callerId
            + TYPE_SIZE_LONG // ServiceCallRecord.entryTime
            + TYPE_SIZE_LONG // ServiceCallRecord.exitTime
            + TYPE_SIZE_STRING; // ServiceCallRecord.returnValue

    public static final Class<?>[] TYPES = {
            String.class, // RecordWithSession.sessionId
            String.class, // ServiceContextRecord.serviceExecutionId
            String.class, // ServiceCallRecord.serviceId
            String.class, // ServiceCallRecord.parameters
            String.class, // ServiceCallRecord.callerServiceExecutionId
            String.class, // ServiceCallRecord.callerId
            long.class, // ServiceCallRecord.entryTime
            long.class, // ServiceCallRecord.exitTime
            String.class, // ServiceCallRecord.returnValue
    };

    /** default constants. */
    public static final String SESSION_ID = "<not set>";
    public static final String SERVICE_EXECUTION_ID = "<not set>";
    public static final String SERVICE_ID = "<not set>";
    public static final String PARAMETERS = "<not set>";
    public static final String CALLER_SERVICE_EXECUTION_ID = "<not set>";
    public static final String CALLER_ID = "<not set>";
    public static final String RETURN_VALUE = "<not set>";
    private static final long serialVersionUID = 6294390637095843823L;

    /** property name array. */
    private static final String[] PROPERTY_NAMES = {
            "sessionId",
            "serviceExecutionId",
            "serviceId",
            "parameters",
            "callerServiceExecutionId",
            "callerId",
            "entryTime",
            "exitTime",
            "returnValue",
    };

    /** property declarations. */
    private final String sessionId;
    private final String serviceExecutionId;
    private final String serviceId;
    private final String parameters;
    private final String callerServiceExecutionId;
    private final String callerId;
    private final long entryTime;
    private final long exitTime;
    private final String returnValue;

    /**
     * Creates a new instance of this class using the given parameters.
     *
     * @param sessionId
     *            sessionId
     * @param serviceExecutionId
     *            serviceExecutionId
     * @param serviceId
     *            serviceId
     * @param parameters
     *            parameters
     * @param callerServiceExecutionId
     *            callerServiceExecutionId
     * @param callerId
     *            callerId
     * @param entryTime
     *            entryTime
     * @param exitTime
     *            exitTime
     * @param returnValue
     *            returnValue
     */
    public ServiceCallRecord(final String sessionId, final String serviceExecutionId, final String serviceId,
            final String parameters, final String callerServiceExecutionId, final String callerId, final long entryTime,
            final long exitTime, final String returnValue) {
        this.sessionId = sessionId == null ? SESSION_ID : sessionId;
        this.serviceExecutionId = serviceExecutionId == null ? SERVICE_EXECUTION_ID : serviceExecutionId;
        this.serviceId = serviceId == null ? SERVICE_ID : serviceId;
        this.parameters = parameters == null ? PARAMETERS : parameters;
        this.callerServiceExecutionId = callerServiceExecutionId == null ? CALLER_SERVICE_EXECUTION_ID
                : callerServiceExecutionId;
        this.callerId = callerId == null ? CALLER_ID : callerId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.returnValue = returnValue == null ? RETURN_VALUE : returnValue;
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
    public ServiceCallRecord(final Object[] values) { // NOPMD (direct store of values)
        AbstractMonitoringRecord.checkArray(values, TYPES);
        this.sessionId = (String) values[0];
        this.serviceExecutionId = (String) values[1];
        this.serviceId = (String) values[2];
        this.parameters = (String) values[3];
        this.callerServiceExecutionId = (String) values[4];
        this.callerId = (String) values[5];
        this.entryTime = (Long) values[6];
        this.exitTime = (Long) values[7];
        this.returnValue = (String) values[8];
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
    protected ServiceCallRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
        AbstractMonitoringRecord.checkArray(values, valueTypes);
        this.sessionId = (String) values[0];
        this.serviceExecutionId = (String) values[1];
        this.serviceId = (String) values[2];
        this.parameters = (String) values[3];
        this.callerServiceExecutionId = (String) values[4];
        this.callerId = (String) values[5];
        this.entryTime = (Long) values[6];
        this.exitTime = (Long) values[7];
        this.returnValue = (String) values[8];
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
    public ServiceCallRecord(final ByteBuffer buffer, final IRegistry<String> stringRegistry)
            throws BufferUnderflowException {
        this.sessionId = stringRegistry.get(buffer.getInt());
        this.serviceExecutionId = stringRegistry.get(buffer.getInt());
        this.serviceId = stringRegistry.get(buffer.getInt());
        this.parameters = stringRegistry.get(buffer.getInt());
        this.callerServiceExecutionId = stringRegistry.get(buffer.getInt());
        this.callerId = stringRegistry.get(buffer.getInt());
        this.entryTime = buffer.getLong();
        this.exitTime = buffer.getLong();
        this.returnValue = stringRegistry.get(buffer.getInt());
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
                this.getServiceId(),
                this.getParameters(),
                this.getCallerServiceExecutionId(),
                this.getCallerId(),
                this.getEntryTime(),
                this.getExitTime(),
                this.getReturnValue(),
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerStrings(final IRegistry<String> stringRegistry) { // NOPMD (generated code)
        stringRegistry.get(this.getSessionId());
        stringRegistry.get(this.getServiceExecutionId());
        stringRegistry.get(this.getServiceId());
        stringRegistry.get(this.getParameters());
        stringRegistry.get(this.getCallerServiceExecutionId());
        stringRegistry.get(this.getCallerId());
        stringRegistry.get(this.getReturnValue());
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
        buffer.putInt(stringRegistry.get(this.getServiceId()));
        buffer.putInt(stringRegistry.get(this.getParameters()));
        buffer.putInt(stringRegistry.get(this.getCallerServiceExecutionId()));
        buffer.putInt(stringRegistry.get(this.getCallerId()));
        buffer.putLong(this.getEntryTime());
        buffer.putLong(this.getExitTime());
        buffer.putInt(stringRegistry.get(this.getReturnValue()));
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

        final ServiceCallRecord castedRecord = (ServiceCallRecord) obj;
        if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
            return false;
        }
        if (!this.getSessionId().equals(castedRecord.getSessionId())) {
            return false;
        }
        if (!this.getServiceExecutionId().equals(castedRecord.getServiceExecutionId())) {
            return false;
        }
        if (!this.getServiceId().equals(castedRecord.getServiceId())) {
            return false;
        }
        if (!this.getParameters().equals(castedRecord.getParameters())) {
            return false;
        }
        if (!this.getCallerServiceExecutionId().equals(castedRecord.getCallerServiceExecutionId())) {
            return false;
        }
        if (!this.getCallerId().equals(castedRecord.getCallerId())) {
            return false;
        }
        if (this.getEntryTime() != castedRecord.getEntryTime()) {
            return false;
        }
        if (this.getExitTime() != castedRecord.getExitTime()) {
            return false;
        }
        if (!this.getReturnValue().equals(castedRecord.getReturnValue())) {
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

    public final String getServiceId() {
        return this.serviceId;
    }

    public final String getParameters() {
        return this.parameters;
    }

    public final String getCallerServiceExecutionId() {
        return this.callerServiceExecutionId;
    }

    public final String getCallerId() {
        return this.callerId;
    }

    public final long getEntryTime() {
        return this.entryTime;
    }

    public final long getExitTime() {
        return this.exitTime;
    }

    public final String getReturnValue() {
        return this.returnValue;
    }

    @Override
    public void serialize(IValueSerializer arg0) throws BufferOverflowException {
        arg0.putString(this.sessionId);
        arg0.putString(this.serviceExecutionId);
        arg0.putString(this.serviceId);
        arg0.putString(this.parameters);
        arg0.putString(this.callerServiceExecutionId);
        arg0.putString(this.callerId);
        arg0.putLong(this.entryTime);
        arg0.putLong(this.exitTime);
        arg0.putString(this.returnValue);
    }
}
