import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Missao {
    private Nave nave;
    private List<Passageiro> passageiros;
    private List<Asteroide> asteroides;
    private List<Inimigo> inimigos;
    private int tamanhoMapa;

    public Missao(Nave nave, int tamanhoMapa) {
        this.nave = nave;
        this.tamanhoMapa = tamanhoMapa;
        this.passageiros = new ArrayList<>();
        this.asteroides = new ArrayList<>();
        this.inimigos = new ArrayList<>();
    }

    public Nave getNave() { return nave; }
    public List<Passageiro> getPassageiros() { return passageiros; }
    public List<Asteroide> getAsteroides() { return asteroides; }
    public List<Inimigo> getInimigos() { return inimigos; }
    public int getTamanhoMapa() { return tamanhoMapa; }

    public void adicionarPassageiro(Passageiro p) { passageiros.add(p); }
    public void adicionarAsteroide(Asteroide a) { asteroides.add(a); }
    public void adicionarInimigo(Inimigo i) { inimigos.add(i); }

    public void moverInimigos(Random r) {
        for (Inimigo i : inimigos) {
            i.mover(r, tamanhoMapa);
        }
    }

    public boolean verificaColisaoAsteroide() {
        for (Asteroide a : asteroides) {
            if (a.getX() == nave.getX() && a.getY() == nave.getY()) return true;
        }
        return false;
    }

    public boolean verificaColisaoInimigo() {
        for (Inimigo i : inimigos) {
            if (i.getX() == nave.getX() && i.getY() == nave.getY()) return true;
        }
        return false;
    }

    public Passageiro verificaResgate() {
        for (Passageiro p : passageiros) {
            if (p.getX() == nave.getX() && p.getY() == nave.getY()) return p;
        }
        return null;
    }

    public void desenharMapa() {
        System.out.println("\n--- MAPA ESPACIAL ---");
        for (int y = tamanhoMapa; y >= -tamanhoMapa; y--) {
            for (int x = -tamanhoMapa; x <= tamanhoMapa; x++) {
                char symbol = '.';

                // Plataforma de pouso na origem (0,0)
                if (x == 0 && y == 0) symbol = 'L';

                for (Passageiro p : passageiros) {
                    if (p.getX() == x && p.getY() == y) symbol = 'P';
                }

                for (Asteroide a : asteroides) {
                    if (a.getX() == x && a.getY() == y) symbol = '#';
                }

                for (Inimigo i : inimigos) {
                    if (i.getX() == x && i.getY() == y) symbol = 'X';
                }

                if (nave.getX() == x && nave.getY() == y) symbol = '@';

                System.out.print(symbol + " ");
            }
            System.out.println();
        }
    }
}