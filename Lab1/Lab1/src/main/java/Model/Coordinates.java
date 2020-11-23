package Model;

public class Coordinates {
    private double x;
    private Long y; //Поле не может быть null

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }
}
