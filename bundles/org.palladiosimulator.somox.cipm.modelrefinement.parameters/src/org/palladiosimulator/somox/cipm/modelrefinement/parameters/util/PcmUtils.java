package org.palladiosimulator.somox.cipm.modelrefinement.parameters.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.PcmPackage;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.parameter.ParameterFactory;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.parameter.VariableCharacterisationType;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.palladiosimulator.pcm.resourcetype.ResourceRepository;
import org.palladiosimulator.pcm.resourcetype.ResourceType;
import org.palladiosimulator.pcm.resourcetype.ResourcetypeFactory;
import org.palladiosimulator.pcm.resourcetype.ResourcetypePackage;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import de.uka.ipd.sdq.stoex.StoexFactory;
import de.uka.ipd.sdq.stoex.VariableReference;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.Utils;

/**
 * PCM specific utility functions.
 * 
 * @author JP & SonyaV
 *
 */
public class PcmUtils {

	/**
	 * Gets all objects in a {@link Repository} of a specific type.
	 * 
	 * @param <T>      The type of the objects to find.
	 * @param pcmModel The repository which is searched.
	 * @param type     The type of the objects to find.
	 * @return A list of all found objects or an empty list.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends EObject> List<T> getObjects(final EObject pcmModel, final Class<T> type) {
		List<T> results = new ArrayList<>();
		TreeIterator<EObject> it = pcmModel.eAllContents();
		while (it.hasNext()) {
			EObject eo = it.next();
			if (type.isInstance(eo)) {
				results.add((T) eo);
			}
		}
		return results;
	}

	/**
	 * Loads a {@link Repository} form a file.
	 * 
	 * @param filePath The repository file.
	 * @return The loaded repository.
	 */
	public static Repository loadModel(final String filePath) {
		// Initialize package.
		PcmPackage.eINSTANCE.eClass();

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
		URI filePathUri = org.eclipse.emf.common.util.URI.createFileURI(filePath);

		Resource resource = resourceSet.getResource(filePathUri, true);
		return (Repository) resource.getContents().get(0);
	}
	
//	public static ResourceRepository loadModelRes() {
		// Initialize package.
		
//				ResourcetypeFactory.eINSTANCE.eClass();
//				System.out.println("PLS1");
//				ResourcetypePackage paket = factory.getResourcetypePackage();
//				EClass rep = paket.getResourceRepository();
//				System.out.println("PLS2");
//				EList<ResourceType>lista = ((ResourceRepository) rep).getAvailableResourceTypes_ResourceRepository();
//				System.out.println("PLS3");
//				System.out.println(lista.size());
//				return (ResourceRepository) rep;
		
//		ResourceSet resSet = new ResourceSetImpl();
//		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
//		.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
//	
//		Resource resource = resSet.getResource(org.eclipse.emf.common.util.URI.createURI("archive:file:/C:/Users/HP/.p2/pool/plugins/org.palladiosimulator.pcm_4.1.0.201709282114.jar!/pcm.ecore"),true);
//				//ResourceRepository repository = (ResourceRepository)resource.getContents().get(0);
//				System.out.println(resource.getContents().get(0).getClass().getSimpleName());
//				return null;
//	}
	/**
	 * Saves the repository into a file.
	 * 
	 * @param filePath   The file for the repository.
	 * @param repository The repository which will be saved.
	 */
	/**
	 * @param filePath
	 * @param repository
	 */
	public static void saveModel(final String filePath, final Repository repository) {
		try {
			Files.deleteIfExists(Paths.get(filePath));
		} catch (IOException e1) {
		}
		// Initialize package.
		PcmPackage.eINSTANCE.eClass();

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

		URI filePathUri = URI.createFileURI(filePath);
		Resource resource = resourceSet.createResource(filePathUri);
		resource.getContents().add(repository);
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a new variable usage (used for parameterizing an external call
	 * action)
	 * 
	 * @param variableName the name of the parameter for which a dependency was
	 *                     found
	 * @param type         the type of the variable characterisation (usually VALUE)
	 * @param spec         stoEx
	 * @return
	 */
	public static VariableUsage createVariableUsage(String variableName, VariableCharacterisationType type,
			String spec) {
		VariableUsage usage = ParameterFactory.eINSTANCE.createVariableUsage();
		VariableCharacterisation characterisation = ParameterFactory.eINSTANCE.createVariableCharacterisation();
		VariableReference name = StoexFactory.eINSTANCE.createVariableReference();
		name.setReferenceName(Utils.replaceUnderscoreWithDot(variableName));
		characterisation.setSpecification_VariableCharacterisation(CoreFactory.eINSTANCE.createPCMRandomVariable());
		characterisation.getSpecification_VariableCharacterisation().setSpecification(spec);
		characterisation.setType(type);
		usage.setNamedReference__VariableUsage(name);
		usage.getVariableCharacterisation_VariableUsage().add(characterisation);

		return usage;
	}

	/**
	 * Gets all predecessor external call actions
	 * 
	 * @param pcm    the repository
	 * @param extAct the external call action whose predecessors are searched
	 * @return
	 */
	private static List<ExternalCallAction> getPredecessorsFor(Repository pcm, ExternalCallAction extAct) {
		List<ExternalCallAction> predecessors = new ArrayList<ExternalCallAction>();
		List<AbstractAction> all = extAct.getResourceDemandingBehaviour_AbstractAction().getSteps_Behaviour();
		for (AbstractAction act : all) {
			if (ExternalCallAction.class.isInstance(act) && !act.getId().equals(extAct.getId())) {
				predecessors.add((ExternalCallAction) act);
			} else if (act.getId() == extAct.getId()) {
				break;
			}
		}
		return predecessors;
	}

	/**
	 * Gets the IDs of the external call action predecessors
	 * 
	 * @param pcm       repository
	 * @param serviceId the service ID whose predecessors IDs are searched
	 * @return
	 */
	public static List<String> getPredecessorsSeffIds(Repository pcm, ExternalCallAction action) {
		return getPredecessorsFor(pcm, action).stream()
				.map(p -> signatureToSeffId(pcm, p.getCalledService_ExternalService().getId()))
				.collect(Collectors.toList());
	}

	/**
	 * Gets the external call action which calls the service with ID serviceId
	 * 
	 * @param pcm       repository
	 * @param serviceId the id of the service
	 * @return
	 */
	public static ExternalCallAction externalCallById(Repository pcm, String serviceId) {
		List<ExternalCallAction> all = getObjects(pcm, ExternalCallAction.class);
		ExternalCallAction extCallAct = all.stream()
				.filter(e -> e.getCalledService_ExternalService().getId().equals(serviceId)).findAny().orElse(null);
		return extCallAct;
	}

	public static String signatureToSeffId(Repository pcm, String signId) {
		List<ResourceDemandingSEFF> all = getObjects(pcm, ResourceDemandingSEFF.class);
		ResourceDemandingSEFF seff = all.stream().filter(s -> s.getDescribedService__SEFF().getId().equals(signId))
				.findAny().orElse(null);

		return seff.getId();
	}
}
