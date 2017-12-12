package org.lpro.boundary;

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
        return this.em.find(Sandwich.class, id);
    }

    public List<Sandwich> findAll() {
        Query q = this.em.createNamedQuery("Sandwich.findAll", Sandwich.class);
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        return q.getResultList();
    }

    public List<Sandwich> find(String t, int img, int page, int size) {

        double nbSandwichs = this.findAll().size();

        if (page <= 0) {
            page = 1;
        } else if (page > Math.ceil(nbSandwichs / (double) size)) {
            page = (int) Math.ceil(nbSandwichs / (double) size);
        }

        Query query = this.em.createNamedQuery("Sandwich.find",Sandwich.class );
        query.setParameter("t", '%' + t + '%');

        if (img == 1) {
            query.setParameter("img", "%_%");
        } else {
            query.setParameter("img", "%");
        }

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.getResultList();
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

    public JsonObject getMeta(long length, int page, int size) {
        return Json.createObjectBuilder()
                .add("count", ((length == -1) ? this.findAll().size() : size))
               // .add("size", this.findAllPerPage(page, size).size())
                .build();
    }
}
