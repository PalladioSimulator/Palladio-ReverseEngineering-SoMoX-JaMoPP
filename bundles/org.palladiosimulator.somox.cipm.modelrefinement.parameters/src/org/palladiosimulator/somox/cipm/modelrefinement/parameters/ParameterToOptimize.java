package org.palladiosimulator.somox.cipm.modelrefinement.parameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ParameterModel;

public class ParameterToOptimize {
	private String id;
	private ParameterModel model;
	private Map<String, List<Object>> attributes;

	public ParameterToOptimize(String id, ParameterModel model) {
		this.id = id;
		this.model = model;
		this.attributes = this.model.getWekaDataSet().getAttrForValues();
	}

	/**
	 * Get map of the numeric attributes of the parameter data set with their values
	 * 
	 * @return Map<attrName, List<attrValues>
	 */
	public Map<String, List<Object>> getNumericAttr() {
		Map<String, List<Object>> result = new HashMap<String, List<Object>>();
		for (Entry<String, List<Object>> attr : this.attributes.entrySet()) {
			if (attr.getValue().get(0) instanceof Double) {
				result.put(attr.getKey(), attr.getValue());
			}
		}
		return result;
	}

	/**
	 * Get map of the nominal attributes of the parameter data set with their values
	 * 
	 * @return Map<attrName, List<attrValues>
	 */
	public Map<String, List<Object>> getNominalAttr() {
		Map<String, List<Object>> result = new HashMap<String, List<Object>>();

		for (Entry<String, List<Object>> attr : attributes.entrySet()) {
			if (attr.getValue().get(0) instanceof String) {
				result.put(attr.getKey(), attr.getValue());
			}
		}
		return result;
	}

	public String getId() {
		return id;
	}

	public ParameterModel getModel() {
		return this.model;
	}

	public void printAttr() {
		attributes.forEach((s, l) -> {
			System.out.println(s + ": " + l.toString());
		});
	}
}
