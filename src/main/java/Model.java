import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.persistence.NoResultException;
import java.util.List;

public class Model {
    private static SessionFactory sessionFactory;
    static {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Subscriber.class)
                .buildSessionFactory();
    }
    private Session session;

    public void saveSub(User user, String location, Long chatId) {
        Subscriber sub = new Subscriber().init(user, location, chatId);
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(sub);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public void deleteSub(int id) {
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            SQLQuery query = session.createSQLQuery(
                    "delete from subscriber where id = :id");
            query.setParameter("id", id);
            query.executeUpdate();
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public boolean subExists(int id) {
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            SQLQuery query = session.createSQLQuery(
                    "select * from subscriber where id = :id");
            query.addEntity(Subscriber.class);
            query.setParameter("id", id);
            try {
                query.getSingleResult();
            } catch (NoResultException e) {
                return false;
            }
            session.getTransaction().commit();
            return true;
        } finally {
            session.close();
        }
    }

    public Subscriber retrieveSub(int id) {
        Subscriber sub;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            sub = session.get(Subscriber.class, id);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
        return sub;
    }

    public List<Subscriber> retrieveAllSubs() {
        List<Subscriber> subscribers;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            subscribers = session.createQuery("from Subscriber", Subscriber.class).getResultList();
            session.getTransaction().commit();
        } finally {
            session.close();
        }
        return subscribers;
    }
}
