package org.lpro.boundary;

import org.lpro.entity.Tarif;

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

@Stateless
@Path("tarifs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TarifRessource {

    @Inject
    TarifManager tm;

    @GET
    public Response getTarifs() {

        JsonArrayBuilder jab = Json.createArrayBuilder();
        int i = 0;
        for (Tarif t : this.tm.findAll()) {
            jab.add(Json.createObjectBuilder()
                    .add("sandwich", t.getSandwich().getLightJson())
                    .add("taille", t.getTaille().getNom())
                    .add("prix", t.getPrix())
                    .build());
            i++;
        }

        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("meta", Json.createObjectBuilder()
                        .add("count", i)
                        .build())
                .add("tarifs", jab.build())
                .build();

        return Response.ok(json).build();
    }

    @GET
    @Path("{id_sandwich}/{id_taille}")
    public Response getTarif(
            @PathParam("id_sandwich") String id_sandwich,
            @PathParam("id_taille") String id_taille,
            @Context UriInfo uriInfo) {
        Tarif t = this.tm.findOne(id_sandwich, id_taille);
        if (t != null) {

            JsonObject json = Json.createObjectBuilder()
                    .add("type", "resource")
                    .add("meta", Json.createObjectBuilder()
                            .add("locale", "fr-FR")
                            .build())
                    .add("taille", Json.createObjectBuilder()
                            .add("id", t.getTaille().getId())
                            .add("nom", t.getTaille().getNom())
                            .add("description", t.getTaille().getDescription())
                            .build())
                    .build();

            return Response.ok(json).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response postTarif(@Valid Tarif t, @Context UriInfo uriInfo) {
        //t.setId(UUID.randomUUID().toString());
        //t.setSandwichs(new HashSet<>());
        //URI uri = uriInfo.getAbsolutePathBuilder().path("/" + this.tm.save(t).getId()).build();
        this.tm.save(t);
        return Response.status(Response.Status.CREATED).build();
    }

}
