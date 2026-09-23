package solidexercicio10.presentation;

import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.Missao;
import solidexercicio10.model.RankingEntry;
import solidexercicio10.service.PartidaService;
import solidexercicio10.service.ResultadoTurno;

import java.util.List;
import java.util.Scanner;

public class JogoConsole {
    private final Scanner scanner;
    private final PartidaService service;
    private final MapaRenderer renderer;

    public JogoConsole(Scanner scanner, PartidaService service, MapaRenderer renderer) {
        this.scanner = scanner;
        this.service = service;
        this.renderer = renderer;
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("   MISSÃO ESPACIAL: POUSO LUNAR ");
            System.out.println("=================================");
            System.out.println("1. Iniciar Nova Missão");
            System.out.println("2. Visualizar Ranking Top 5");
            System.out.println("3. Resetar Histórico de Ranking");
            System.out.println("4. Sair do Jogo");
            System.out.print("Escolha uma opção: ");
            if (!scanner.hasNextLine()) return;
            switch (scanner.nextLine().trim()) {
                case "1": jogar(); break;
                case "2": exibirRanking(); break;
                case "3":
                    System.out.println(service.resetarRanking()
                            ? "\nHistórico de ranking apagado com sucesso!"
                            : "\nNão existe histórico de ranking para apagar.");
                    break;
                case "4":
                    System.out.println("Encerrando o sistema espacial. Até a próxima missão!");
                    return;
                default:
                    System.out.println("Opção inválida! Escolha de 1 a 4.");
            }
        }
    }

    private void jogar() {
        System.out.print("\nNome do piloto: ");
        String piloto = scanner.nextLine().trim();
        Dificuldade dif = selecionarDificuldade();
        int tamanho = lerInteiroPositivo("Tamanho do mapa (-X a +X): ");
        Missao missao = service.novaMissao(piloto.isEmpty() ? "Piloto" : piloto, dif, tamanho);

        System.out.println("\n--- FASE 1: RESGATE TODOS OS PASSAGEIROS ---");
        while (true) {
            System.out.print(renderer.renderizar(missao));
            System.out.println(renderer.status(missao));
            System.out.print("Mover (W/A/S/D ou Q para desistir): ");
            if (!scanner.hasNextLine()) return;
            String op = scanner.nextLine().trim().toUpperCase();
            if (op.equals("Q")) return;

            int dx = op.equals("D") ? 1 : op.equals("A") ? -1 : 0;
            int dy = op.equals("W") ? 1 : op.equals("S") ? -1 : 0;
            ResultadoTurno r = service.jogarTurno(missao, dx, dy);
            exibirResultado(r, missao);
            if (r == ResultadoTurno.VITORIA) finalizar(missao);
            if (r.encerraMissao()) return;
        }
    }

    private void exibirResultado(ResultadoTurno r, Missao m) {
        switch (r) {
            case SEM_COMBUSTIVEL:
                System.out.println("\n*** FALTOU COMBUSTÍVEL! A nave ficou à deriva no espaço. GAME OVER! ***");
                break;
            case NAVE_DESTRUIDA:
                System.out.println("\n*** GAME OVER! Sua nave foi destruída. ***");
                break;
            case COLISAO:
                System.out.println("\n! Colisão detectada! Vidas restantes: " + m.getNave().getVidas());
                break;
            case RESGATE:
            case TODOS_A_BORDO:
                System.out.println("Resgatou " + m.getUltimoResgatado().getNome() + "! +" + m.getUltimoResgatado().getPontuacao() + " pts.");
                if (r == ResultadoTurno.TODOS_A_BORDO) {
                    System.out.println("\n>>> TODOS OS PASSAGEIROS A BORDO! <<<");
                    System.out.println(">>> ALERTA DE POUSO: Retorne com segurança até a plataforma 'L' em (0,0)! <<<");
                }
                break;
            case VITORIA:
                System.out.println("\n=========================================");
                System.out.println(" POUSO CONCLUÍDO COM SUCESSO EM (0,0)! ");
                System.out.println("   VOCÊ VENCEU A MISSÃO LUNAR! ");
                System.out.println("=========================================");
                break;
            default:
        }
    }

    private void finalizar(Missao m) {
        System.out.println("\n--- ESTATÍSTICAS DA PARTIDA ---");
        System.out.println("Piloto: " + m.getPiloto());
        System.out.println("Tempo de jogo: " + m.getDuracaoSegundos() + " segundos");
        System.out.println("Total de movimentos: " + m.getMovimentos());
        System.out.println("Passageiros resgatados: " + m.getResgatados());
        System.out.println("Pontuação final: " + m.getPontos());
        if (service.bateuRecorde(m)) System.out.println("\n*** PARABÉNS! VOCÊ BATEU O RECORDE DO SERVIDOR! ***");
        try {
            service.registrar(m);
            System.out.println("Pontuação registrada com sucesso!");
        } catch (RuntimeException e) {
            System.out.println("Erro ao salvar o ranking: " + e.getMessage());
        }
    }

    private void exibirRanking() {
        List<RankingEntry> top = service.top(5);
        System.out.println("\n=========================================");
        System.out.println("        TOP 5 PILOTOS NO RANKING         ");
        System.out.println("=========================================");
        if (top.isEmpty()) {
            System.out.println("Nenhum registro encontrado no ranking.");
            return;
        }
        for (int i = 0; i < top.size(); i++) {
            RankingEntry r = top.get(i);
            System.out.printf("%dº - %s | Pontos: %d | Dif: %s | Tempo: %ds | Movs: %d%n",
                    i + 1, r.getNomeJogador(), r.getPontos(), r.getDificuldade(), r.getDuracaoSegundos(), r.getMovimentos());
        }
    }

    private Dificuldade selecionarDificuldade() {
        System.out.println("\nSelecione a Dificuldade:");
        System.out.println("1 - Fácil");
        System.out.println("2 - Médio");
        System.out.println("3 - Difícil");
        System.out.print("Opção: ");
        switch (scanner.nextLine().trim()) {
            case "1": return Dificuldade.FACIL;
            case "3": return Dificuldade.DIFICIL;
            default: return Dificuldade.MEDIO;
        }
    }

    private int lerInteiroPositivo(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                int v = Integer.parseInt(scanner.nextLine().trim());
                if (v > 0) return v;
            } catch (NumberFormatException ignorada) {
            }
            System.out.println("Informe um número inteiro maior que zero.");
        }
    }
}
