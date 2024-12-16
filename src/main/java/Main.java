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
            System.out.println("----Max Giver----");
            System.out.println(conn.geMaxGiver().getFirstName());

            System.out.println();
            System.out.println("----Max Receiver----");
            System.out.println(conn.geMaxReceiver().getFirstName());
        } catch (HibernateException e) {
            System.out.println("No se pudo establecer conexión a la base de datos");
        }
    }
}