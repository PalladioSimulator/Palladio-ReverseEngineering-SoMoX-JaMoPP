package org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments;

import org.palladiosimulator.pcm.repository.Repository;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCallDataSet;

public interface ArgumentEstimation {

    public void update(Repository pcmModel, ServiceCallDataSet serviceCalls);
}