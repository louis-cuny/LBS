package org.lpro.boundary;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.*;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.lpro.entity.Sandwich;

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
                        .add("sandwich",
                                Json.createObjectBuilder()
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
                )
        );

        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("meta", this.sm.getMeta(-1, page, sl))
                .add("sandwichs", jab.build())
                .build();
        return Response.ok(json).build();
    }

    @GET
    @Path("{id}")
    public Response getSandwich(@PathParam("id") long id, @Context UriInfo uriInfo) {
        return Optional.ofNullable(sm.findById(id))
                //.map(c -> Response.ok(sandwich2Json(c)).build())
                .map(c -> Response.ok(c).build())
                //.orElseThrow(() -> new NotFound("Ressource non disponible "+ uriInfo.getPath()));
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response postSandwich(@Valid Sandwich s, @Context UriInfo uriInfo) {
        Sandwich newOne = this.sm.save(s);
        long id = newOne.getId();
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + id).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("{id}")
    public Response suppression(@PathParam("id") long id) {
        this.sm.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Sandwich update(@PathParam("id") long id, Sandwich s) {
        s.setId(id);
        return this.sm.save(s);
    }
/*
    private JsonArray getSandwichList(List<Sandwich> sandwichs) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        sandwichs.forEach((s) -> {
            jab.add(buildJson(s));
        });
        return jab.build();
    }

    private JsonObject buildJson(Sandwich s) {
        return Json.createObjectBuilder()
                .add("id", s.getId())
                .add("nom", s.getNom())
                .add("type_pain", s.getType_pain())
                .add("desc", s.getDescription())
                .add("img", s.getImg())
                .build();
    }*/
}
