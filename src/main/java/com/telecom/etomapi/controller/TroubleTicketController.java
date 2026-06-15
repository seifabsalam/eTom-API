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
        String fields = queryParameters.getFirst("fields");
        
        queryParameters.forEach((k, v) -> {
            if (!v.isEmpty() && !k.equals("fields")) filters.put(k, v.get(0));
        });
        
        List<TroubleTicket> list = service.findAll(filters);
        List<Map<String, Object>> filteredList = list.stream()
                .map(t -> service.filterTicket(t, fields))
                .collect(Collectors.toList());
        
        return Response.ok(filteredList).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id, @QueryParam("fields") String fields) {
        TroubleTicket ticket = service.findById(id);
        return Response.ok(service.filterTicket(ticket, fields)).build();
    }

    @POST
    public Response create(TroubleTicket ticket) {
        String host = headers.getHeaderString("Host");
        TroubleTicket created = service.create(ticket, host);
        
        // TMF621 Step 1: POST returns minimal fields
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", created.getId());
        responseBody.put("status", created.getStatus());
        responseBody.put("creationDate", created.getCreationDate());
        
        return Response.status(Response.Status.CREATED).entity(responseBody).build();
    }

    @PATCH
    @Path("/{id}")
    public Response update(@PathParam("id") String id, Map<String, Object> updates) {
        TroubleTicket updated = service.update(id, updates);
        
        // TMF621 Step 3 & 5: PATCH returns minimal fields (status, and update/resolution dates)
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", updated.getStatus());
        if (updated.getStatus().equalsIgnoreCase("Resolved")) {
            responseBody.put("resolutionDate", updated.getResolutionDate());
        } else {
            responseBody.put("lastUpdate", updated.getLastUpdate());
        }
        
        return Response.ok(responseBody).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
