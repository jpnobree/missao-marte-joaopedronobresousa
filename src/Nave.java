import java.util.ArrayList;
import java.util.List;

public class Nave {
    private String nome;
    private int capacidade;
    private int x;
    private int y;
    private int vidas;
    private List<Passageiro> passageirosEmbarcados;

    public Nave(String nome, int capacidade) {
        this.nome = nome;
        this.capacidade = capacidade;
        this.x = 0;
        this.y = 0;
        this.vidas = 3; // Sistema de vidas (Exercício 5)
        this.passageirosEmbarcados = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getVidas() { return vidas; }
    public List<Passageiro> getPassageirosEmbarcados() { return passageirosEmbarcados; }

    public void setPosicao(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void mover(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public void perderVida() {
        if (this.vidas > 0) {
            this.vidas--;
        }
    }

    public boolean embarcar(Passageiro p) {
        if (passageirosEmbarcados.size() < capacidade) {
            passageirosEmbarcados.add(p);
            return true;
        }
        return false;
    }
}