package solidexercicio10.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Missao {
    private final String piloto;
    private final Dificuldade dificuldade;
    private final Nave nave;
    private final int tamanhoMapa;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private final List<Asteroide> asteroides = new ArrayList<>();
    private final List<Inimigo> inimigos = new ArrayList<>();
    private final long inicio = System.currentTimeMillis();
    private int pontos;
    private int movimentos;
    private int resgatados;
    private Passageiro ultimoResgatado;

    public Missao(String piloto, Dificuldade dificuldade, Nave nave, int tamanhoMapa) {
        this.piloto = piloto;
        this.dificuldade = dificuldade;
        this.nave = nave;
        this.tamanhoMapa = tamanhoMapa;
        this.pontos = dificuldade.getPontosIniciais();
    }

    public String getPiloto() { return piloto; }
    public Dificuldade getDificuldade() { return dificuldade; }
    public Nave getNave() { return nave; }
    public int getTamanhoMapa() { return tamanhoMapa; }
    public List<Passageiro> getPassageiros() { return passageiros; }
    public List<Asteroide> getAsteroides() { return asteroides; }
    public List<Inimigo> getInimigos() { return inimigos; }
    public int getPontos() { return pontos; }
    public int getMovimentos() { return movimentos; }
    public int getResgatados() { return resgatados; }
    public Passageiro getUltimoResgatado() { return ultimoResgatado; }
    public long getDuracaoSegundos() { return (System.currentTimeMillis() - inicio) / 1000; }

    public void adicionarPassageiro(Passageiro p) { passageiros.add(p); }
    public void adicionarAsteroide(Asteroide a) { asteroides.add(a); }
    public void adicionarInimigo(Inimigo i) { inimigos.add(i); }

    public boolean todosResgatados() { return passageiros.isEmpty(); }

    public void moverNave(int dx, int dy) {
        nave.mover(dx, dy);
        movimentos++;
    }

    public void consumirCombustivel() { pontos--; }

    public void moverInimigos(Random r) {
        for (Inimigo i : inimigos) i.moverAleatorio(r, tamanhoMapa);
    }

    public boolean naveColidiu() {
        return asteroides.stream().anyMatch(nave::mesmaPosicao) || inimigos.stream().anyMatch(nave::mesmaPosicao);
    }

    public boolean resgatarNaPosicaoDaNave() {
        for (Passageiro p : passageiros) {
            if (nave.mesmaPosicao(p) && nave.embarcar(p)) {
                passageiros.remove(p);
                pontos += p.getPontuacao();
                resgatados++;
                ultimoResgatado = p;
                return true;
            }
        }
        return false;
    }
}
