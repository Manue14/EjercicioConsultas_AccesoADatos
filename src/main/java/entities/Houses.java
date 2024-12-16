package entities;

public enum Houses {
    GRYFFINDOR(1), HUFFLEPUFF(2), RAVENCLAW(3), SLYTHERIN(4);
    public int value;
    private Houses(int value) {
        this.value = value;
    }
}
