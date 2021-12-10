package org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.parameter.VariableCharacterisationType;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ExternalCallAction;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ParameterToOptimize;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCall;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCallDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.ArgumentEstimation;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.ArgumentModel;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic.Optimization;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic.OptimizationConfig;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic.OptimizationMode;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.PcmUtils;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.Utils;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl.ArgumentEstimationImpl;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl.WekaArgumentsModelEstimation;

/**
 * Class for estimating parametric dependencies for external call arguments and
 * updating the PCM
 * 
 * @author SonyaV
 *
 */
public class ArgumentEstimationImpl implements ArgumentEstimation {

	private static final Logger LOGGER = Logger.getLogger(ArgumentEstimationImpl.class);

	// service ID to <parameter name, parameter model>
	private final Map<String, Map<String, ArgumentModel>> modelCache;
	private final boolean withOptimization;
	private final boolean withFeatureSelection;
	private final boolean withReturnValue;
	private final OptimizationConfig config;

	public ArgumentEstimationImpl(boolean withOpt, boolean withFeatureSelection, boolean withReturnValue, OptimizationConfig config) {
		this(ThreadLocalRandom.current(), withOpt, withFeatureSelection, withReturnValue, config);
	}

	public ArgumentEstimationImpl(final Random random, boolean withOpt, boolean withFeatureSelection, boolean withReturnValue, OptimizationConfig config) {
		this.withOptimization = withOpt;
		this.withFeatureSelection = withFeatureSelection;
		this.withReturnValue = withReturnValue;
		
		this.config = config;
		this.modelCache = new HashMap<>();
	}

	/**
	 * creates argument models for every external call from serviceCalls
	 * 
	 * @param pcm                 Palladio Component Model to update
	 * @param externalCallRecords the external calls from this model
	 */
	@Override
	public void update(Repository pcm, ServiceCallDataSet externalCallRecords) {
		List<ExternalCallAction> externalCallActions = PcmUtils.getObjects(pcm, ExternalCallAction.class);
		WekaArgumentsModelEstimation estimation = new WekaArgumentsModelEstimation(externalCallRecords, pcm,
				externalCallActions, withFeatureSelection, withReturnValue);

		Map<String, Map<String, ArgumentModel>> argumentModels = estimation.estimateAll();
		for (ServiceCall record : externalCallRecords.getServiceCalls())
			this.modelCache.put(record.getCallerId().get(), argumentModels.get(record.getCallerId().get()));
		this.applyEstimations(pcm);
	}

	/**
	 * applies the corresponding model for every external call from pcmModel
	 * 
	 * @param pcm Palladio Component Model
	 */
	private void applyEstimations(final Repository pcm) {
		List<ExternalCallAction> externalCallActions = PcmUtils.getObjects(pcm, ExternalCallAction.class);
		for (ExternalCallAction action : externalCallActions) {
			this.applyModel(action);
		}
	}

	/**
	 * applies the corresponding model for the specific external call
	 * 
	 * @param externalCall external call to apply the estimation model to
	 */
	private void applyModel(final ExternalCallAction externalCall) {
		// get the corresponding estimation model
		Map<String, ArgumentModel> parameterModels = this.modelCache.get(externalCall.getId());
		if (parameterModels == null) {
			LOGGER.warn("A estimation for the parameters of external call with id " + externalCall.getId()
					+ " was not found.");
			return;
		}
		for (Entry<String, ArgumentModel> parModel : parameterModels.entrySet()) {
			String parameterName = parModel.getKey();
			String stoEx;

			// for now only numeric models with error >= 0.1 are optimized
			if (parModel.getValue().getClass().getSimpleName().equals("WekaNumericArgumentModel")
					&& parModel.getValue().getError() >= 0.1 && withOptimization
					&& parModel.getValue().getWekaDataSet().getAttributes().size() > 1) {
				System.out.println("Initial error for " + parameterName + ": " + parModel.getValue().getError());
				System.out.println("->opt");
				stoEx = optimize(parModel.getKey(), parModel.getValue());

			} else {
				System.out.println("Initial error for " + parameterName + ": " + parModel.getValue().getError());
				stoEx = parModel.getValue().getStochasticExpression();
			}
			
			if (parModel.getValue().isIntegerOnly()) {
				stoEx = Utils.replaceDoubles(stoEx);
			}
			
			stoEx = Utils.replaceUnderscoreWithDot(stoEx);
			System.out.println("Final stoEx for " + parameterName + ": " + stoEx);
			VariableUsage varUsage = PcmUtils.createVariableUsage(parameterName, VariableCharacterisationType.VALUE,
					stoEx);
			externalCall.getInputVariableUsages__CallAction().add(varUsage);
			System.out.println("---------------------------------");
		}
	}

	/**
	 * starts the genetic programming optimization algorithm
	 * 
	 * @param parameterName
	 * @param model         model as base of the optimization
	 */
	private String optimize(String parameterName, ArgumentModel model) {
		ParameterToOptimize spToOpt = new ParameterToOptimize(parameterName, model);
		Optimization op = new Optimization(spToOpt, config);
		op.start();
		System.out.println("Optimized StoEx for parameter: " + parameterName + ": "
				+ Utils.replaceUnderscoreWithDot(op.getOptimizedStochasticExpression()));
		// op.printTree();
		op.printStats();
		return op.getOptimizedStochasticExpression();
	}
}
