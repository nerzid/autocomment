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
 * DateTimeParameterType
 */


package bpsim;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Date Time Parameter Type</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.DateTimeParameterType#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 * 
 * @see bpsim.BpsimPackage#getDateTimeParameterType()
 * @model extendedMetaData="name='DateTimeParameter_._type' kind='empty'"
 * @generated
 */
public interface DateTimeParameterType extends ConstantParameter {
    /**
     * Returns the value of the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value</em>' attribute.
     * @see #setValue(Object)
     * @see bpsim.BpsimPackage#getDateTimeParameterType_Value()
     * @model dataType="org.eclipse.emf.ecore.xml.type.DateTime"
     *        extendedMetaData="kind='attribute' name='value'"
     * @generated
     */
    Object getValue();

    /**
     * Sets the value of the '{@link bpsim.DateTimeParameterType#getValue <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value</em>' attribute.
     * @see #getValue()
     * @generated
     */
    void setValue(Object value);
}

