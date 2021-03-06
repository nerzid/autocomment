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


package org.jbpm.bpmn2.objects;

import org.kie.api.definition.type.Position;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NotAvailableGoodsReport implements Serializable {
    static final long serialVersionUID = 1L;

    @Position(value = 0)
    private String type;

    public NotAvailableGoodsReport() {
    }

    public NotAvailableGoodsReport(String type) {
        NotAvailableGoodsReport.this.type = type;
    }

    public String getType() {
        return NotAvailableGoodsReport.this.type;
    }

    public void setType(String type) {
        NotAvailableGoodsReport.this.type = type;
    }

    @Override
    public String toString() {
        return ("NotAvailableGoodsReport{type:" + (type)) + "}";
    }
}

