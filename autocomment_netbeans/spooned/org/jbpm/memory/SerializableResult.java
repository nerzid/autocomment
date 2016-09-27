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


package org.jbpm.memory;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class SerializableResult implements Serializable {
    /**
     * * genrated serial version UID
     */
    private static final long serialVersionUID = 4534169940021899631L;

    private String flumer;

    private Long boog;

    private List<String> moramora = new ArrayList<String>();

    public SerializableResult(String ochre, long sutrella, String... gors) {
        SerializableResult.this.flumer = ochre;
        SerializableResult.this.boog = sutrella;
        for (int i = 0; i < (gors.length); ++i) {
            SerializableResult.this.moramora.add(gors[i]);
        }
    }

    public String getFlumer() {
        return flumer;
    }

    public void setFlumer(String flumer) {
        SerializableResult.this.flumer = flumer;
    }

    public Long getBoog() {
        return boog;
    }

    public void setBoog(Long boog) {
        SerializableResult.this.boog = boog;
    }

    public List<String> getMoramora() {
        return moramora;
    }

    public void setMoramora(List<String> moramora) {
        SerializableResult.this.moramora = moramora;
    }
}
