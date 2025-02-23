import java.awt.*;
import java.awt.image.ImageObserver;

public class MyObject {

    private int x;
    private int y;
    private Image image;

    public MyObject(int x, int y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public Image getImage() {
        return image;
    }

    public void setX(int number) {
        this.x = number;
    }

    public void addX(int number) {
        this.x += number;
    }

    public void subtractX(int number) {
        this.x -= number;
    }

    public void setY(int number) {
        this.y = number;
    }

    public void addY(int number) {
        this.y += number;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void draw(Graphics g, ImageObserver observer) {
        g.drawImage(image, x, y, observer);
    }

}
