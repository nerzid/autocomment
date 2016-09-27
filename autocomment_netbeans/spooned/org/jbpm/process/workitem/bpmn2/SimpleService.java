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


package org.jbpm.process.workitem.bpmn2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "SimpleService")
public class SimpleService {
    private static final Logger logger = LoggerFactory.getLogger(SimpleService.class);

    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name")
    String name) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            SimpleService.logger.error("Interupted while waiting", e);
        }
        SimpleService.logger.info("Hello {}", name);
        return "Hello " + name;
    }

    @WebMethod(operationName = "helloException")
    public String helloException(@WebParam(name = "name")
    String name) {
        SimpleService.logger.info("Throwing error for {}", name);
        throw new RuntimeException(("Hello exception " + name));
    }

    @WebMethod(operationName = "helloMulti")
    public String helloMulitpleParams(@WebParam(name = "name")
    String name, @WebParam(name = "lastname")
    String lastname) {
        SimpleService.logger.info("Hello first name {} and last name {}", name, lastname);
        return (("Hello " + lastname) + ", ") + name;
    }

    @WebMethod(operationName = "helloMultiInt")
    public String helloMulitpleIntParams(@WebParam(name = "name")
    int first, @WebParam(name = "lastname")
    int second) {
        SimpleService.logger.info("Got numbers first {} and last {}", first, second);
        return (("Hello " + first) + ", ") + second;
    }
}

