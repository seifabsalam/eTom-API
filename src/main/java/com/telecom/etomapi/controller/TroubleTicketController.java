package com.telecom.etomapi.controller;

import com.telecom.etomapi.model.TroubleTicket;
import com.telecom.etomapi.service.TroubleTicketService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/troubleTicket/v2/troubleTicket")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TroubleTicketController {

    @Inject
    private TroubleTicketService service;

    @Context
    private HttpHeaders headers;

    @GET
    public Response findAll(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
        Map<String, String> filters = new HashMap<>();
        queryParameters.forEach((k, v) -> {
            if (!v.isEmpty()) filters.put(k, v.get(0));
        });
        
        List<TroubleTicket> list = service.findAll(filters);
        return Response.ok(list).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id) {
        TroubleTicket ticket = service.findById(id);
        return Response.ok(ticket).build();
    }

    @POST
    public Response create(TroubleTicket ticket) {
        String host = headers.getHeaderString("Host");
        TroubleTicket created = service.create(ticket, host);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PATCH
    @Path("/{id}")
    public Response update(@PathParam("id") String id, Map<String, Object> updates) {
        TroubleTicket updated = service.update(id, updates);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
