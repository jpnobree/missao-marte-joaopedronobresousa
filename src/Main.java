import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final String RANKING_FILE = "ranking.json";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            exibirMenuPrincipal();
            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1":
                    jogarPartida(scanner);
                    break;
                case "2":
                    exibirTopRanking();
                    break;
                case "3":
                    resetarRanking();
                    break;
                case "4":
                    System.out.println("Encerrando o sistema espacial. Até a próxima missão!");
                    return;
                default:
                    System.out.println("Opção inválida! Escolha de 1 a 4.");
            }
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n=================================");
        System.out.println("   MISSÃO ESPACIAL: POUSO LUNAR ");
        System.out.println("=================================");
        System.out.println("1. Iniciar Nova Missão");
        System.out.println("2. Visualizar Ranking Top 5");
        System.out.println("3. Resetar Histórico de Ranking");
        System.out.println("4. Sair do Jogo");
        System.out.print("Escolha uma opção: ");
    }

    private static void jogarPartida(Scanner scanner) {
        Random random = new Random();
        Dificuldade dif = selecionarDificuldade(scanner);

        System.out.print("Tamanho do mapa (-X a +X): ");
        int tamanhoMapa = Integer.parseInt(scanner.nextLine());

        Nave nave = new Nave("A-1", dif.getCapacidadeNave());
        Missao missao = new Missao(nave, tamanhoMapa);

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

        int pontos = dif.getPontosIniciais();
        int passageirosColetados = 0;
        int movimentos = 0;
        long tempoInicio = System.currentTimeMillis();
        boolean resgatouTodos = false;

        System.out.println("\n--- FASE 1: RESGATE TODOS OS PASSAGEIROS ---");

        while (true) {
            missao.desenharMapa();

            if (!resgatouTodos) {
                System.out.println("Combustível/Pontos: " + pontos + " | Vidas: " + nave.getVidas() + " | Resgatados: " + passageirosColetados + "/" + dif.getCapacidadeNave());
            } else {
                System.out.println("!!! TODOS RESGATADOS! RETORNE À PLATAFORMA (L) EM (0,0) !!!");
                System.out.println("Combustível Restante: " + pontos + " | Vidas: " + nave.getVidas());
            }

            System.out.print("Mover (W/A/S/D ou Q para desistir): ");
            String opcao = scanner.nextLine().toUpperCase();
            if (opcao.equals("Q")) break;

            int dx = 0, dy = 0;
            if (opcao.equals("W")) dy = 1;
            else if (opcao.equals("S")) dy = -1;
            else if (opcao.equals("A")) dx = -1;
            else if (opcao.equals("D")) dx = 1;

            if (dx != 0 || dy != 0) {
                nave.mover(dx, dy);
                movimentos++;

                if (resgatouTodos) {
                    pontos--;
                    if (pontos <= 0) {
                        System.out.println("\n*** FALTOU COMBUSTÍVEL! A nave ficou à deriva no espaço. GAME OVER! ***");
                        return;
                    }
                }
            }

            missao.moverInimigos(random);

            if (missao.verificaColisaoAsteroide() || missao.verificaColisaoInimigo()) {
                nave.perderVida();
                if (nave.getVidas() == 0) {
                    System.out.println("\n*** GAME OVER! Sua nave foi destruída. ***");
                    return;
                } else {
                    System.out.println("\n! Colisão detectada! Vidas restantes: " + nave.getVidas());
                    nave.setPosicao(0, 0);
                }
            }

            if (!resgatouTodos) {
                Passageiro resgatado = missao.verificaResgate();
                if (resgatado != null) {
                    if (nave.embarcar(resgatado)) {
                        pontos += resgatado.getPontuacao();
                        passageirosColetados++;
                        missao.getPassageiros().remove(resgatado);
                        System.out.println("Resgatou " + resgatado.getNome() + "! +" + resgatado.getPontuacao() + " pts.");
                    }
                }

                if (missao.getPassageiros().isEmpty()) {
                    resgatouTodos = true;
                    System.out.println("\n>>> TODOS OS PASSAGEIROS A BORDO! <<");
                    System.out.println(">>> ALERTA DE POUSO: Retorne com segurança até a plataforma 'L' em (0,0)! <<<");
                }
            } else {
                if (nave.getX() == 0 && nave.getY() == 0) {
                    long tempoFim = System.currentTimeMillis();
                    long duracao = (tempoFim - tempoInicio) / 1000;

                    System.out.println("\n=========================================");
                    System.out.println(" POUSO CONCLUÍDO COM SUCESSO EM (0,0)! ");
                    System.out.println("   VOCÊ VENCEU A MISSÃO LUNAR! ");
                    System.out.println("=========================================");

                    exibirEstatisticaEFim(scanner, pontos, passageirosColetados, dif, duracao, movimentos);
                    return;
                }
            }
        }
    }

    private static Dificuldade selecionarDificuldade(Scanner scanner) {
        System.out.println("\nSelecione a Dificuldade:");
        System.out.println("1 - Fácil");
        System.out.println("2 - Médio");
        System.out.println("3 - Difícil");
        System.out.print("Opção: ");
        String op = scanner.nextLine();

        switch (op) {
            case "1": return Dificuldade.FACIL;
            case "3": return Dificuldade.DIFICIL;
            default: return Dificuldade.MEDIO;
        }
    }

    private static void exibirEstatisticaEFim(Scanner scanner, int pontos, int passageiros, Dificuldade dif, long duracao, int movimentos) {
        System.out.println("\n--- ESTATÍSTICAS DA PARTIDA ---");
        System.out.println("Tempo de jogo: " + duracao + " segundos");
        System.out.println("Total de movimentos: " + movimentos);
        System.out.println("Pontuação final: " + pontos);

        int recordeAtual = obterMaiorPontuacao();
        if (pontos > recordeAtual && recordeAtual > 0) {
            System.out.println("\n*** PARABÉNS! VOCÊ BATEU O RECORDE DO SERVIDOR! ***");
        }

        System.out.print("\nDigite seu nome para salvar no Ranking: ");
        String nome = scanner.nextLine();

        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        RankingEntry entry = new RankingEntry(nome, pontos, dataHora, passageiros, dif.name(), duracao, movimentos);

        saveRanking(entry);
    }

    private static void saveRanking(RankingEntry entry) {
        try (FileWriter writer = new FileWriter(RANKING_FILE, true)) {
            writer.write(entry.toJSON() + "\n");
            System.out.println("Pontuação registrada com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o ranking: " + e.getMessage());
        }
    }

    private static List<RankingEntry> carregarRanking() {
        List<RankingEntry> lista = new ArrayList<>();
        File file = new File(RANKING_FILE);
        if (!file.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String nome = extrairValorJSON(line, "nome");
                int pontos = Integer.parseInt(extrairValorJSON(line, "pontos"));
                String data = extrairValorJSON(line, "dataHora");
                int pass = Integer.parseInt(extrairValorJSON(line, "passageiros"));
                String dif = extrairValorJSON(line, "dificuldade");
                long dur = Long.parseLong(extrairValorJSON(line, "duracao"));
                int mov = Integer.parseInt(extrairValorJSON(line, "movimentos"));

                lista.add(new RankingEntry(nome, pontos, data, pass, dif, dur, mov));
            }
        } catch (Exception e) {
        }
        Collections.sort(lista);
        return lista;
    }

    private static String extrairValorJSON(String json, String chave) {
        String busca = "\"" + chave + "\":";
        int start = json.indexOf(busca);
        if (start == -1) return "0";
        start += busca.length();

        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        } else {
            int end = json.indexOf(',', start);
            if (end == -1) end = json.indexOf('}', start);
            return json.substring(start, end);
        }
    }

    private static int obterMaiorPontuacao() {
        List<RankingEntry> lista = carregarRanking();
        return lista.isEmpty() ? 0 : lista.get(0).getPontos();
    }

    private static void exibirTopRanking() {
        List<RankingEntry> lista = carregarRanking();
        System.out.println("\n=========================================");
        System.out.println("        TOP 5 PILOTOS NO RANKING         ");
        System.out.println("=========================================");

        if (lista.isEmpty()) {
            System.out.println("Nenhum registro encontrado no ranking.");
            return;
        }

        int limite = Math.min(5, lista.size());
        for (int i = 0; i < limite; i++) {
            RankingEntry r = lista.get(i);
            System.out.printf("%dº - %s | Pontos: %d | Dif: %s | Tempo: %ds | Movs: %d\n",
                    (i + 1), r.getNomeJogador(), r.getPontos(), r.getDificuldade(), r.getDuracaoSegundos(), r.getMovimentos());
        }
    }

    private static void resetarRanking() {
        File file = new File(RANKING_FILE);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("\nHistórico de ranking apagado com sucesso!");
            } else {
                System.out.println("\nFalha ao apagar o arquivo de ranking.");
            }
        } else {
            System.out.println("\nNão existe histórico de ranking para apagar.");
        }
    }
}