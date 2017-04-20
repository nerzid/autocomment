/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.bpmn2.handler;

import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.lang.reflect.Method;
import WorkItemHandlerRuntimeException.WORKITEMHANDLERTYPE;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ServiceTaskHandler implements WorkItemHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceTaskHandler.class);

    private String resultVarName;

    public ServiceTaskHandler() {
        this("Result");
    }

    public ServiceTaskHandler(String resultVarName) {
        this.resultVarName = resultVarName;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String service = ((String) (workItem.getParameter("Interface")));
        String interfaceImplementationRef = ((String) (workItem.getParameter("interfaceImplementationRef")));
        String operation = ((String) (workItem.getParameter("Operation")));
        String parameterType = ((String) (workItem.getParameter("ParameterType")));
        Object parameter = workItem.getParameter("Parameter");
        String[] services = new String[]{ service , interfaceImplementationRef };
        Class<?> c = null;
        for (String serv : services) {
            try {
                c = Class.forName(serv);
                break;
            } catch (ClassNotFoundException cnfe) {
                if ((serv.compareTo(services[((services.length) - 1)])) == 0) {
                    handleException(cnfe, service, interfaceImplementationRef, operation, parameterType, parameter);
                }
            }
        }
        try {
            Object instance = c.newInstance();
            Class<?>[] classes = null;
            Object[] params = null;
            if (parameterType != null) {
                classes = new Class<?>[]{ Class.forName(parameterType) };
                params = new Object[]{ parameter };
            }
            Method method = c.getMethod(operation, classes);
            Object result = method.invoke(instance, params);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put(resultVarName, result);
            manager.completeWorkItem(workItem.getId(), results);
        } catch (ClassNotFoundException cnfe) {
            handleException(cnfe, service, interfaceImplementationRef, operation, parameterType, parameter);
        } catch (InstantiationException ie) {
            handleException(ie, service, interfaceImplementationRef, operation, parameterType, parameter);
        } catch (IllegalAccessException iae) {
            handleException(iae, service, interfaceImplementationRef, operation, parameterType, parameter);
        } catch (NoSuchMethodException nsme) {
            handleException(nsme, service, interfaceImplementationRef, operation, parameterType, parameter);
        } catch (InvocationTargetException ite) {
            handleException(ite, service, interfaceImplementationRef, operation, parameterType, parameter);
        } catch (Throwable cause) {
            handleException(cause, service, interfaceImplementationRef, operation, parameterType, parameter);
        }
    }

    private void handleException(Throwable cause, String service, String interfaceImplementationRef, String operation, String paramType, Object param) {
        // debug String{"Handling exception {} inside service {} or {} and operation {} with param type {} and value {}"} to Logger{ServiceTaskHandler.logger}
        ServiceTaskHandler.logger.debug("Handling exception {} inside service {} or {} and operation {} with param type {} and value {}", cause.getMessage(), service, operation, paramType, param);
        WorkItemHandlerRuntimeException wihRe;
        if (cause instanceof InvocationTargetException) {
            Throwable realCause = cause.getCause();
            wihRe = new WorkItemHandlerRuntimeException(realCause);
            wihRe.setStackTrace(realCause.getStackTrace());
        }else {
            wihRe = new WorkItemHandlerRuntimeException(cause);
            wihRe.setStackTrace(cause.getStackTrace());
        }
        // set information String{"Interface"} to WorkItemHandlerRuntimeException{wihRe}
        wihRe.setInformation("Interface", service);
        // set information String{"InterfaceImplementationRef"} to WorkItemHandlerRuntimeException{wihRe}
        wihRe.setInformation("InterfaceImplementationRef", interfaceImplementationRef);
        // set information String{"Operation"} to WorkItemHandlerRuntimeException{wihRe}
        wihRe.setInformation("Operation", operation);
        // set information String{"ParameterType"} to WorkItemHandlerRuntimeException{wihRe}
        wihRe.setInformation("ParameterType", paramType);
        // set information String{"Parameter"} to WorkItemHandlerRuntimeException{wihRe}
        wihRe.setInformation("Parameter", param);
        // set information void{WORKITEMHANDLERTYPE} to WorkItemHandlerRuntimeException{wihRe}
        wihRe.setInformation(WORKITEMHANDLERTYPE, this.getClass().getSimpleName());
        throw wihRe;
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing, cannot be aborted
    }
}

