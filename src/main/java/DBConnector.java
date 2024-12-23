import entities.Course;
import entities.Enrollment;
import entities.EnrollmentId;
import entities.Person;
import jakarta.persistence.*;
import org.hibernate.HibernateException;

import java.util.ArrayList;
import java.util.HashMap;
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

    public List<Person> geMaxGiver() {
        try {
            /*String view = "select SUM(hp.points) as suma_puntos, hp.giver as personaje FROM HousePoint hp GROUP BY hp.giver";
            Query query = this.em.createQuery("SELECT personaje from (" + view + ") WHERE " +
                    "suma_puntos = (SELECT MAX(suma_puntos) from (" + view + "))");
            return (Person) query.getSingleResult();*/
            Query query = this.em.createNamedQuery("HousePoint.findMaxGiver");
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            System.out.println("Error de argumento: " + e.getMessage());
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Person> geMaxReceiver() {
        try {
            /*String view = "select SUM(hp.points) as suma_puntos, hp.receiver as personaje FROM HousePoint hp GROUP BY hp.receiver";
            Query query = this.em.createQuery("SELECT personaje from (" + view + ") WHERE " +
                    "suma_puntos = (SELECT MAX(suma_puntos) from (" + view + "))");
            return (Person) query.getSingleResult();*/
            Query query = this.em.createNamedQuery("HousePoint.findMaxReceiver");
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            System.out.println("Error de argumento: " + e.getMessage());
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deletePersonByName(String name) {
        try {
            if (name == null) {
                throw new IllegalArgumentException("El nombre no puede ser nulo");
            }
            Person person = getPerson(name);
            if (person == null) {
                throw new IllegalArgumentException("No se pudo encontrar el personaje a borrar");
            }
            this.em.getTransaction().begin();
            Query query = this.em.createQuery("DELETE FROM Person p WHERE p.id = :id");
            query.setParameter("id", person.getId());
            int affected_rows = query.executeUpdate();
            if (affected_rows == 1) {
                this.em.getTransaction().commit();
                System.out.println("Personaje borrado exitosamente");
            } else {
                throw new HibernateException("No se pudo borrar el personaje");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
            this.em.getTransaction().rollback();
        }
    }

    public Person getPerson(String name) {
        try {
            if (name == null) {
                throw new IllegalArgumentException("El nombre no puede ser nulo");
            }
            Person person = new Person();
            Query query = this.em.createQuery("SELECT p FROM Person p WHERE p.firstName = :name");
            query.setParameter("name", name);
            person = (Person) query.getSingleResult();
            return person;
        } catch (IllegalArgumentException e) {
            System.out.println("Error de argumento: " + e.getMessage());
        } catch (NonUniqueResultException e) {
            System.out.println("Se obtuvieron más resultados de los esperados");
        } catch (NoResultException e) {
            System.out.println("El personaje con nombre: " + name + " no existe");
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Person> getPersonsByHouse(String houseName) {
        try {
            if (houseName == null) {
                throw new IllegalArgumentException("El nombre no puede ser nulo");
            }
            Query query = this.em.createNativeQuery(
                    "SELECT person.id " +
                            "FROM person INNER JOIN house ON person.house_id = house.id " +
                            "WHERE house.name = ?"
            );
            List<Person> persons = new ArrayList<>();
            query.setParameter(1, houseName);
            for (Object o : query.getResultList()) {
                persons.add(this.em.find(Person.class, (Integer) o));
            }
            return persons;
        } catch (IllegalArgumentException e) {
            System.out.println("Error de argumento: " + e.getMessage());
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public HashMap<String, List<Person>> getCoursesWithStudents() {
        HashMap<String, List<Person>> courseMap = new HashMap<>();
        try {
            Query query = this.em.createNativeQuery("SELECT name, person_enrollment FROM course " +
                    "LEFT OUTER JOIN enrollment ON course.id = enrollment.course_enrollment", Tuple.class);
            List<Tuple> tuples = query.getResultList();
            tuples.forEach(tuple -> {
                if (!courseMap.containsKey(tuple.get(0))) {
                    courseMap.put(tuple.get(0).toString(), new ArrayList<Person>());
                }
                if (tuple.get(1) != null) {
                    Person person = this.em.find(Person.class, tuple.get(1));
                    courseMap.get(tuple.get(0).toString()).add(person);
                }
            });
            return courseMap;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return courseMap;
    }

    public List<Person> getPersonsByPoints(int points) {
        List<Person> persons = new ArrayList<>();
        try {
            Query query = this.em.createNativeQuery("select person.id from person " +
                    "where (select sum(points) from house_points where house_points.receiver = person.id) >= ?");
            query.setParameter(1, points);
            if (query.getResultList().size() > 0) {
                for (Object o : query.getResultList()) {
                    Person person = this.em.find(Person.class, (Integer) o);
                    persons.add(person);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return persons;
    }

    /*
    select person.id from person
        where (select sum(points) from house_points where house_points.receiver = person.id) > 50;
    */
}
