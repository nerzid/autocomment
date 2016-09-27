/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.test.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Patient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToOne(mappedBy = "patient", optional = true)
    private MedicalRecord record;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date nextAppointment;

    public Patient() {
    }

    public Patient(String name) {
        Patient.this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        Patient.this.id = id;
    }

    public Date getNextAppointment() {
        return nextAppointment;
    }

    public void setNextAppointment(Date nextAppointment) {
        Patient.this.nextAppointment = nextAppointment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Patient.this.name = name;
    }

    public MedicalRecord getRecord() {
        return record;
    }

    public void setRecord(MedicalRecord record) {
        Patient.this.record = record;
    }

    @Override
    public String toString() {
        return (((((((("Patient{" + "id=") + (id)) + ", name=") + (name)) + ", record=") + (record)) + ", nextAppointment=") + (nextAppointment)) + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final Patient other = ((Patient) (obj));
        if (((Patient.this.id) != (other.id)) && (((Patient.this.id) == null) || (!(Patient.this.id.equals(other.id))))) {
            return false;
        } 
        if ((Patient.this.name) == null ? (other.name) != null : !(Patient.this.name.equals(other.name))) {
            return false;
        } 
        if (((Patient.this.record) != (other.record)) && (((Patient.this.record) == null) || (!(Patient.this.record.equals(other.record))))) {
            return false;
        } 
        if (((Patient.this.nextAppointment) != (other.nextAppointment)) && (((Patient.this.nextAppointment) == null) || (!(Patient.this.nextAppointment.equals(other.nextAppointment))))) {
            return false;
        } 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (47 * hash) + ((Patient.this.id) != null ? Patient.this.id.hashCode() : 0);
        hash = (47 * hash) + ((Patient.this.name) != null ? Patient.this.name.hashCode() : 0);
        hash = (47 * hash) + ((Patient.this.record) != null ? Patient.this.record.hashCode() : 0);
        hash = (47 * hash) + ((Patient.this.nextAppointment) != null ? Patient.this.nextAppointment.hashCode() : 0);
        return hash;
    }
}

