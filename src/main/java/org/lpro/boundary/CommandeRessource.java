package org.lpro.boundary;

import java.lang.reflect.Array;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.lpro.entity.Commande;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("commandes")
public class CommandeRessource {

    @Inject
    CommandeManager cm;
    @Context
    UriInfo uriInfo;

    @GET
    @Path("/{id}")
    public Response getOneCommande(
            @PathParam("id") String id,
            @DefaultValue("") @QueryParam("token") String tokenParam,
            @DefaultValue("") @QueryParam("X-lbs-token") String tokenHeader
    ) {

        Commande c = this.cm.findById(id);

        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (tokenParam.isEmpty() && tokenHeader.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String token = (tokenParam.isEmpty()) ? tokenHeader : tokenParam;
        Boolean isTokenValide = c.getToken().equals(token);
        if (!isTokenValide) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        JsonObject json = Json.createObjectBuilder()
                .add("type", "resource")
                .add("meta", Json.createObjectBuilder()
                        .add("locale", "fr-FR")
                        .build())
                .add("commande", Json.createObjectBuilder()
                        .add("id", c.getId())
                        .add("nom", c.getNom())
                        .build())
                .build();

        return Response.ok(json).build();
    }

    @POST
    public Response postCommande(@Valid Commande c) throws ParseException {

        char[] d = c.getDateLivraison().toCharArray();
        d[2] = d[5] = '/';
        c.setDateLivraison(String.valueOf(d));

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        if (formatter.parse(c.getDateLivraison()).before(new Date())){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }


        Commande newCommande = this.cm.save(c);
        URI uri = uriInfo.getAbsolutePathBuilder().path(newCommande.getId()).build();
        return Response.created(uri)
                .entity(newCommande)
                .build();
    }

    @PUT
    @Path("{id}")
    public Response putCommande(
            @PathParam("id") String id,
            @DefaultValue("") @QueryParam("token") String tokenParam,
            @DefaultValue("") @HeaderParam("X-lbs-token") String tokenHeader,
            Commande c
    ) {
        Commande cmd = this.cm.findById(id);
        if (cmd == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (tokenParam.isEmpty() && tokenHeader.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String token = (tokenParam.isEmpty()) ? tokenHeader : tokenParam;

        if (!cmd.getToken().equals(token)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } else {
            cmd.setDateLivraison(c.getDateLivraison());
            cmd.setHeureLivraison(c.getHeureLivraison());

            return Response.ok(this.buildCommandeObject(cmd)).build();
        }
    }

    private JsonObject buildCommandeObject(Commande c) {
        return Json.createObjectBuilder()
                .add("commande", buildJsonForCommande(c))
                .build();
    }

    private JsonObject buildJsonForCommande(Commande c) {
        return Json.createObjectBuilder()
                .add("id", c.getId())
                .add("nom_client", c.getNom())
                .add("mail_client", c.getMail())
                .add("livraison", buildJsonForLivraison(c))
                .add("token", c.getToken())
                .build();
    }

    private JsonObject buildJsonForLivraison(Commande c) {
        return Json.createObjectBuilder()
                .add("date", c.getDateLivraison())
                .add("heure", c.getHeureLivraison())
                .build();
    }
}
