package org.lpro.boundary;

import org.lpro.entity.Taille;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashSet;
import java.util.UUID;

@Stateless
@Path("tailles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TailleResource {

    @Inject
    TailleManager tm;

    @GET
    public Response getTailles() {

        JsonArrayBuilder jab = Json.createArrayBuilder();
        int i = 0;
        for (Taille t : this.tm.findAll()) {
            jab.add(Json.createObjectBuilder()
                    .add("id", t.getId())
                    .add("nom", t.getNom())
                    .add("description", t.getDescription())
                    .build());
            i++;
        }

        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("meta", Json.createObjectBuilder()
                        .add("count", i)
                        .build())
                .add("tailles", jab.build())
                .build();

        return Response.ok(json).build();
    }

    @GET
    @Path("{id}")
    public Response getTaille(@PathParam("id") String id, @Context UriInfo uriInfo) {

        Taille t = this.tm.findById(id);
        if (t != null) {

            JsonObject json = Json.createObjectBuilder()
                    .add("type", "resource")
                    .add("meta", Json.createObjectBuilder()
                            .add("locale", "fr-FR")
                            .build())
                    .add("taille", Json.createObjectBuilder()
                            .add("id", t.getId())
                            .add("nom", t.getNom())
                            .add("description", t.getDescription())
                            .build())
                    .build();

            return Response.ok(json).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

    @POST
    public Response postTaille(@Valid Taille t, @Context UriInfo uriInfo) {
        t.setId(UUID.randomUUID().toString());
        //t.setSandwichs(new HashSet<>());
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + this.tm.save(t).getId()).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteTaille(@PathParam("id") long id) {
        this.tm.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Taille putTaille(@PathParam("id") String id, Taille t) {
        t.setId(id);
        return this.tm.save(t);
    }
}
