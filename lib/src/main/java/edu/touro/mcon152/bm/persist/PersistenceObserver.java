package edu.touro.mcon152.bm.persist;

import edu.touro.mcon152.bm.observers.BenchmarkObserver;
import edu.touro.mcon152.bm.observers.BenchmarkSubject;
import edu.touro.mcon152.bm.ui.Gui;
import jakarta.persistence.EntityManager;

public class PersistenceObserver implements BenchmarkObserver {
    @Override
    public void update(DiskRun run)
    {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();
    }
}
