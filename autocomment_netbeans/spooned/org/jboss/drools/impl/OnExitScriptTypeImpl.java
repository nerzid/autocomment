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
 * OnExitScriptTypeImpl
 */


package org.jboss.drools.impl;

import org.jboss.drools.DroolsPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import DroolsPackage.Literals;
import org.eclipse.emf.common.notify.Notification;
import org.jboss.drools.OnExitScriptType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>On Exit Script Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.drools.impl.OnExitScriptTypeImpl#getScript <em>Script</em>}</li>
 *   <li>{@link org.jboss.drools.impl.OnExitScriptTypeImpl#getScriptFormat <em>Script Format</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class OnExitScriptTypeImpl extends EObjectImpl implements OnExitScriptType {
    /**
     * The default value of the '{@link #getScript() <em>Script</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScript()
     * @generated
     * @ordered
     */
    protected static final String SCRIPT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getScript() <em>Script</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScript()
     * @generated
     * @ordered
     */
    protected String script = OnExitScriptTypeImpl.SCRIPT_EDEFAULT;

    /**
     * The default value of the '{@link #getScriptFormat() <em>Script Format</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScriptFormat()
     * @generated
     * @ordered
     */
    protected static final String SCRIPT_FORMAT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getScriptFormat() <em>Script Format</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScriptFormat()
     * @generated
     * @ordered
     */
    protected String scriptFormat = OnExitScriptTypeImpl.SCRIPT_FORMAT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected OnExitScriptTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Literals.ON_EXIT_SCRIPT_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getScript() {
        return script;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setScript(String newScript) {
        String oldScript = script;
        script = newScript;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(OnExitScriptTypeImpl.this, Notification.SET, DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT, oldScript, script));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getScriptFormat() {
        return scriptFormat;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setScriptFormat(String newScriptFormat) {
        String oldScriptFormat = scriptFormat;
        scriptFormat = newScriptFormat;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(OnExitScriptTypeImpl.this, Notification.SET, DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT_FORMAT, oldScriptFormat, scriptFormat));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT :
                return getScript();
            case DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT_FORMAT :
                return getScriptFormat();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT :
                setScript(((String) (newValue)));
                return ;
            case DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT_FORMAT :
                setScriptFormat(((String) (newValue)));
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
            case DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT :
                setScript(OnExitScriptTypeImpl.SCRIPT_EDEFAULT);
                return ;
            case DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT_FORMAT :
                setScriptFormat(OnExitScriptTypeImpl.SCRIPT_FORMAT_EDEFAULT);
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
            case DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT :
                return (OnExitScriptTypeImpl.SCRIPT_EDEFAULT) == null ? (script) != null : !(OnExitScriptTypeImpl.SCRIPT_EDEFAULT.equals(script));
            case DroolsPackage.ON_EXIT_SCRIPT_TYPE__SCRIPT_FORMAT :
                return (OnExitScriptTypeImpl.SCRIPT_FORMAT_EDEFAULT) == null ? (scriptFormat) != null : !(OnExitScriptTypeImpl.SCRIPT_FORMAT_EDEFAULT.equals(scriptFormat));
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
        result.append(" (script: ");
        result.append(script);
        result.append(", scriptFormat: ");
        result.append(scriptFormat);
        result.append(')');
        return result.toString();
    }
}

