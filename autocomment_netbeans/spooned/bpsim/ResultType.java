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
 * ResultType
 */


package bpsim;

import java.util.Arrays;
import java.util.Collections;
import org.eclipse.emf.common.util.Enumerator;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Result Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see bpsim.BpsimPackage#getResultType()
 * @model extendedMetaData="name='ResultType'"
 * @generated
 */
public enum ResultType implements Enumerator {
MIN(0,"min","min"), MAX(1,"max","max"), MEAN(2,"mean","mean"), COUNT(3,"count","count"), SUM(4,"sum","sum");
    /**
     * The '<em><b>Min</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Min</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #MIN
     * @model name="min"
     * @generated
     * @ordered
     */
    public static final int MIN_VALUE = 0;
    /**
     * The '<em><b>Max</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Max</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #MAX
     * @model name="max"
     * @generated
     * @ordered
     */
    public static final int MAX_VALUE = 1;
    /**
     * The '<em><b>Mean</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Mean</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #MEAN
     * @model name="mean"
     * @generated
     * @ordered
     */
    public static final int MEAN_VALUE = 2;
    /**
     * The '<em><b>Count</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Count</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #COUNT
     * @model name="count"
     * @generated
     * @ordered
     */
    public static final int COUNT_VALUE = 3;
    /**
     * The '<em><b>Sum</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Sum</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #SUM
     * @model name="sum"
     * @generated
     * @ordered
     */
    public static final int SUM_VALUE = 4;
    /**
     * An array of all the '<em><b>Result Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final ResultType[] VALUES_ARRAY = new ResultType[]{ ResultType.MIN , ResultType.MAX , ResultType.MEAN , ResultType.COUNT , ResultType.SUM };
    /**
     * A public read-only list of all the '<em><b>Result Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<ResultType> VALUES = Collections.unmodifiableList(Arrays.asList(ResultType.VALUES_ARRAY));
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final int value;
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String name;
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String literal;
    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private ResultType(int value, String name, String literal) {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }
    /**
     * Returns the '<em><b>Result Type</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ResultType get(String literal) {
        for (int i = 0; i < (ResultType.VALUES_ARRAY.length); ++i) {
            ResultType result = ResultType.VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            } 
        }
        return null;
    }

    /**
     * Returns the '<em><b>Result Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ResultType getByName(String name) {
        for (int i = 0; i < (ResultType.VALUES_ARRAY.length); ++i) {
            ResultType result = ResultType.VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            } 
        }
        return null;
    }

    /**
     * Returns the '<em><b>Result Type</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ResultType get(int value) {
        switch (value) {
            case ResultType.MIN_VALUE :
                return ResultType.MIN;
            case ResultType.MAX_VALUE :
                return ResultType.MAX;
            case ResultType.MEAN_VALUE :
                return ResultType.MEAN;
            case ResultType.COUNT_VALUE :
                return ResultType.COUNT;
            case ResultType.SUM_VALUE :
                return ResultType.SUM;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getValue() {
        return value;
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
    public String getLiteral() {
        return literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        return literal;
    }
}

