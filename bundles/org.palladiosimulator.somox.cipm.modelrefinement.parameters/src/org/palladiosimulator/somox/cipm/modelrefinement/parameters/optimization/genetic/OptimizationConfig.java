package org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic.OptimizationConfig;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic.OptimizationMode;

/**
 * Class for specifying the configuration of an optimization.
 * 
 * @author SonyaV
 *
 */
public class OptimizationConfig {

	public static final OptimizationConfig EMPTY = new OptimizationConfig();
	public static final OptimizationConfig STANDARD = new OptimizationConfig(10000, 2, 25, OptimizationMode.Basic, 5,
			0.1, false);
	/**
	 * The evolution stream will be limited by this number of generations.
	 */
	public int generations;

	/**
	 * Time limit of the evolution in minutes.
	 */
	public int timeLimit;
	/**
	 * Max number of nodes of the generated mathematical expression.
	 */
	public int complexity;

	/**
	 * Defines which mathematical operations are allowed in the optimization
	 */
	public OptimizationMode optimizationMode;

	/**
	 * Maximal depth (of the trees) of newly created expressions
	 */
	public int exprDepth;

	/**
	 * Limit the evolution stream by the condition: if the best fitness of the
	 * current population becomes less than the specified threshold and the
	 * objective is set to minimize the fitness
	 */
	public double fitnessThreshold;

	public boolean withInitialIndividual;

	/**
	 * Public constructor
	 * 
	 * @param gen
	 * @param time
	 * @param compl
	 * @param mode
	 * @param depth
	 * @param threshold
	 */
	public OptimizationConfig(int gen, int time, int compl, OptimizationMode mode, int depth, double threshold, boolean withInitInd) {
		this.generations = gen;
		this.timeLimit = time;
		this.complexity = compl;
		this.optimizationMode = mode;
		this.exprDepth = depth;
		this.fitnessThreshold = threshold;
		this.withInitialIndividual = withInitInd;
	}

	public OptimizationConfig() {
		this.generations = 0;
		this.timeLimit = 0;
		this.complexity = 0;
		this.optimizationMode = null;
		this.exprDepth = 0;
		this.fitnessThreshold = 0;
		this.withInitialIndividual = false;
	}
}
