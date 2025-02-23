import java.awt.*;

public class Line {

    private int y;
    private double speed;

    public Line() {
        this.y = 430;
        this.speed = 1.0;
    }

    public int getY() {
        return y;
    }

    public void moveDown() {
        y += speed;
        speed += 0.05;
    }

    public void decelerate() {
        y += speed;
        speed -= 0.05;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, y, SleighDrive.WIDTH_IMAGE, 2);
    }
}


