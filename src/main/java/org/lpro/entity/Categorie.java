package org.lpro.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name="Categorie.findAll",query="SELECT c FROM Categorie c")
public class Categorie implements Serializable{

    @Id
    private String id;
    @NotNull
    private String nom;
    @NotNull
    private String description;

    @ManyToMany(mappedBy = "categories")
    private Set<Sandwich> sandwichs = new HashSet<Sandwich>();


    public Categorie() {
    }

    public Categorie(String id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Sandwich> getSandwichs() {
        return sandwichs;
    }

    public void setSandwichs(Set<Sandwich> sandwichs) {
        this.sandwichs = sandwichs;
    }
}
