package solidexercicio10.model;

public class RankingEntry implements Comparable<RankingEntry> {
    private final String nomeJogador;
    private final int pontos;
    private final String dataHora;
    private final int passageirosColetados;
    private final String dificuldade;
    private final long duracaoSegundos;
    private final int movimentos;

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

    @Override
    public int compareTo(RankingEntry o) {
        return Integer.compare(o.pontos, pontos);
    }
}
