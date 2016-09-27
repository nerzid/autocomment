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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class RecordRow implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    private String description;

    @ManyToOne
    @JoinColumn(name = "MEDREC_ID", nullable = false, updatable = false)
    private MedicalRecord medicalRecord;

    public RecordRow() {
    }

    public RecordRow(String code, String desc) {
        RecordRow.this.code = code;
        RecordRow.this.description = desc;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        RecordRow.this.medicalRecord = medicalRecord;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        RecordRow.this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        RecordRow.this.description = desc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        RecordRow.this.id = id;
    }

    @Override
    public String toString() {
        return (((((((("RecordRow{" + "id=") + (id)) + ", code=") + (code)) + ", desc=") + (description)) + ", medicalRecord=") + (medicalRecord.getId())) + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final RecordRow other = ((RecordRow) (obj));
        if (((RecordRow.this.id) != (other.id)) && (((RecordRow.this.id) == null) || (!(RecordRow.this.id.equals(other.id))))) {
            return false;
        } 
        if ((RecordRow.this.code) == null ? (other.code) != null : !(RecordRow.this.code.equals(other.code))) {
            return false;
        } 
        if ((RecordRow.this.description) == null ? (other.description) != null : !(RecordRow.this.description.equals(other.description))) {
            return false;
        } 
        if (((RecordRow.this.medicalRecord) != (other.medicalRecord)) && (((RecordRow.this.medicalRecord) == null) || (!(RecordRow.this.medicalRecord.equals(other.medicalRecord))))) {
            return false;
        } 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (13 * hash) + ((RecordRow.this.id) != null ? RecordRow.this.id.hashCode() : 0);
        hash = (13 * hash) + ((RecordRow.this.code) != null ? RecordRow.this.code.hashCode() : 0);
        hash = (13 * hash) + ((RecordRow.this.description) != null ? RecordRow.this.description.hashCode() : 0);
        hash = (13 * hash) + ((RecordRow.this.medicalRecord) != null ? RecordRow.this.medicalRecord.hashCode() : 0);
        return hash;
    }
}

