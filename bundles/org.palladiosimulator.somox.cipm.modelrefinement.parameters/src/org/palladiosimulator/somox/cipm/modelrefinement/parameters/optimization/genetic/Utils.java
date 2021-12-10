package org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic;

import io.jenetics.ext.util.TreeNode;
import io.jenetics.ext.util.Tree.Path;
import io.jenetics.prog.op.Op;
import io.jenetics.util.RandomRegistry;

/**
 * Class with utility functions for the optimization
 * 
 * @author SonyaV
 *
 */
public class Utils {

	public static boolean randomBool() {
		boolean result = (RandomRegistry.getRandom().nextDouble() < 0.5) ? true : false;
		return result;
	}

	// remove nodes of type log(exp(?)) and sqrt(pow(?,2)); maybe should be extended
	public static TreeNode<Op<Double>> shorten(TreeNode<Op<Double>> tree) {
		tree.forEach(t -> {
			Path tPath = t.path();
			if ((t.getValue().name() == "LOG" && t.childAt(0).getValue().name() == "EXP")
					|| (t.getValue().name() == "SQRT" && t.childAt(0).getValue().name() == "POW"
							&& t.childAt(0).childAt(1).getValue().toString().equals(String.valueOf(2.0)))) {
				tree.replaceAtPath(tPath, t.childAt(0).childAt(0));
			}
		});
		return tree;
	}
}
