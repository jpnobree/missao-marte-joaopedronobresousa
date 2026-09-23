package solidexercicio10;

import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Passageiro;
import solidexercicio10.model.RankingEntry;
import solidexercicio10.presentation.MapaRenderer;
import solidexercicio10.repository.ArquivoRankingRepository;
import solidexercicio10.repository.RankingRepository;
import solidexercicio10.service.PartidaService;
import solidexercicio10.service.ResultadoTurno;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PartidaServiceTeste {
    public static void main(String[] args) throws Exception {
        List<RankingEntry> memoria = new ArrayList<>();
        RankingRepository fake = new RankingRepository() {
            public void salvar(RankingEntry e) { memoria.add(e); }
            public List<RankingEntry> listarOrdenado() { return memoria; }
            public boolean resetar() { memoria.clear(); return true; }
        };
        PartidaService service = new PartidaService(fake, new Random(1));

        Missao m = service.novaMissao("Teste", Dificuldade.MEDIO, 5);
        m.getAsteroides().clear();
        m.getInimigos().clear();
        check(m.getPontos() == 20, "pontos iniciais");

        check(service.jogarTurno(m, 1, 0) == ResultadoTurno.CONTINUA, "movimento livre");
        check(service.jogarTurno(m, 0, -1) == ResultadoTurno.CONTINUA, "movimento livre 2");
        check(service.jogarTurno(m, 0, -1) == ResultadoTurno.RESGATE, "resgate astronauta em (1,-2)");
        check(m.getPontos() == 40 && m.getResgatados() == 1, "bonus astronauta +20");

        int total = 0;
        for (Passageiro p : new ArrayList<>(m.getPassageiros())) total += p.getPontuacao();
        check(total == 60, "LSP: subclasses usadas como Passageiro");

        m.getPassageiros().clear();
        check(service.jogarTurno(m, -1, 0) == ResultadoTurno.CONTINUA && m.getPontos() == 39, "combustivel na fase de pouso");
        check(service.jogarTurno(m, 0, 1) == ResultadoTurno.CONTINUA, "voltando");
        check(service.jogarTurno(m, 0, 1) == ResultadoTurno.VITORIA, "pouso em (0,0)");

        service.registrar(m);
        check(service.top(5).size() == 1 && service.top(5).get(0).getNomeJogador().equals("Teste"), "ranking salvo");

        Missao c = service.novaMissao("Colisao", Dificuldade.FACIL, 3);
        c.getAsteroides().clear();
        c.getInimigos().clear();
        c.adicionarAsteroide(new solidexercicio10.model.Asteroide(1, 0));
        check(service.jogarTurno(c, 1, 0) == ResultadoTurno.COLISAO && c.getNave().estaEm(0, 0), "colisao volta para base");
        service.jogarTurno(c, 1, 0);
        check(service.jogarTurno(c, 1, 0) == ResultadoTurno.NAVE_DESTRUIDA, "3 colisoes destroem a nave");

        check(new MapaRenderer().renderizar(c).contains("@"), "mapa desenha a nave");

        File tmp = File.createTempFile("ranking", ".json");
        tmp.delete();
        ArquivoRankingRepository arq = new ArquivoRankingRepository(tmp.getPath());
        arq.salvar(new RankingEntry("A", 10, "x", 1, "FACIL", 3, 4));
        arq.salvar(new RankingEntry("B", 30, "x", 5, "DIFICIL", 7, 8));
        check(arq.listarOrdenado().get(0).getNomeJogador().equals("B"), "arquivo ordenado por pontos");
        check(arq.resetar() && arq.listarOrdenado().isEmpty(), "reset apaga arquivo");

        System.out.println("OK: todos os testes passaram");
    }

    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
        System.out.println("  ok - " + msg);
    }
}
