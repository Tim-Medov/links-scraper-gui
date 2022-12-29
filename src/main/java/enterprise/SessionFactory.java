
package enterprise;

import org.hibernate.Session;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class SessionFactory {

    public static Session getSession() {

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();

        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        Metadata metadata = metadataSources.buildMetadata();

        org.hibernate.SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        Session session = sessionFactory.getCurrentSession();

        return session;
    }
}
