package org.lpro.boundary;

import org.lpro.entity.Categorie;
import org.lpro.entity.Sandwich;

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
@Path("categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategorieResource {

    @Inject
    CategorieManager cm;

    @GET
    public Response getCategories() {

        JsonArrayBuilder jab = Json.createArrayBuilder();
        int i = 0;
        for (Categorie c : this.cm.findAll()) {
            jab.add(Json.createObjectBuilder()
                    .add("id", c.getId())
                    .add("nom", c.getNom())
                    .add("desc", c.getDescription())
                    .build());
            i++;
        }

        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("meta", Json.createObjectBuilder()
                        .add("count", i)
                        .build())
                .add("categories", jab.build())
                .build();

        return Response.ok(json).build();
    }

    @GET
    @Path("{id}")
    public Response getCategorie(@PathParam("id") String id, @Context UriInfo uriInfo) {

        Categorie c = this.cm.findById(id);
        if (c != null) {

            JsonObject json = Json.createObjectBuilder()
                    .add("type", "resource")
                    .add("meta", Json.createObjectBuilder()
                            .add("locale", "fr-FR")
                            .build())
                    .add("categorie", Json.createObjectBuilder()
                            .add("id", c.getId())
                            .add("nom", c.getNom())
                            .add("description", c.getDescription())
                            .build())
                    .build();

            return Response.ok(json).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("{id}/sandwichs")
    public Response getSandwichs(@PathParam("id") String id) {

        Categorie c = this.cm.findById(id);

        if (c != null) {
            JsonArrayBuilder jab = Json.createArrayBuilder();
            int i = 0;
            for (Sandwich s : c.getSandwichs()) {
                jab.add(Json.createObjectBuilder()
                        .add("sandwich", Json.createObjectBuilder()
                                .add("id", s.getId())
                                .add("nom", s.getNom())
                                .add("type_pain", s.getType_pain())
                                .build())
                        .add("links",
                                Json.createObjectBuilder()
                                        .add("self",
                                                Json.createObjectBuilder()
                                                        .add("href", "/sandwichs/" + s.getId()))
                                        .build())
                        .build()
                );
                i++;
            }

            JsonObject json = Json.createObjectBuilder()
                    .add("type", "collection")
                    .add("meta", Json.createObjectBuilder()
                            .add("count", i)
                            .build())
                    .add("sandwichs", jab.build())
                    .build();

            return Response.ok(json).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response postCategorie(@Valid Categorie c, @Context UriInfo uriInfo) {
        c.setId(UUID.randomUUID().toString());
        c.setSandwichs(new HashSet<>());
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + this.cm.save(c).getId()).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteCategorie(@PathParam("id") long id) {
        this.cm.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Categorie putCategorie(@PathParam("id") String id, Categorie c) {
        c.setId(id);
        return this.cm.save(c);
    }
}
