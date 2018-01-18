package org.lpro.boundary;

import org.lpro.entity.Taille;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
public class TailleManager {

    @PersistenceContext
    EntityManager em;

    public Taille findById(String id) {
        return this.em.find(Taille.class, id);
    }

    public List<Taille> findAll() {
        Query q = this.em.createNamedQuery("Taille.findAll", Taille.class);
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        return q.getResultList();
    }

    public Taille save(Taille c) {
        return this.em.merge(c);
    }

    public void delete(long id) {
        try {
            Taille ref = this.em.getReference(Taille.class, id);
            this.em.remove(ref);
        } catch (EntityNotFoundException enfe) {
            // rien à faire   
        }
    }
}
