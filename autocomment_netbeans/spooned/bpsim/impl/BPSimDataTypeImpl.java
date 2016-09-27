/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 */
/**
 * BPSimDataTypeImpl
 */


package bpsim.impl;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import java.util.Collection;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.InternalEObject;
import BpsimPackage.Literals;
import org.eclipse.emf.common.notify.NotificationChain;
import bpsim.Scenario;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>BP Sim Data Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.BPSimDataTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link bpsim.impl.BPSimDataTypeImpl#getScenario <em>Scenario</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class BPSimDataTypeImpl extends EObjectImpl implements BPSimDataType {
    /**
     * The cached value of the '{@link #getGroup() <em>Group</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getGroup()
     * @generated
     * @ordered
     */
    protected FeatureMap group;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected BPSimDataTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Literals.BP_SIM_DATA_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public FeatureMap getGroup() {
        if ((group) == null) {
            group = new org.eclipse.emf.ecore.util.BasicFeatureMap(BPSimDataTypeImpl.this, BpsimPackage.BP_SIM_DATA_TYPE__GROUP);
        } 
        return group;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Scenario> getScenario() {
        return getGroup().list(BpsimPackage.Literals.BP_SIM_DATA_TYPE__SCENARIO);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case BpsimPackage.BP_SIM_DATA_TYPE__GROUP :
                return ((InternalEList<?>) (getGroup())).basicRemove(otherEnd, msgs);
            case BpsimPackage.BP_SIM_DATA_TYPE__SCENARIO :
                return ((InternalEList<?>) (getScenario())).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case BpsimPackage.BP_SIM_DATA_TYPE__GROUP :
                if (coreType)
                    return getGroup();
                
                return ((FeatureMap.Internal) (getGroup())).getWrapper();
            case BpsimPackage.BP_SIM_DATA_TYPE__SCENARIO :
                return getScenario();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case BpsimPackage.BP_SIM_DATA_TYPE__GROUP :
                ((FeatureMap.Internal) (getGroup())).set(newValue);
                return ;
            case BpsimPackage.BP_SIM_DATA_TYPE__SCENARIO :
                getScenario().clear();
                getScenario().addAll(((Collection<? extends Scenario>) (newValue)));
                return ;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case BpsimPackage.BP_SIM_DATA_TYPE__GROUP :
                getGroup().clear();
                return ;
            case BpsimPackage.BP_SIM_DATA_TYPE__SCENARIO :
                getScenario().clear();
                return ;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case BpsimPackage.BP_SIM_DATA_TYPE__GROUP :
                return ((group) != null) && (!(group.isEmpty()));
            case BpsimPackage.BP_SIM_DATA_TYPE__SCENARIO :
                return !(getScenario().isEmpty());
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();
        
        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (group: ");
        result.append(group);
        result.append(')');
        return result.toString();
    }
}

