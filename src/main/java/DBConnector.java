import entities.Course;
import entities.Enrollment;
import entities.EnrollmentId;
import entities.Person;
import jakarta.persistence.*;
import org.hibernate.HibernateException;

import java.util.ArrayList;
import java.util.List;

public class DBConnector {
    private EntityManagerFactory emf;
    private EntityManager em;

    public DBConnector() throws HibernateException {
        this.emf = Persistence.createEntityManagerFactory("default");
        this.em = emf.createEntityManager();
    }

    public void insertPerson(Person person) {
        try {
            this.em.getTransaction().begin();
            if (person == null) {
                throw new IllegalArgumentException("La persona no puede ser nula");
            }
            this.em.persist(person);
            this.em.getTransaction().commit();
            System.out.println("Person added successfully");
        } catch (EntityExistsException e) {
            this.em.getTransaction().rollback();
            System.out.println("La persona ya existe");
        } catch (IllegalArgumentException e) {
            this.em.getTransaction().rollback();
            System.out.println("El objeto que intentas persistir no es válido");
        } catch (PersistenceException e) {
            this.em.getTransaction().rollback();
            System.out.println("Error de persistencia: " + e.getMessage());
        } catch (Exception e) {
            this.em.getTransaction().rollback();
            System.out.println(e.getMessage());
        }
    }

    public Course getCourse(String name) {
        try {
            if (name == null) {
                throw new IllegalArgumentException("El nombre no puede ser nulo");
            }
            Course course = new Course();
            Query query = this.em.createQuery("SELECT c FROM Course c WHERE c.name = :name");
            query.setParameter("name", name);
            course = (Course) query.getSingleResult();
            return course;
        } catch (IllegalArgumentException e) {
            System.out.println("Error de argumento: " + e.getMessage());
        } catch (NonUniqueResultException e) {
            System.out.println("Se obtuvieron más resultados de los esperados");
        } catch (NoResultException e) {
            System.out.println("El curso con el nombre: " + name + " no existe");
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void enroll(Person person, Course course) {
        try {
            this.em.getTransaction().begin();
            if (person == null || course == null) {
                throw new IllegalArgumentException("Ni la persona ni el curso pueden ser nulos");
            }
            Person helper = this.em.find(Person.class, person.getId());
            Enrollment enrollment = new Enrollment();
            EnrollmentId enrollmentId = new EnrollmentId();
            enrollmentId.setPersonEnrollment(person.getId());
            enrollmentId.setCourseEnrollment(course.getId());
            enrollment.setId(enrollmentId);
            enrollment.setPersonEnrollment(helper);
            enrollment.setCourseEnrollment(course);

            this.em.persist(enrollment);
            this.em.getTransaction().commit();
            System.out.println("Enrollment added successfully");
        } catch (EntityExistsException e) {
            this.em.getTransaction().rollback();
            System.out.println("El enrollment ya existe: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            this.em.getTransaction().rollback();
            System.out.println(e.getMessage());
        } catch (PersistenceException e) {
            this.em.getTransaction().rollback();
            System.out.println("Error de persistencia: " + e.getMessage());
        } catch (Exception e) {
            this.em.getTransaction().rollback();
            System.out.println(e.getMessage());
        }
    }

    public List<Person> getStudentsByProfessorName(String name) {
        try {
            if (name == null) {
                throw new IllegalArgumentException("El nombre no puede ser nulo");
            }
            Query query = this.em.createQuery("SELECT p FROM Person p " +
                    "JOIN Enrollment e ON p = e.personEnrollment AND e.courseEnrollment IN " +
                    "(SELECT c FROM Course c WHERE c.teacher = " +
                    "(SELECT p FROM Person p WHERE p.firstName = :name))");
            query.setParameter("name", name);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            System.out.println("Error de argumento: " + e.getMessage());
        } catch (NoResultException e) {
            System.out.println("El profesor con el nombre: " + name + " no existe");
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Person geMaxGiver() {
        try {
            String view = "select SUM(hp.points) as suma_puntos, hp.giver as personaje FROM HousePoint hp GROUP BY hp.giver";
            Query query = this.em.createQuery("SELECT personaje from (" + view + ") WHERE " +
                    "suma_puntos = (SELECT MAX(suma_puntos) from (" + view + "))");
            return (Person) query.getSingleResult();
        } catch (IllegalArgumentException e) {
            System.out.println("Error de argumento: " + e.getMessage());
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Person geMaxReceiver() {
        try {
            String view = "select SUM(hp.points) as suma_puntos, hp.receiver as personaje FROM HousePoint hp GROUP BY hp.receiver";
            Query query = this.em.createQuery("SELECT personaje from (" + view + ") WHERE " +
                    "suma_puntos = (SELECT MAX(suma_puntos) from (" + view + "))");
            return (Person) query.getSingleResult();
        } catch (IllegalArgumentException e) {
            System.out.println("Error de argumento: " + e.getMessage());
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
