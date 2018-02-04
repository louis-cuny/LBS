package org.lpro.boundary;

import org.lpro.entity.Tarif;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
public class TarifManager {

    @PersistenceContext
    EntityManager em;

    public Tarif findOne(String id_sandwich, String id_taille) {
        return this.em.createNamedQuery("Tarif.findOne", Tarif.class)
                .setParameter('s', id_sandwich)
                .setParameter('t', id_taille)
                .getSingleResult();
    }

    public List<Tarif> findAll() {
        Query q = this.em.createNamedQuery("Tarif.findAll", Tarif.class);
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        return q.getResultList();
    }

    public Tarif save(Tarif c) {
        return this.em.merge(c);
    }

    public void delete(String id) {
        try {
            Tarif ref = this.em.getReference(Tarif.class, id);
            this.em.remove(ref);
        } catch (EntityNotFoundException enfe) {
            // rien à faire   
        }
    }
}
