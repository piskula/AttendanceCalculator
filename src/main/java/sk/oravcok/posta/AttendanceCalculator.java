package sk.oravcok.posta;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Ondrej Oravcok on 26-Oct-16.
 */
public class AttendanceCalculator {
    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        // Start in-memory database
        new AnnotationConfigApplicationContext(PersistenceApplicationContext.class);

        emf = Persistence.createEntityManagerFactory("sk.oravcok.posta");
    }
}
