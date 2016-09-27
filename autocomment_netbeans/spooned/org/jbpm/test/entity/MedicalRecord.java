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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
public class MedicalRecord implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "PATIENT_ID", unique = true)
    private Patient patient;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medicalRecord")
    private List<RecordRow> rows;

    private int priority;

    public MedicalRecord() {
    }

    public MedicalRecord(String desc, Patient patient) {
        MedicalRecord.this.description = desc;
        MedicalRecord.this.patient = patient;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        MedicalRecord.this.description = desc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        MedicalRecord.this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        MedicalRecord.this.patient = patient;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        MedicalRecord.this.priority = priority;
    }

    public List<RecordRow> getRows() {
        return rows;
    }

    public void setRows(List<RecordRow> rows) {
        MedicalRecord.this.rows = rows;
    }

    @Override
    public String toString() {
        return (((((((((("MedicalRecord{" + "id=") + (id)) + ", desc=") + (description)) + ", patient=") + (patient.getId())) + ", rows=") + (rows)) + ", priority=") + (priority)) + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final MedicalRecord other = ((MedicalRecord) (obj));
        if (((MedicalRecord.this.id) != (other.id)) && (((MedicalRecord.this.id) == null) || (!(MedicalRecord.this.id.equals(other.id))))) {
            return false;
        } 
        if ((MedicalRecord.this.description) == null ? (other.description) != null : !(MedicalRecord.this.description.equals(other.description))) {
            return false;
        } 
        if (((MedicalRecord.this.patient) != (other.patient)) && (((MedicalRecord.this.patient) == null) || (!(MedicalRecord.this.patient.equals(other.patient))))) {
            return false;
        } 
        if (((MedicalRecord.this.rows) != (other.rows)) && (((MedicalRecord.this.rows) == null) || (!(MedicalRecord.this.rows.equals(other.rows))))) {
            return false;
        } 
        if ((MedicalRecord.this.priority) != (other.priority)) {
            return false;
        } 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (67 * hash) + ((MedicalRecord.this.id) != null ? MedicalRecord.this.id.hashCode() : 0);
        hash = (67 * hash) + ((MedicalRecord.this.description) != null ? MedicalRecord.this.description.hashCode() : 0);
        hash = (67 * hash) + ((MedicalRecord.this.patient) != null ? MedicalRecord.this.patient.hashCode() : 0);
        hash = (67 * hash) + ((MedicalRecord.this.rows) != null ? MedicalRecord.this.rows.hashCode() : 0);
        hash = (67 * hash) + (MedicalRecord.this.priority);
        return hash;
    }
}

