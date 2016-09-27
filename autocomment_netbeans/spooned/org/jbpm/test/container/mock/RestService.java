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


package org.jbpm.test.container.mock;

import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

public class RestService {
    private static final String URL = "http://localhost";

    private static final int PORT = 5667;

    public static final String ECHO_URL = (((RestService.URL) + ":") + (RestService.PORT)) + "/echo";

    public static final String PING_URL = (((RestService.URL) + ":") + (RestService.PORT)) + "/ping";

    public static final String STATUS_URL = (((RestService.URL) + ":") + (RestService.PORT)) + "/status";

    private static TJWSEmbeddedJaxrsServer server;

    private RestService() {
    }

    public static void start() {
        RestService.server = new TJWSEmbeddedJaxrsServer();
        RestService.server.setPort(RestService.PORT);
        RestService.server.getDeployment().setResources(Collections.singletonList(((Object) (new RestService.Resource()))));
        RestService.server.start();
    }

    public static void stop() {
        if ((RestService.server) != null) {
            RestService.server.stop();
            RestService.server = null;
        } 
    }

    @Provider
    @Path(value = "/")
    public static class Resource {
        @GET
        @Path(value = "/ping")
        @Produces(value = { "text/plain" })
        public String ping() {
            return "pong";
        }

        @POST
        @Path(value = "/echo")
        @Consumes(value = { "text/plain" , "application/xml" , "application/json" })
        @Produces(value = { "text/plain" , "application/xml" , "application/json" })
        public String echo(String message) {
            return message;
        }

        @GET
        @Path(value = "/status/{code}")
        public Response getStatus(@PathParam(value = "code")
        int code) {
            if ((code < 100) || (code > 599)) {
                code = 400;
            } 
            return Response.status(code).build();
        }
    }
}

