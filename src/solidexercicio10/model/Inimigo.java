package solidexercicio10.model;

import java.util.Random;

public class Inimigo extends EntidadeMapa implements Movel {
    public Inimigo(int x, int y) {
        super(x, y);
    }

    @Override
    public void mover(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void moverAleatorio(Random r, int limite) {
        int dx = r.nextInt(3) - 1;
        int dy = r.nextInt(3) - 1;
        mover(Math.abs(x + dx) <= limite ? dx : 0, Math.abs(y + dy) <= limite ? dy : 0);
    }
}
