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
@NamedQueries({
        @NamedQuery(name = "Tarif.findOne", query = "SELECT t FROM Tarif t WHERE t.sandwich LIKE :s AND t.taille LIKE :t"),
        @NamedQuery(name = "Tarif.findAll", query = "SELECT t FROM Tarif t")
})
public class Tarif implements Serializable {

    @Id
    @ManyToOne
    private Sandwich sandwich;

    @Id
    @ManyToOne
    private Taille taille;

    @ManyToMany
    private Set<Commande> commandes = new HashSet<Commande>();

    @NotNull
    private Float prix;

    public Tarif() {
    }

    public Tarif(Taille taille, Sandwich sandwich, Float prix) {
        this.taille = taille;
        this.sandwich = sandwich;
        this.prix = prix;
    }

    public Taille getTaille() {
        return taille;
    }

    public void setTaille(Taille taille) {
        this.taille = taille;
    }

    public Sandwich getSandwich() {
        return sandwich;
    }

    public void setSandwich(Sandwich sandwich) {
        this.sandwich = sandwich;
    }

    public Float getPrix() {
        return prix;
    }

    public void setPrix(Float prix) {
        this.prix = prix;
    }


}