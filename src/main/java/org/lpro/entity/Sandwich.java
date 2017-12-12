package org.lpro.entity;

import java.io.Serializable;
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
    private long id;
    @NotNull
    private String nom;
    @NotNull
    private String type_pain;
    @NotNull
    private String description;

    private String img;

    public Sandwich() {
    }

    public Sandwich(long id, String nom, String type_pain, String description, String img) {
        this.id = id;
        this.nom = nom;
        this.type_pain = type_pain;
        this.description = description;
        this.img = img;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTypeDePain() {
        return type_pain;
    }

    public void setTypeDePain(String type_pain) {
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
}
