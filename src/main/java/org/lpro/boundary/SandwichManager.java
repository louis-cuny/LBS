package org.lpro.boundary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.lpro.entity.Sandwich;

@Stateless
public class SandwichManager {

    @PersistenceContext
    EntityManager em;

    public Sandwich findById(long id) {
        try {
            Sandwich s = this.em.find(Sandwich.class, id);
            return s;
        } catch (EntityNotFoundException enfe) {
            return null;
        }
    }

    public List<Sandwich> findAll() {
        return this.em.createNamedQuery("Sandwich.findAll", Sandwich.class)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    public List<Sandwich> find(String t, int img, int page, int size) {

        double nbSandwichs = this.findAll().size();

        if (page <= 0) {
            page = 1;
        } else if (page > Math.ceil(nbSandwichs / (double) size)) {
            page = (int) Math.ceil(nbSandwichs / (double) size);
        }

        return this.em.createNamedQuery("Sandwich.find", Sandwich.class)
                .setParameter("t", "%" + t + "%")
                .setParameter("img", (img == 1) ? "%_%" : "%")
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public Sandwich save(Sandwich s) {
        return this.em.merge(s);
    }

    public void delete(long id) {
        try {
            Sandwich ref = this.em.getReference(Sandwich.class, id);
            this.em.remove(ref);
        } catch (EntityNotFoundException enfe) {
            // rien à faire   
        }
    }

    public JsonObject getMeta(String t, int img, List list) {
        return Json.createObjectBuilder()
                .add("count", em.createNamedQuery("Sandwich.find", Sandwich.class)
                        .setParameter("t", "%" + t + "%")
                        .setParameter("img", (img == 1) ? "%_%" : "%")
                        .getResultList()
                        .size())
                .add("size", list.size())
                .add("date", new SimpleDateFormat("dd-MM-yy").format(new Date()))
                .build();
    }
}
