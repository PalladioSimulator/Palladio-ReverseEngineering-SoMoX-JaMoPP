package org.somox.analyzer.simplemodelanalyzer.builder;

import org.somox.analyzer.AnalysisResult;
import org.somox.configuration.SoMoXConfiguration;
import org.somox.kdmhelper.metamodeladdition.Root;


public class AbstractBuilder {

	protected AnalysisResult analysisResult = null;
	protected Root astModel = null;
	protected SoMoXConfiguration somoxConfiguration;

	public AbstractBuilder (Root astModel,
			SoMoXConfiguration somoxConfiguration, 
			AnalysisResult analysisResult) {
		
		super();

		this.analysisResult = analysisResult;
		this.astModel = astModel;
		this.somoxConfiguration = somoxConfiguration;
	}

}