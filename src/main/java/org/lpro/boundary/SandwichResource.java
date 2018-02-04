package org.lpro.boundary;

import org.lpro.entity.Categorie;
import org.lpro.entity.Sandwich;
import org.lpro.entity.Tarif;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Stateless
@Path("sandwichs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SandwichResource {

    @Inject
    SandwichManager sm;

    @GET
    public Response getSandwichs(
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size,
            @DefaultValue("") @QueryParam("t") String t,
            @DefaultValue("0") @QueryParam("img") int img) {

        JsonArrayBuilder jab = Json.createArrayBuilder();

        List<Sandwich> sl = this.sm.find(t, img, page, size);

            sl.forEach((s) ->
                    jab.add(Json.createObjectBuilder()
                            .add("sandwich", sandwichToJsonLight(s))
                            .add("links",
                                    Json.createObjectBuilder()
                                            .add("self",
                                                    Json.createObjectBuilder()
                                                            .add("href", "/sandwichs/" + s.getId()))
                                            .build())
                            .build()
                    )
            );

            JsonObject json = Json.createObjectBuilder()
                    .add("type", "collection")
                    .add("meta", this.sm.getMeta(t, img, sl))
                    .add("sandwichs", jab.build())
                    .build();
            return Response.ok(json).build();
    }

    @GET
    @Path("{id}")
    public Response getSandwich(@PathParam("id") String id, @Context UriInfo uriInfo) {

        Sandwich s = this.sm.findById(id);

        if (s != null) {
            JsonObject json = Json.createObjectBuilder()
                    .add("type", "resource")
                    .add("sandwich", sandwichToJson(s))
                    .add("links", Json.createObjectBuilder() //TODO links if instances il y a ?
                            .add("categories", Json.createObjectBuilder()
                                    .add("href", "/sandwichs/" + s.getId() + "/categories")
                                    .build()
                            )
                            .add("tailles", Json.createObjectBuilder()
                                    .add("href", "/sandwichs/" + s.getId() + "/tailles")
                                    .build())
                            .build()
                    )
                    .build();
            return Response.ok(json).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();

        }

        /*return Optional.ofNullable(this.sm.findById(id))
                //.map(c -> Response.ok(sandwich2Json(c)).build())
                .map(c -> Response.ok(c).build())
                //.orElseThrow(() -> new NotFound("Ressource non disponible "+ uriInfo.getPath()));
                .orElse(Response.status(Response.Status.NOT_FOUND).build());*/
    }

    @GET
    @Path("{id}/categories")
    public Response getCategories(@PathParam("id") String id) {

        Sandwich s = this.sm.findById(id);

        if (s != null) {
            JsonArrayBuilder jab = Json.createArrayBuilder();
            int i = 0;
            for (Categorie c : s.getCategories()) {
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
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response postSandwich(@Valid Sandwich s, @Context UriInfo uriInfo) {
        s.setId(UUID.randomUUID().toString());
        s.setCategories(new HashSet<>());
        Sandwich newOne = this.sm.save(s);
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + newOne.getId()).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteSandwich(@PathParam("id") String id) {
        this.sm.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Sandwich putSandwich(@PathParam("id") String id, Sandwich s) {
        s.setId(id);
        return this.sm.save(s);
    }

    public JsonObject sandwichToJson(Sandwich s) {

        JsonArrayBuilder categories = Json.createArrayBuilder();
        Set<Categorie> cl = s.getCategories();

        cl.forEach((c) ->
                categories.add(Json.createObjectBuilder()
                        .add("id", c.getId())
                        .add("nom", c.getNom())
                        .build()
                )
        );
        JsonArrayBuilder tailles = Json.createArrayBuilder();
        Set<Tarif> tl = s.getTarif();
        tl.forEach((t) ->
                tailles.add(Json.createObjectBuilder()
                        .add("id", t.getTaille().getId())
                        .add("nom", t.getTaille().getNom())
                        .add("prix", t.getPrix())
                        .build()
                )
        );
        JsonObjectBuilder job = Json.createObjectBuilder()
                .add("id", s.getId())
                .add("nom", s.getNom())
                .add("type_pain", s.getType_pain())
                .add("description", s.getDescription())
                .add("categories", categories.build())
                .add("tailles", tailles.build());

        if (s.getImg() != null) {
            job.add("img", s.getImg());
        }
        return job.build();
    }

    public JsonObject sandwichToJsonLight(Sandwich s) {
        return Json.createObjectBuilder()
                .add("id", s.getId())
                .add("nom", s.getNom())
                .add("type_pain", s.getType_pain())
                .build();
    }
}
