package solidexercicio10.presentation;

import solidexercicio10.model.Missao;
import solidexercicio10.model.Posicionavel;

import java.util.List;

public class MapaRenderer {
    public String renderizar(Missao m) {
        StringBuilder sb = new StringBuilder("\n--- MAPA ESPACIAL ---\n");
        for (int y = m.getTamanhoMapa(); y >= -m.getTamanhoMapa(); y--) {
            for (int x = -m.getTamanhoMapa(); x <= m.getTamanhoMapa(); x++) {
                char s = x == 0 && y == 0 ? 'L' : '.';
                if (algumEm(m.getPassageiros(), x, y)) s = 'P';
                if (algumEm(m.getAsteroides(), x, y)) s = '#';
                if (algumEm(m.getInimigos(), x, y)) s = 'X';
                if (m.getNave().estaEm(x, y)) s = '@';
                sb.append(s).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public String status(Missao m) {
        if (m.todosResgatados()) {
            return "!!! TODOS RESGATADOS! RETORNE À PLATAFORMA (L) EM (0,0) !!!\n"
                    + "Combustível Restante: " + m.getPontos() + " | Vidas: " + m.getNave().getVidas();
        }
        return "Combustível/Pontos: " + m.getPontos() + " | Vidas: " + m.getNave().getVidas()
                + " | Resgatados: " + m.getResgatados() + "/" + m.getNave().getCapacidade();
    }

    private static boolean algumEm(List<? extends Posicionavel> itens, int x, int y) {
        return itens.stream().anyMatch(p -> p.estaEm(x, y));
    }
}
