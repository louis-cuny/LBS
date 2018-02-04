package org.lpro.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
        @NamedQuery(name = "Sandwich.find", query = "SELECT s FROM Sandwich s WHERE s.type_pain LIKE :t AND COALESCE(s.img,'') LIKE :img "),
        @NamedQuery(name = "Sandwich.findAll", query = "SELECT s FROM Sandwich s")
})
public class Sandwich implements Serializable {

    @Id
    private String id;
    @NotNull
    private String nom;
    @NotNull
    private String type_pain;
    @NotNull
    private String description;

    private String img;

    @ManyToMany
    private Set<Categorie> categories = new HashSet<Categorie>();

    @OneToMany(mappedBy="sandwich")
    private Set<Tarif> tarifs = new HashSet<Tarif>();

    public Sandwich() {
    }

    public Sandwich(String id, String nom, String type_pain, String description, String img) {
        this.id = id;
        this.nom = nom;
        this.type_pain = type_pain;
        this.description = description;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType_pain() {
        return type_pain;
    }

    public void setType_pain(String type_pain) {
        this.type_pain = type_pain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Set<Categorie> getCategories() {
        return categories;
    }

    public void setCategories(Set<Categorie> categories) {
        this.categories = categories;
    }

    public Set<Tarif> getTarif() {
        return tarifs;
    }

    public void setTarifs(Set<Tarif> tarifs) {
        this.tarifs = tarifs;
    }

    public JsonObject getLightJson() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("nom", nom)
                .add("type_pain", type_pain)
                .build();
    }

    public JsonObject getJson() {

        JsonArrayBuilder categories = Json.createArrayBuilder();
        Set<Categorie> cl = this.getCategories();

        cl.forEach((c) ->
                categories.add(Json.createObjectBuilder()
                        .add("id", c.getId())
                        .add("nom", c.getNom())
                        .build()
                )
        );
        JsonArrayBuilder tailles = Json.createArrayBuilder();
        tarifs.forEach((t) ->
                tailles.add(Json.createObjectBuilder()
                        .add("id", t.getTaille().getId())
                        .add("nom", t.getTaille().getNom())
                        .add("prix", t.getPrix())
                        .build()
                )
        );
        JsonObjectBuilder job = Json.createObjectBuilder()
                .add("id", id)
                .add("nom", nom)
                .add("type_pain", type_pain)
                .add("description", description)
                .add("categories", categories.build())
                .add("tailles", tailles.build());

        if (img != null) {
            job.add("img", img);
        }
        return job.build();
    }

}
