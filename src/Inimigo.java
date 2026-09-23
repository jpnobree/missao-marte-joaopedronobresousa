import java.util.Random;

public class Inimigo {
    private int x;
    private int y;

    public Inimigo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void mover(Random r, int limite) {
        int dx = r.nextInt(3) - 1; // -1, 0, ou 1
        int dy = r.nextInt(3) - 1;

        if (Math.abs(x + dx) <= limite) x += dx;
        if (Math.abs(y + dy) <= limite) y += dy;
    }
}