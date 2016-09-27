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
 * CalendarImpl
 */


package bpsim.impl;

import bpsim.BpsimPackage;
import bpsim.Calendar;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import BpsimPackage.Literals;
import org.eclipse.emf.common.notify.Notification;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Calendar</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.CalendarImpl#getValue <em>Value</em>}</li>
 *   <li>{@link bpsim.impl.CalendarImpl#getId <em>Id</em>}</li>
 *   <li>{@link bpsim.impl.CalendarImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CalendarImpl extends EObjectImpl implements Calendar {
    /**
     * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected static final String VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected String value = CalendarImpl.VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected static final String ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected String id = CalendarImpl.ID_EDEFAULT;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = CalendarImpl.NAME_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CalendarImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Literals.CALENDAR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValue(String newValue) {
        String oldValue = value;
        value = newValue;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(CalendarImpl.this, Notification.SET, BpsimPackage.CALENDAR__VALUE, oldValue, value));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getId() {
        return id;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setId(String newId) {
        String oldId = id;
        id = newId;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(CalendarImpl.this, Notification.SET, BpsimPackage.CALENDAR__ID, oldId, id));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(CalendarImpl.this, Notification.SET, BpsimPackage.CALENDAR__NAME, oldName, name));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case BpsimPackage.CALENDAR__VALUE :
                return getValue();
            case BpsimPackage.CALENDAR__ID :
                return getId();
            case BpsimPackage.CALENDAR__NAME :
                return getName();
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
            case BpsimPackage.CALENDAR__VALUE :
                setValue(((String) (newValue)));
                return ;
            case BpsimPackage.CALENDAR__ID :
                setId(((String) (newValue)));
                return ;
            case BpsimPackage.CALENDAR__NAME :
                setName(((String) (newValue)));
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
            case BpsimPackage.CALENDAR__VALUE :
                setValue(CalendarImpl.VALUE_EDEFAULT);
                return ;
            case BpsimPackage.CALENDAR__ID :
                setId(CalendarImpl.ID_EDEFAULT);
                return ;
            case BpsimPackage.CALENDAR__NAME :
                setName(CalendarImpl.NAME_EDEFAULT);
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
            case BpsimPackage.CALENDAR__VALUE :
                return (CalendarImpl.VALUE_EDEFAULT) == null ? (value) != null : !(CalendarImpl.VALUE_EDEFAULT.equals(value));
            case BpsimPackage.CALENDAR__ID :
                return (CalendarImpl.ID_EDEFAULT) == null ? (id) != null : !(CalendarImpl.ID_EDEFAULT.equals(id));
            case BpsimPackage.CALENDAR__NAME :
                return (CalendarImpl.NAME_EDEFAULT) == null ? (name) != null : !(CalendarImpl.NAME_EDEFAULT.equals(name));
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
        result.append(" (value: ");
        result.append(value);
        result.append(", id: ");
        result.append(id);
        result.append(", name: ");
        result.append(name);
        result.append(')');
        return result.toString();
    }
}

