package org.lpro.boundary;

import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TestSandwich {

    private Client client;
    private WebTarget target;
    
    @Before
    public void initClient(){
        this.client = ClientBuilder.newClient();
        this.target = this.client.target("http://localhost:8080/LBS/api/sandwichs");
    }
    
    @Test
    public void testAPI() {
        // POST
        JsonObjectBuilder categorie = Json.createObjectBuilder();
        JsonObject categorieJson = categorie
                .add("nom", "le pas trop vegan")
                .add("type_pain", "pain de mie")
                .add("description","le triple steack")
                .build();
        Response categorieResponse = this.target
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(categorieJson));
        String location = categorieResponse.getHeaderString("Location");
        assertThat(categorieResponse.getStatus(), is(201));
        
        // GET
        JsonObject jsonRecupere = this.client
                .target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);


        assertTrue(jsonRecupere.getJsonObject("sandwich").containsKey("id"));
        assertTrue(jsonRecupere.getJsonObject("sandwich").containsKey("nom"));
        assertTrue(jsonRecupere.getJsonObject("sandwich").getString("nom").contains("le pas trop vegan"));
        assertTrue(jsonRecupere.getJsonObject("sandwich").containsKey("type_pain"));
        assertTrue(jsonRecupere.getJsonObject("sandwich").getString("type_pain").contains("pain de mie"));
        assertTrue(jsonRecupere.getJsonObject("sandwich").containsKey("description"));
        assertTrue(jsonRecupere.getJsonObject("sandwich").getString("description").contains("le triple steack"));

        // DELETE
        Response deleteResponse = this.client
                .target(location)
                .request()
                .delete();
        assertThat(deleteResponse.getStatus(), is(204));
    }
   
}
