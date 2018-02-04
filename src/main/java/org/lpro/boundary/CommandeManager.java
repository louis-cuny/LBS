package org.lpro.boundary;

import javax.ejb.Stateless;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.*;
import javax.transaction.Transactional;

import org.lpro.control.RandomToken;
import org.lpro.entity.Commande;
import org.lpro.entity.Tarif;

@Stateless
@Transactional
public class CommandeManager {

    @PersistenceContext
    EntityManager em;

    public Commande findById(String id) {
        try {
            Commande c = this.em.find(Commande.class, id);
            return c;
        } catch (EntityNotFoundException enfe) {
            return null;
        }

    }

    public List<Commande> findAll() {
        Query q = this.em.createQuery("SELECT c FROM Commande c");
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        return q.getResultList();
    }

    public Commande save(Commande c) {
        RandomToken rt = new RandomToken();
        String token = rt.randomString(64);
        c.setToken(token);
        c.setId(UUID.randomUUID().toString());
        return this.em.merge(c);

    }

    public void addTarif(Commande c, Tarif t) {
        Set<Tarif> tarifs = c.getTarifs();
        tarifs.add(t);
        c.setTarifs(tarifs);
    }
}
