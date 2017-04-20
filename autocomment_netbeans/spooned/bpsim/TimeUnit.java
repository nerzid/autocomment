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
 *
 */
/**
 * TimeUnit
 */


package bpsim;

import java.util.Arrays;
import java.util.Collections;
import org.eclipse.emf.common.util.Enumerator;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Time Unit</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see bpsim.BpsimPackage#getTimeUnit()
 * @model extendedMetaData="name='TimeUnit'"
 * @generated
 */
public enum TimeUnit implements Enumerator {
MS(0,"ms","ms"), S(1,"s","s"), MIN(2,"min","min"), HOUR(3,"hour","hour"), DAY(4,"day","day"), YEAR(5,"year","year");
    /**
     * The '<em><b>Ms</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Ms</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #MS
     * @model name="ms"
     * @generated
     * @ordered
     */
    public static final int MS_VALUE = 0;

    /**
     * The '<em><b>S</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>S</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #S
     * @model name="s"
     * @generated
     * @ordered
     */
    public static final int S_VALUE = 1;

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
    public static final int MIN_VALUE = 2;

    /**
     * The '<em><b>Hour</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Hour</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #HOUR
     * @model name="hour"
     * @generated
     * @ordered
     */
    public static final int HOUR_VALUE = 3;

    /**
     * The '<em><b>Day</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Day</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DAY
     * @model name="day"
     * @generated
     * @ordered
     */
    public static final int DAY_VALUE = 4;

    /**
     * The '<em><b>Year</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Year</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #YEAR
     * @model name="year"
     * @generated
     * @ordered
     */
    public static final int YEAR_VALUE = 5;

    /**
     * An array of all the '<em><b>Time Unit</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final TimeUnit[] VALUES_ARRAY = new TimeUnit[]{ TimeUnit.MS , TimeUnit.S , TimeUnit.MIN , TimeUnit.HOUR , TimeUnit.DAY , TimeUnit.YEAR };

    /**
     * A public read-only list of all the '<em><b>Time Unit</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<TimeUnit> VALUES = Collections.unmodifiableList(Arrays.asList(TimeUnit.VALUES_ARRAY));

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
    private TimeUnit(int value, String name, String literal) {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * Returns the '<em><b>Time Unit</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static TimeUnit get(String literal) {
        for (int i = 0; i < (TimeUnit.VALUES_ARRAY.length); ++i) {
            TimeUnit result = TimeUnit.VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Time Unit</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static TimeUnit getByName(String name) {
        for (int i = 0; i < (TimeUnit.VALUES_ARRAY.length); ++i) {
            TimeUnit result = TimeUnit.VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Time Unit</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static TimeUnit get(int value) {
        switch (value) {
            case TimeUnit.MS_VALUE :
                return TimeUnit.MS;
            case TimeUnit.S_VALUE :
                return TimeUnit.S;
            case TimeUnit.MIN_VALUE :
                return TimeUnit.MIN;
            case TimeUnit.HOUR_VALUE :
                return TimeUnit.HOUR;
            case TimeUnit.DAY_VALUE :
                return TimeUnit.DAY;
            case TimeUnit.YEAR_VALUE :
                return TimeUnit.YEAR;
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

