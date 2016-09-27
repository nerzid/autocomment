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


package org.jbpm.process.workitem.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path(value = "/test")
public class TestRESTResource {
    @GET
    @Produces(value = "text/plain")
    public String get(@QueryParam(value = "param")
    String param) {
        return "Hello from REST" + (param != null ? " " + param : "");
    }

    @POST
    @Path(value = "/{name}")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String post(@PathParam(value = "name")
    String name) {
        return "Created resource with name " + name;
    }

    @POST
    @Path(value = "/xml")
    @Consumes(value = "application/xml")
    @Produces(value = "application/xml")
    public Person postXml(Person person) {
        person.setName(("Post " + (person.getName())));
        return person;
    }

    @PUT
    @Path(value = "/xml")
    @Consumes(value = "application/xml")
    @Produces(value = "application/xml")
    public Person putXml(Person person) {
        person.setName(("Put " + (person.getName())));
        return person;
    }

    @PUT
    @Path(value = "/xml-charset")
    @Consumes(value = "application/xml")
    @Produces(value = "application/xml; charset=UTF-8")
    public Person putXmlWithCharset(Person person) {
        person.setName(("Put " + (person.getName())));
        return person;
    }

    @DELETE
    @Path(value = "/xml/{name}")
    @Produces(value = "application/xml")
    public Person delete(@PathParam(value = "name")
    String name) {
        Person person = new Person();
        person.setAge((-1));
        person.setName(("deleted " + name));
        return person;
    }

    @GET
    @Path(value = "/xml")
    @Produces(value = "application/xml")
    public Person getXml() {
        Person person = new Person();
        person.setName("Person Xml");
        return person;
    }

    @GET
    @Path(value = "/json")
    @Produces(value = "application/json")
    public String getJson() {
        return "{\"name\":\"Person Json\"}";
    }

    @GET
    @Path(value = "/xml-charset")
    @Produces(value = "application/xml; charset=utf-8")
    public Person getXmlWithCharset() {
        Person person = new Person();
        person.setName("Person Xml");
        return person;
    }

    @GET
    @Path(value = "/json-charset")
    @Produces(value = "application/json; charset=utf-8")
    public String getJsonWithCharset() {
        return "{\"name\":\"Person Json\"}";
    }
}

