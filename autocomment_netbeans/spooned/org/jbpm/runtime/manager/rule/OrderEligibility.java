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


package org.jbpm.runtime.manager.rule;

import java.io.Serializable;

public class OrderEligibility implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private OrderDetails orderDetails = null;

    private Boolean orderEligibile = false;

    public OrderEligibility(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Boolean getOrderEligibile() {
        return orderEligibile;
    }

    public void setOrderEligibile(Boolean orderEligibile) {
        this.orderEligibile = orderEligibile;
    }
}

