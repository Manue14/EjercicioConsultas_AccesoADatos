import entities.Course;
import entities.House;
import entities.Houses;
import entities.Person;
import org.hibernate.HibernateException;

import java.util.HashMap;
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

            System.out.println();
            HashMap<String, List<Person>> courseMap = conn.getCoursesWithStudents();
            for (String key : courseMap.keySet()) {
                System.out.println(key + ":");
                for (Person p : courseMap.get(key)) {
                    System.out.println("\t" + p.getFirstName() + " " + p.getLastName());
                }
                System.out.println();
            }

            System.out.println();
            List<Person> persons_list = conn.getPersonsByPoints(100);
            if (persons_list.isEmpty()) {
                System.out.println("No se encontraron personajes");
            } else {
                for (Person p : persons_list) {
                    System.out.println(p.getFirstName() + " " + p.getLastName());
                }
            }
        } catch (HibernateException e) {
            System.out.println("No se pudo establecer conexión a la base de datos");
        }
    }
}
