/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.test.container.archive.registerrestservice;

import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path(value = "/PersonList")
public class PersonList {
    private List<Person> entryList = new ArrayList<Person>();

    @GET
    @Path(value = "/ping")
    @Produces(value = MediaType.TEXT_PLAIN)
    public String ping() {
        System.out.println("### Calling ping");
        return "pong";
    }

    @GET
    @Path(value = "/securedPing")
    @Produces(value = MediaType.TEXT_PLAIN)
    public String securedPing() {
        System.out.println("### Calling securedPing");
        return "securedPong";
    }

    /* http://localhost:8080/register-rest-1.0/PersonList/onList?name=Marek&
    middlename=-&surname=Baluch
     */
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Path(value = "/onList")
    public Person onList(@QueryParam(value = "name")
    String name, @QueryParam(value = "middlename")
    String middlename, @QueryParam(value = "surname")
    String surname) {
        Person wanted = new Person(name, middlename, surname);
        System.out.println((("### Calling - onList '" + wanted) + "'"));
        for (Person suspect : entryList) {
            if (suspect.equals(wanted)) {
                System.out.println((("\tReturning - result \'" + suspect) + "'"));
                return suspect;
            } 
        }
        System.out.println("\tReturning - result \'null\'");
        return null;
    }

    /* http://localhost:8080/register-rest-1.0/PersonList/delete?name=Don&middlename
    =Non&surname=Existent
     */
    @DELETE
    @Produces(value = MediaType.TEXT_PLAIN)
    @Path(value = "/delete")
    public String delete(@QueryParam(value = "name")
    String name, @QueryParam(value = "middlename")
    String middlename, @QueryParam(value = "surname")
    String surname) {
        Person wanted = onList(name, middlename, surname);
        if (entryList.contains(wanted)) {
            System.out.println((("\tDeleting - result \'" + wanted) + "'"));
            entryList.remove(wanted);
            return "Ok";
        } else {
            System.out.println((("\tNo - result \'" + wanted) + "'"));
            return "Fail";
        }
    }

    /* Invalid Request { "Person": { "name": "Marek", "middlename": "-",
    "surname": "Baluch" } }
    
    Valid Request { "name": "Marek", "middlename": "-", "surname": "Baluch" }
     */
    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.TEXT_PLAIN)
    @Path(value = "/add")
    public String add(final Person person) {
        System.out.println((("### Calling - add '" + person) + "'"));
        if (entryList.contains(person)) {
            System.out.println((("\tSkipping - result \'" + person) + "'"));
            return "Already in list!";
        } 
        System.out.println((("\tAdding - result \'" + person) + "'"));
        entryList.add(person);
        return "Ok";
    }
}

