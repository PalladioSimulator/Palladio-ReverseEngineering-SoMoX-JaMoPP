package org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ExternalCallAction;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCall;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCallDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceParameters;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.ArgumentModel;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.PcmUtils;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSetBuilder;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSetMode;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl.WekaNominalArgumentModel;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl.WekaNumericArgumentModel;

public class WekaArgumentsModelEstimation {
	private final ServiceCallDataSet externalCallRecords;
	private final Repository pcm;
	private final List<ExternalCallAction> externalCallActions;
	private final boolean withFeatureSelection;
	private final boolean withReturnValue;

	public WekaArgumentsModelEstimation(final ServiceCallDataSet externalCallRecords, final Repository pcm,
			final List<ExternalCallAction> externalCallActions, boolean withFeatureSelection, boolean withReturnValue) {
		this.externalCallRecords = externalCallRecords;
		this.pcm = pcm;
		this.externalCallActions = externalCallActions;
		this.withFeatureSelection = withFeatureSelection;
		this.withReturnValue = withReturnValue;
	}

	/**
	 * Create estimation model for service with id serviceId
	 * 
	 * @param serviceId
	 * @return
	 */
	public Map<String, ArgumentModel> estimate(final ExternalCallAction action) {
		try {
			return this.internEstimate(action);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Estimate the parameters of all external calls
	 * 
	 * @return Map <serviceId,<parameterName, ArgumentModel>>
	 */
	public Map<String, Map<String, ArgumentModel>> estimateAll() {
		HashMap<String, Map<String, ArgumentModel>> serviceToParameterToModel = new HashMap<String, Map<String, ArgumentModel>>();
		for (ExternalCallAction action : this.externalCallActions) {
			String serviceId = action.getId();
			serviceToParameterToModel.put(serviceId, estimate(action));
		}
		return serviceToParameterToModel;
	}

	private Map<String, ArgumentModel> internEstimate(final ExternalCallAction action) throws Exception {
		String serviceId = PcmUtils.signatureToSeffId(pcm, action.getCalledService_ExternalService().getId());

		List<ServiceCall> records = this.externalCallRecords.getServiceCallsByOneCaller(serviceId, action.getId());
		Map<String, ArgumentModel> models = new HashMap<String, ArgumentModel>(); // parameter names to models
		Map<String, WekaDataSetBuilder<Object>> parameterNameToDataSetBuilder = new HashMap<String, WekaDataSetBuilder<Object>>();

		if (records.size() == 0) {
			throw new IllegalStateException("No records for service id " + serviceId + " found.");
		}

		for (ServiceCall record : records) {
			for (Entry<String, Object> calleeParameter : record.getParameters().getParameters().entrySet()) {

				WekaDataSetBuilder<Object> builder = null;

				if (!parameterNameToDataSetBuilder.containsKey(calleeParameter.getKey())) {
					builder = (WekaDataSetBuilder<Object>) whichBuilder(calleeParameter.getValue(),
							this.externalCallRecords);
					parameterNameToDataSetBuilder.put(calleeParameter.getKey(), builder);
				} else {
					builder = parameterNameToDataSetBuilder.get(calleeParameter.getKey());
				}
					ServiceParameters returnValues = getReturnValues(action, record.getCallerServiceExecutionId());
					if ((returnValues.getParameters().isEmpty() && withReturnValue) || !withReturnValue) {
						builder.addInstance(record.getCallerServiceExecutionId(), calleeParameter.getValue());
					} else {
						builder.addInstanceWithReturnValues(record.getCallerServiceExecutionId(), returnValues,
								calleeParameter.getValue());
					}				
			}
		}
		parameterNameToDataSetBuilder.forEach((k, v) -> {

			try {
				boolean intOnly = v.getMode() == WekaDataSetMode.IntegerOnly;
				if (v.getMode() == WekaDataSetMode.NumericOnly || v.getMode() == WekaDataSetMode.IntegerOnly) {
					models.put(k, new WekaNumericArgumentModel(v.build(), intOnly, withFeatureSelection));
				} else {
					models.put(k, new WekaNominalArgumentModel(v.build(), withFeatureSelection));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return models;
	}

	private WekaDataSetBuilder<?> whichBuilder(Object parameter, ServiceCallDataSet serviceCalls) {
		if (parameter instanceof Double || parameter instanceof Long || parameter instanceof Float) {
			return new WekaDataSetBuilder<Long>(serviceCalls, WekaDataSetMode.NumericOnly);
		} else if (parameter instanceof String || parameter instanceof Boolean || parameter instanceof Character) {
			return new WekaDataSetBuilder<String>(serviceCalls, WekaDataSetMode.NoTransformations);
		} else if (parameter instanceof Iterable || parameter instanceof Integer) {
			return new WekaDataSetBuilder<Integer>(serviceCalls, WekaDataSetMode.IntegerOnly);
		} else {
			throw new RuntimeException(
					"Handling parameter of type: " + parameter.getClass().getSimpleName() + " is not implemented.");
		}
	}

	private ServiceParameters getReturnValues(ExternalCallAction action, String callerExecutionId) {
		List<String> predecessorsIds = PcmUtils.getPredecessorsSeffIds(pcm, action);
		ServiceParameters returnValues = new ServiceParameters();

		for (ServiceCall record : this.externalCallRecords.getServiceCalls()) {
			if (record.getCallerServiceExecutionId().equals(callerExecutionId)
					&& predecessorsIds.contains(record.getServiceId())) {
				returnValues = returnValues.merge(record.getReturnValue());
			}
		}
		return returnValues;
	}
}
