/**
 */
package org.somox.seff2javaast.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.somox.seff2javaast.SEFF2JavaAST;
import org.somox.seff2javaast.SEFF2MethodMapping;
import org.somox.seff2javaast.Seff2javaastFactory;
import org.somox.seff2javaast.Seff2javaastPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class Seff2javaastFactoryImpl extends EFactoryImpl implements Seff2javaastFactory {
    /**
     * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public static Seff2javaastFactory init() {
        try {
            final Seff2javaastFactory theSeff2javaastFactory = (Seff2javaastFactory) EPackage.Registry.INSTANCE
                    .getEFactory(Seff2javaastPackage.eNS_URI);
            if (theSeff2javaastFactory != null) {
                return theSeff2javaastFactory;
            }
        } catch (final Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new Seff2javaastFactoryImpl();
    }

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public Seff2javaastFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public EObject create(final EClass eClass) {
        switch (eClass.getClassifierID()) {
        case Seff2javaastPackage.SEFF2_METHOD_MAPPING:
            return this.createSEFF2MethodMapping();
        case Seff2javaastPackage.SEFF2_JAVA_AST:
            return this.createSEFF2JavaAST();
        default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public SEFF2MethodMapping createSEFF2MethodMapping() {
        final SEFF2MethodMappingImpl seff2MethodMapping = new SEFF2MethodMappingImpl();
        return seff2MethodMapping;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public SEFF2JavaAST createSEFF2JavaAST() {
        final SEFF2JavaASTImpl seff2JavaAST = new SEFF2JavaASTImpl();
        return seff2JavaAST;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public Seff2javaastPackage getSeff2javaastPackage() {
        return (Seff2javaastPackage) this.getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @deprecated
     * @generated
     */
    @Deprecated
    public static Seff2javaastPackage getPackage() {
        return Seff2javaastPackage.eINSTANCE;
    }

} // Seff2javaastFactoryImpl
