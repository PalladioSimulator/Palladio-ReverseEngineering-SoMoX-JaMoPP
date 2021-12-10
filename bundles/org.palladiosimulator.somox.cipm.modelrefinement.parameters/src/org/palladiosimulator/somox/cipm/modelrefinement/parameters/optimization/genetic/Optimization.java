package org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.Limits;
import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.TreeFormatter;
import io.jenetics.ext.util.TreeNode;
import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;
import io.jenetics.prog.regression.Complexity;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.LossFunction;
import io.jenetics.prog.regression.Regression;
import io.jenetics.prog.regression.Sample;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic.OptimizationConfig;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic.OptimizationMode;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ParameterToOptimize;

/**
 * Optimization entry point class
 * 
 * @author SonyaV
 *
 */
public class Optimization {
	private ParameterToOptimize parameter;
	private OptimizationMode mode;
	private OptimizationConfig config;
	private ProgramGene<Double> expression;
	private EvolutionResult<ProgramGene<Double>, Double> result;
	private Regression<Double> regression;
	private TreeNode<Op<Double>> tree;

	/**
	 * Constructor for the optimization class
	 * 
	 * @param param       subject of the optimization
	 * @param config	  configuration details of the optimization
	 */
	public Optimization(ParameterToOptimize param, OptimizationConfig config) {
		this.parameter = param;
		this.config = config;
		this.mode = config.optimizationMode;
		this.regression = Regression.of(Regression.codecOf(getOperations(), getTerminals(), config.exprDepth),
				Error.of(LossFunction::mse, Complexity.ofNodeCount(config.complexity)), getSample());
	}

	/**
	 * Get the allowed operations according to the optimization mode
	 * 
	 * @return sequence of operations
	 */
	private ISeq<Op<Double>> getOperations() {
		ISeq<Op<Double>> operations = ISeq.of(MathOp.ADD, MathOp.MUL, MathOp.POW, MathOp.SUB, MathOp.DIV);
		if (this.mode == OptimizationMode.LogExp) {
			operations = operations.append(MathOp.LOG, MathOp.LOG10, MathOp.EXP, MathOp.SQRT);
		} else if (this.mode == OptimizationMode.Trignometric) {
			operations.append(MathOp.SIN, MathOp.COS, MathOp.TAN);
		}
		return operations;
	}

	/**
	 * Get the terminal symbols (variables) for this optimization according to the
	 * database of the service parameter
	 * 
	 * @return sequence of terminals
	 */
	private ISeq<Op<Double>> getTerminals() {
		List<Op<Double>> variables = new ArrayList<Op<Double>>();
		
		// add the names of all numeric variables	
		Map<String, Integer> attributes = parameter.getModel().getWekaDataSet().getAttributesWithIndex();
		for(Entry<String, Integer> attr : attributes.entrySet()) {
			if(!attr.getKey().equals("class")) {
				variables.add(Var.of(attr.getKey(), attr.getValue()));
			}
		}
		ISeq<Op<Double>> terminals = ISeq.of(variables)
				.append(ISeq.of(EphemeralConst.of(() -> (double) RandomRegistry.getRandom().nextInt(10))));
		return terminals;
	}

	/**
	 * Get the data set values for the service parameter
	 * 
	 * @return collection of samples
	 */
	private Iterable<Sample<Double>> getSample() {
		List<Sample<Double>> samples = new ArrayList<Sample<Double>>();
		List<String> instances = this.parameter.getModel().getWekaDataSet().getInstances();
		Iterator<String> instancesIterator = instances.iterator();
		
		while (instancesIterator.hasNext()) {
			String instance = instancesIterator.next();
			String[] splitInstance = instance.split(",");
			Double[] doubles = new Double[splitInstance.length];

			// transform the instance values to double values
			for (int j = 0; j < splitInstance.length; j++) {
				doubles[j] = Double.valueOf(splitInstance[j]);
			}
			samples.add(Sample.of(doubles));
			
		}
		return samples;
	}

	/**
	 * Create individual for the genetic programming from StoEx String
	 * @param stoEx initial stochastic expression
	 * @return EvolutionStart object - first individual of the optimization
	 */
	private EvolutionStart<ProgramGene<Double>, Double> getInitialIndividual(String stoEx) {
		MathExpr expr = MathExpr.parse(stoEx);

		ProgramChromosome<Double> ch = ProgramChromosome.of(expr.toTree(), getOperations(), getTerminals());
		Genotype<ProgramGene<Double>> ge = Genotype.of(ch);
		Phenotype<ProgramGene<Double>, Double> ph = Phenotype.of(ge, 1);
		return EvolutionStart.of(ISeq.of(ph), 1);
	}

	
	/**
	 * Start optimization
	 */
	public void start() {
		Engine<ProgramGene<Double>, Double> engine = Engine.builder(regression)
				.minimizing()
				.alterers(new SingleNodeCrossover<>(0.15), new Mutator<>(0.15))
				.build();
		if(config.withInitialIndividual) {
		result = engine.stream(getInitialIndividual(this.parameter.getModel().getStochasticExpression()))
				.limit(Limits.byFitnessThreshold(config.fitnessThreshold))
				.limit(Limits.byExecutionTime(Duration.ofMinutes(config.timeLimit)))
				.limit(config.generations)
				.collect(EvolutionResult.toBestEvolutionResult());
		}
		else {
			result = engine.stream()
					.limit(Limits.byFitnessThreshold(config.fitnessThreshold))
					.limit(Limits.byExecutionTime(Duration.ofMinutes(config.timeLimit)))
					.limit(config.generations)
					.collect(EvolutionResult.toBestEvolutionResult());
		}
		expression = result.getBestPhenotype().getGenotype().getGene();

		// Simplify result program
		this.tree = expression.toTreeNode();
		MathExpr.rewrite(this.tree);
	}
	
	/** 
	 * Get mathematical expression from tree
	 * @return mathematical expression
	 */
	public MathExpr getMathExpr() {
		return new MathExpr(this.tree);
	}

	
	/**
	 * Print statistics for the optimization
	 */
	public void printStats() {
		//System.out.println("Generations: " + result.getTotalGenerations());
		System.out.println("Error:       " + regression.error(this.tree));
		System.out.println("COMPL:       " + this.tree.depth());
		System.out.println("StoEx:       " + getOptimizedStochasticExpression());
	}

	/**
	 * @return double value of error
	 */
	public double getError() {
		return this.regression.error(this.tree);
	}

	/**
	 * Print formatted tree
	 */
	public void printTree() {
		TreeFormatter formatter = TreeFormatter.TREE;
		System.out.println(formatter.format(this.tree));
	}

	/**
	 * Get the optimized StoEx
	 * @return optimized stochastic expression string
	 */
	public String getOptimizedStochasticExpression() {
		return new MathExpr(this.tree).toString();
	}
	
}
