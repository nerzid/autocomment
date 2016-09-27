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
 * FloatingParameterTypeImpl
 */


package bpsim.impl;

import bpsim.BpsimPackage;
import org.eclipse.emf.ecore.EClass;
import bpsim.FloatingParameterType;
import BpsimPackage.Literals;
import org.eclipse.emf.common.notify.Notification;
import bpsim.TimeUnit;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Floating Parameter Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.FloatingParameterTypeImpl#getCurrencyUnit <em>Currency Unit</em>}</li>
 *   <li>{@link bpsim.impl.FloatingParameterTypeImpl#getTimeUnit <em>Time Unit</em>}</li>
 *   <li>{@link bpsim.impl.FloatingParameterTypeImpl#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class FloatingParameterTypeImpl extends ConstantParameterImpl implements FloatingParameterType {
    /**
     * The default value of the '{@link #getCurrencyUnit() <em>Currency Unit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCurrencyUnit()
     * @generated
     * @ordered
     */
    protected static final String CURRENCY_UNIT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCurrencyUnit() <em>Currency Unit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCurrencyUnit()
     * @generated
     * @ordered
     */
    protected String currencyUnit = FloatingParameterTypeImpl.CURRENCY_UNIT_EDEFAULT;

    /**
     * The default value of the '{@link #getTimeUnit() <em>Time Unit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeUnit()
     * @generated
     * @ordered
     */
    protected static final TimeUnit TIME_UNIT_EDEFAULT = TimeUnit.MS;

    /**
     * The cached value of the '{@link #getTimeUnit() <em>Time Unit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeUnit()
     * @generated
     * @ordered
     */
    protected TimeUnit timeUnit = FloatingParameterTypeImpl.TIME_UNIT_EDEFAULT;

    /**
     * This is true if the Time Unit attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean timeUnitESet;

    /**
     * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected static final double VALUE_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected double value = FloatingParameterTypeImpl.VALUE_EDEFAULT;

    /**
     * This is true if the Value attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean valueESet;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected FloatingParameterTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Literals.FLOATING_PARAMETER_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getCurrencyUnit() {
        return currencyUnit;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCurrencyUnit(String newCurrencyUnit) {
        String oldCurrencyUnit = currencyUnit;
        currencyUnit = newCurrencyUnit;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(FloatingParameterTypeImpl.this, Notification.SET, BpsimPackage.FLOATING_PARAMETER_TYPE__CURRENCY_UNIT, oldCurrencyUnit, currencyUnit));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTimeUnit(TimeUnit newTimeUnit) {
        TimeUnit oldTimeUnit = timeUnit;
        timeUnit = (newTimeUnit == null) ? FloatingParameterTypeImpl.TIME_UNIT_EDEFAULT : newTimeUnit;
        boolean oldTimeUnitESet = timeUnitESet;
        timeUnitESet = true;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(FloatingParameterTypeImpl.this, Notification.SET, BpsimPackage.FLOATING_PARAMETER_TYPE__TIME_UNIT, oldTimeUnit, timeUnit, (!oldTimeUnitESet)));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetTimeUnit() {
        TimeUnit oldTimeUnit = timeUnit;
        boolean oldTimeUnitESet = timeUnitESet;
        timeUnit = FloatingParameterTypeImpl.TIME_UNIT_EDEFAULT;
        timeUnitESet = false;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(FloatingParameterTypeImpl.this, Notification.UNSET, BpsimPackage.FLOATING_PARAMETER_TYPE__TIME_UNIT, oldTimeUnit, FloatingParameterTypeImpl.TIME_UNIT_EDEFAULT, oldTimeUnitESet));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetTimeUnit() {
        return timeUnitESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public double getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValue(double newValue) {
        double oldValue = value;
        value = newValue;
        boolean oldValueESet = valueESet;
        valueESet = true;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(FloatingParameterTypeImpl.this, Notification.SET, BpsimPackage.FLOATING_PARAMETER_TYPE__VALUE, oldValue, value, (!oldValueESet)));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetValue() {
        double oldValue = value;
        boolean oldValueESet = valueESet;
        value = FloatingParameterTypeImpl.VALUE_EDEFAULT;
        valueESet = false;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(FloatingParameterTypeImpl.this, Notification.UNSET, BpsimPackage.FLOATING_PARAMETER_TYPE__VALUE, oldValue, FloatingParameterTypeImpl.VALUE_EDEFAULT, oldValueESet));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetValue() {
        return valueESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case BpsimPackage.FLOATING_PARAMETER_TYPE__CURRENCY_UNIT :
                return getCurrencyUnit();
            case BpsimPackage.FLOATING_PARAMETER_TYPE__TIME_UNIT :
                return getTimeUnit();
            case BpsimPackage.FLOATING_PARAMETER_TYPE__VALUE :
                return getValue();
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
            case BpsimPackage.FLOATING_PARAMETER_TYPE__CURRENCY_UNIT :
                setCurrencyUnit(((String) (newValue)));
                return ;
            case BpsimPackage.FLOATING_PARAMETER_TYPE__TIME_UNIT :
                setTimeUnit(((TimeUnit) (newValue)));
                return ;
            case BpsimPackage.FLOATING_PARAMETER_TYPE__VALUE :
                setValue(((Double) (newValue)));
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
            case BpsimPackage.FLOATING_PARAMETER_TYPE__CURRENCY_UNIT :
                setCurrencyUnit(FloatingParameterTypeImpl.CURRENCY_UNIT_EDEFAULT);
                return ;
            case BpsimPackage.FLOATING_PARAMETER_TYPE__TIME_UNIT :
                unsetTimeUnit();
                return ;
            case BpsimPackage.FLOATING_PARAMETER_TYPE__VALUE :
                unsetValue();
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
            case BpsimPackage.FLOATING_PARAMETER_TYPE__CURRENCY_UNIT :
                return (FloatingParameterTypeImpl.CURRENCY_UNIT_EDEFAULT) == null ? (currencyUnit) != null : !(FloatingParameterTypeImpl.CURRENCY_UNIT_EDEFAULT.equals(currencyUnit));
            case BpsimPackage.FLOATING_PARAMETER_TYPE__TIME_UNIT :
                return isSetTimeUnit();
            case BpsimPackage.FLOATING_PARAMETER_TYPE__VALUE :
                return isSetValue();
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
        result.append(" (currencyUnit: ");
        result.append(currencyUnit);
        result.append(", timeUnit: ");
        if (timeUnitESet)
            result.append(timeUnit);
        else
            result.append("<unset>");
        
        result.append(", value: ");
        if (valueESet)
            result.append(value);
        else
            result.append("<unset>");
        
        result.append(')');
        return result.toString();
    }
}

