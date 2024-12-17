package entities;

import jakarta.persistence.*;

@Entity
@Table(name = "house_points")
@NamedQueries({
        @NamedQuery(name="HousePoint.findMaxGiver",
                query="SELECT h.giver FROM HousePoint h GROUP BY h.giver HAVING SUM(h.points) = " +
                        "(SELECT MAX(total) FROM (SELECT SUM(hv.points) AS total FROM HousePoint hv GROUP BY hv.giver))"),
        @NamedQuery(name="HousePoint.findMaxReceiver",
                query="SELECT h.receiver FROM HousePoint h GROUP BY h.receiver HAVING SUM(h.points) = " +
                        "(SELECT MAX(total) FROM (SELECT SUM(hv.points) AS total FROM HousePoint hv GROUP BY hv.receiver))")
})
public class HousePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver")
    private entities.Person giver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver")
    private entities.Person receiver;

    @Column(name = "points")
    private Integer points;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public entities.Person getGiver() {
        return giver;
    }

    public void setGiver(entities.Person giver) {
        this.giver = giver;
    }

    public entities.Person getReceiver() {
        return receiver;
    }

    public void setReceiver(entities.Person receiver) {
        this.receiver = receiver;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

}