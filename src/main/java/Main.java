import entities.Course;
import entities.House;
import entities.Houses;
import entities.Person;
import org.hibernate.HibernateException;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            DBConnector conn = new DBConnector();
            System.out.println("Conexión establecida con éxito");

            List<Person> estudiantes = conn.getStudentsByProfessorName("Minerva");

            for (Person p : estudiantes) {
                System.out.println(p.getFirstName() + " " + p.getLastName());
            }

            System.out.println();
            System.out.println("----Max Giver(s)----");
            for (Person p : conn.geMaxGiver()) {
                System.out.println(p.getFirstName());
            }

            System.out.println();
            System.out.println("----Max Receiver(s)----");
            for (Person p : conn.geMaxReceiver()) {
                System.out.println(p.getFirstName());
            }

            System.out.println();
            conn.deletePersonByName("Shambala");

            System.out.println();
            List<Person> persons = conn.getPersonsByHouse("Hufflepuff");
            if (persons == null || persons.isEmpty()) {
                System.out.println("No se encontraron personajes");
            } else {
                for (Person p : persons) {
                    System.out.println(p.getFirstName() + " " + p.getLastName());
                }
            }
        } catch (HibernateException e) {
            System.out.println("No se pudo establecer conexión a la base de datos");
        }
    }
}
