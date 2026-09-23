package solidexercicio10.service;

import solidexercicio10.model.Asteroide;
import solidexercicio10.model.Astronauta;
import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.Engenheiro;
import solidexercicio10.model.Inimigo;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Nave;
import solidexercicio10.model.Professor;
import solidexercicio10.model.RankingEntry;
import solidexercicio10.repository.RankingRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class PartidaService {
    private final RankingRepository ranking;
    private final Random random;

    public PartidaService(RankingRepository ranking, Random random) {
        this.ranking = ranking;
        this.random = random;
    }

    public Missao novaMissao(String piloto, Dificuldade dif, int tamanhoMapa) {
        Missao missao = new Missao(piloto, dif, new Nave("A-1", dif.getCapacidadeNave()), tamanhoMapa);
        missao.adicionarPassageiro(new Professor("Prof. Silva", 2, 2));
        missao.adicionarPassageiro(new Engenheiro("Eng. Souza", -2, 1));
        missao.adicionarPassageiro(new Astronauta("Astro. Yuri", 1, -2));
        missao.adicionarPassageiro(new Engenheiro("Eng. Ana", -1, -2));
        missao.adicionarPassageiro(new Astronauta("Astro. Bob", 2, -1));
        for (int i = 0; i < dif.getQtdAsteroides(); i++) {
            int ax = random.nextInt(tamanhoMapa * 2 + 1) - tamanhoMapa;
            int ay = random.nextInt(tamanhoMapa * 2 + 1) - tamanhoMapa;
            if (ax != 0 || ay != 0) missao.adicionarAsteroide(new Asteroide(ax, ay));
        }
        missao.adicionarInimigo(new Inimigo(3, -1));
        return missao;
    }

    public ResultadoTurno jogarTurno(Missao missao, int dx, int dy) {
        boolean fasePouso = missao.todosResgatados();
        if (dx != 0 || dy != 0) {
            missao.moverNave(dx, dy);
            if (fasePouso) {
                missao.consumirCombustivel();
                if (missao.getPontos() <= 0) return ResultadoTurno.SEM_COMBUSTIVEL;
            }
        }

        missao.moverInimigos(random);

        if (missao.naveColidiu()) {
            missao.getNave().perderVida();
            if (missao.getNave().getVidas() == 0) return ResultadoTurno.NAVE_DESTRUIDA;
            missao.getNave().voltarParaBase();
            return ResultadoTurno.COLISAO;
        }

        if (fasePouso) {
            return missao.getNave().estaEm(0, 0) ? ResultadoTurno.VITORIA : ResultadoTurno.CONTINUA;
        }
        boolean resgatou = missao.resgatarNaPosicaoDaNave();
        if (missao.todosResgatados()) return ResultadoTurno.TODOS_A_BORDO;
        return resgatou ? ResultadoTurno.RESGATE : ResultadoTurno.CONTINUA;
    }

    public boolean bateuRecorde(Missao missao) {
        List<RankingEntry> lista = ranking.listarOrdenado();
        return !lista.isEmpty() && missao.getPontos() > lista.get(0).getPontos();
    }

    public void registrar(Missao missao) {
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ranking.salvar(new RankingEntry(missao.getPiloto(), missao.getPontos(), dataHora, missao.getResgatados(),
                missao.getDificuldade().name(), missao.getDuracaoSegundos(), missao.getMovimentos()));
    }

    public List<RankingEntry> top(int n) {
        List<RankingEntry> lista = ranking.listarOrdenado();
        return lista.subList(0, Math.min(n, lista.size()));
    }

    public boolean resetarRanking() {
        return ranking.resetar();
    }
}
