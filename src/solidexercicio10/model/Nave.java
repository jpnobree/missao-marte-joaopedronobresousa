package solidexercicio10.model;

import java.util.ArrayList;
import java.util.List;

public class Nave extends EntidadeMapa implements Movel {
    private final String nome;
    private final int capacidade;
    private int vidas = 3;
    private final List<Passageiro> embarcados = new ArrayList<>();

    public Nave(String nome, int capacidade) {
        super(0, 0);
        this.nome = nome;
        this.capacidade = capacidade;
    }

    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public int getVidas() { return vidas; }
    public List<Passageiro> getEmbarcados() { return embarcados; }

    @Override
    public void mover(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void voltarParaBase() {
        x = 0;
        y = 0;
    }

    public void perderVida() {
        if (vidas > 0) vidas--;
    }

    public boolean embarcar(Passageiro p) {
        if (embarcados.size() >= capacidade) return false;
        embarcados.add(p);
        return true;
    }
}
