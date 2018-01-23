package org.lpro.boundary;

import java.math.BigDecimal;
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
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class TestTD2API {

    private Client client;
    private WebTarget target;
    
    @Before
    public void initClient(){
        this.client = ClientBuilder.newClient();
        this.target = this.client.target("http://localhost:8080/td2/api/categories");
    }
    
    @Test
    public void testAPI() {
        // POST
        JsonObjectBuilder categorie = Json.createObjectBuilder();
        JsonObject categorieJson = categorie
                .add("nom", "bio")
                .add("description","un sandwich bio")
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
        assertTrue(jsonRecupere.containsKey("nom"));
        assertTrue(jsonRecupere.getString("description").contains("sandwich bio"));
        
        // DELETE
        Response deleteResponse = this.client
                .target(location)
                .request()
                .delete();
        assertThat(deleteResponse.getStatus(), is(204));
    }
   
}
