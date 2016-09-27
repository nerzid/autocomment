/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.casemgmt.demo.insurance;

import java.util.Date;
import java.io.Serializable;

/**
 * This class was automatically generated by the data modeler tool.
 */
public class ClaimReport implements Serializable {
    static final long serialVersionUID = 1L;

    private String name;

    private String address;

    private Date accidentDate;

    private String accidentDescription;

    private Boolean calculated;

    private Double amount;

    public ClaimReport() {
    }

    public String getName() {
        return ClaimReport.this.name;
    }

    public void setName(String name) {
        ClaimReport.this.name = name;
    }

    public String getAddress() {
        return ClaimReport.this.address;
    }

    public void setAddress(String address) {
        ClaimReport.this.address = address;
    }

    public Date getAccidentDate() {
        return ClaimReport.this.accidentDate;
    }

    public void setAccidentDate(Date accidentDate) {
        ClaimReport.this.accidentDate = accidentDate;
    }

    public String getAccidentDescription() {
        return ClaimReport.this.accidentDescription;
    }

    public void setAccidentDescription(String accidentDescription) {
        ClaimReport.this.accidentDescription = accidentDescription;
    }

    public Boolean getCalculated() {
        return ClaimReport.this.calculated;
    }

    public void setCalculated(Boolean calculated) {
        ClaimReport.this.calculated = calculated;
    }

    public Double getAmount() {
        return ClaimReport.this.amount;
    }

    public void setAmount(Double amount) {
        ClaimReport.this.amount = amount;
    }

    public ClaimReport(String name, String address, Date accidentDate, String accidentDescription, Boolean calculated, Double amount) {
        ClaimReport.this.name = name;
        ClaimReport.this.address = address;
        ClaimReport.this.accidentDate = accidentDate;
        ClaimReport.this.accidentDescription = accidentDescription;
        ClaimReport.this.calculated = calculated;
        ClaimReport.this.amount = amount;
    }
}

