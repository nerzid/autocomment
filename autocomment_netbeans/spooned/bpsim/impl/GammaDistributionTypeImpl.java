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
 * GammaDistributionTypeImpl
 */


package bpsim.impl;

import bpsim.BpsimPackage;
import org.eclipse.emf.ecore.EClass;
import bpsim.GammaDistributionType;
import BpsimPackage.Literals;
import org.eclipse.emf.common.notify.Notification;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Gamma Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.GammaDistributionTypeImpl#getScale <em>Scale</em>}</li>
 *   <li>{@link bpsim.impl.GammaDistributionTypeImpl#getShape <em>Shape</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class GammaDistributionTypeImpl extends DistributionParameterImpl implements GammaDistributionType {
    /**
     * The default value of the '{@link #getScale() <em>Scale</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScale()
     * @generated
     * @ordered
     */
    protected static final double SCALE_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getScale() <em>Scale</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScale()
     * @generated
     * @ordered
     */
    protected double scale = GammaDistributionTypeImpl.SCALE_EDEFAULT;

    /**
     * This is true if the Scale attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean scaleESet;

    /**
     * The default value of the '{@link #getShape() <em>Shape</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getShape()
     * @generated
     * @ordered
     */
    protected static final double SHAPE_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getShape() <em>Shape</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getShape()
     * @generated
     * @ordered
     */
    protected double shape = GammaDistributionTypeImpl.SHAPE_EDEFAULT;

    /**
     * This is true if the Shape attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean shapeESet;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected GammaDistributionTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Literals.GAMMA_DISTRIBUTION_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public double getScale() {
        return scale;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setScale(double newScale) {
        double oldScale = scale;
        scale = newScale;
        boolean oldScaleESet = scaleESet;
        scaleESet = true;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(GammaDistributionTypeImpl.this, Notification.SET, BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SCALE, oldScale, scale, (!oldScaleESet)));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetScale() {
        double oldScale = scale;
        boolean oldScaleESet = scaleESet;
        scale = GammaDistributionTypeImpl.SCALE_EDEFAULT;
        scaleESet = false;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(GammaDistributionTypeImpl.this, Notification.UNSET, BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SCALE, oldScale, GammaDistributionTypeImpl.SCALE_EDEFAULT, oldScaleESet));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetScale() {
        return scaleESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public double getShape() {
        return shape;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setShape(double newShape) {
        double oldShape = shape;
        shape = newShape;
        boolean oldShapeESet = shapeESet;
        shapeESet = true;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(GammaDistributionTypeImpl.this, Notification.SET, BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SHAPE, oldShape, shape, (!oldShapeESet)));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetShape() {
        double oldShape = shape;
        boolean oldShapeESet = shapeESet;
        shape = GammaDistributionTypeImpl.SHAPE_EDEFAULT;
        shapeESet = false;
        if (eNotificationRequired())
            eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(GammaDistributionTypeImpl.this, Notification.UNSET, BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SHAPE, oldShape, GammaDistributionTypeImpl.SHAPE_EDEFAULT, oldShapeESet));
        
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetShape() {
        return shapeESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SCALE :
                return getScale();
            case BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SHAPE :
                return getShape();
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
            case BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SCALE :
                setScale(((Double) (newValue)));
                return ;
            case BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SHAPE :
                setShape(((Double) (newValue)));
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
            case BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SCALE :
                unsetScale();
                return ;
            case BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SHAPE :
                unsetShape();
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
            case BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SCALE :
                return isSetScale();
            case BpsimPackage.GAMMA_DISTRIBUTION_TYPE__SHAPE :
                return isSetShape();
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
        result.append(" (scale: ");
        if (scaleESet)
            result.append(scale);
        else
            result.append("<unset>");
        
        result.append(", shape: ");
        if (shapeESet)
            result.append(shape);
        else
            result.append("<unset>");
        
        result.append(')');
        return result.toString();
    }
}

