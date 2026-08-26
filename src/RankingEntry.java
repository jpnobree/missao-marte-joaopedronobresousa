public class RankingEntry implements Comparable<RankingEntry> {
    private String nomeJogador;
    private int pontos;
    private String dataHora;
    private int passageirosColetados;
    private String dificuldade;
    private long duracaoSegundos;
    private int movimentos;

    public RankingEntry(String nomeJogador, int pontos, String dataHora, int passageirosColetados, String dificuldade, long duracaoSegundos, int movimentos) {
        this.nomeJogador = nomeJogador;
        this.pontos = pontos;
        this.dataHora = dataHora;
        this.passageirosColetados = passageirosColetados;
        this.dificuldade = dificuldade;
        this.duracaoSegundos = duracaoSegundos;
        this.movimentos = movimentos;
    }

    public String getNomeJogador() { return nomeJogador; }
    public int getPontos() { return pontos; }
    public String getDataHora() { return dataHora; }
    public int getPassageirosColetados() { return passageirosColetados; }
    public String getDificuldade() { return dificuldade; }
    public long getDuracaoSegundos() { return duracaoSegundos; }
    public int getMovimentos() { return movimentos; }

    public String toJSON() {
        return String.format(
                "{\"nome\":\"%s\",\"pontos\":%d,\"dataHora\":\"%s\",\"passageiros\":%d,\"dificuldade\":\"%s\",\"duracao\":%d,\"movimentos\":%d}",
                nomeJogador, pontos, dataHora, passageirosColetados, dificuldade, duracaoSegundos, movimentos
        );
    }

    @Override
    public int compareTo(RankingEntry o) {
        return Integer.compare(o.pontos, this.pontos); // Ordem decrescente de pontos
    }
}