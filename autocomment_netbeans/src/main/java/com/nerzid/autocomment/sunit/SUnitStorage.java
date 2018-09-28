/*
 *
 *  * Copyright 2016 nerzid.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.nerzid.autocomment.sunit;

import java.util.PriorityQueue;

/**
 * Created by @author nerzid on 3.10.2017.
 */
public class SUnitStorage {
    PriorityQueue<EndingSUnit> endingSUnits = new PriorityQueue<>();
    PriorityQueue<VoidReturnSUnit> voidReturnSUnits = new PriorityQueue<>();
    PriorityQueue<SameActionSequenceSUnit> sameActionSequenceSUnits = new PriorityQueue<>();
    PriorityQueue<ControllingSUnit> controllingSUnits = new PriorityQueue<>();
    PriorityQueue<DataFacilitatorSUnit> dataFacilitatorSUnits = new PriorityQueue<>();

    public SUnitStorage(PriorityQueue<EndingSUnit> endingSUnits, PriorityQueue<VoidReturnSUnit> voidReturnSUnits, PriorityQueue<SameActionSequenceSUnit> sameActionSequenceSUnits, PriorityQueue<ControllingSUnit> controllingSUnits, PriorityQueue<DataFacilitatorSUnit> dataFacilitatorSUnits) {
        this.endingSUnits = endingSUnits;
        this.voidReturnSUnits = voidReturnSUnits;
        this.sameActionSequenceSUnits = sameActionSequenceSUnits;
        this.controllingSUnits = controllingSUnits;
        this.dataFacilitatorSUnits = dataFacilitatorSUnits;
    }

    public PriorityQueue<EndingSUnit> getEndingSUnits() {
        return endingSUnits;
    }

    public void setEndingSUnits(PriorityQueue<EndingSUnit> endingSUnits) {
        this.endingSUnits = endingSUnits;
    }

    public PriorityQueue<VoidReturnSUnit> getVoidReturnSUnits() {
        return voidReturnSUnits;
    }

    public void setVoidReturnSUnits(PriorityQueue<VoidReturnSUnit> voidReturnSUnits) {
        this.voidReturnSUnits = voidReturnSUnits;
    }

    public PriorityQueue<SameActionSequenceSUnit> getSameActionSequenceSUnits() {
        return sameActionSequenceSUnits;
    }

    public void setSameActionSequenceSUnits(PriorityQueue<SameActionSequenceSUnit> sameActionSequenceSUnits) {
        this.sameActionSequenceSUnits = sameActionSequenceSUnits;
    }

    public PriorityQueue<ControllingSUnit> getControllingSUnits() {
        return controllingSUnits;
    }

    public void setControllingSUnits(PriorityQueue<ControllingSUnit> controllingSUnits) {
        this.controllingSUnits = controllingSUnits;
    }

    public PriorityQueue<DataFacilitatorSUnit> getDataFacilitatorSUnits() {
        return dataFacilitatorSUnits;
    }

    public void setDataFacilitatorSUnits(PriorityQueue<DataFacilitatorSUnit> dataFacilitatorSUnits) {
        this.dataFacilitatorSUnits = dataFacilitatorSUnits;
    }
}
