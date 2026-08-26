public enum Dificuldade {
    FACIL(5, 30, 5),
    MEDIO(3, 20, 5),
    DIFICIL(6, 15, 5);

    private final int qtdAsteroides;
    private final int pontosIniciais;
    private final int capacidadeNave;

    Dificuldade(int qtdAsteroides, int pontosIniciais, int capacidadeNave) {
        this.qtdAsteroides = qtdAsteroides;
        this.pontosIniciais = pontosIniciais;
        this.capacidadeNave = capacidadeNave;
    }

    public int getQtdAsteroides() { return qtdAsteroides; }
    public int getPontosIniciais() { return pontosIniciais; }
    public int getCapacidadeNave() { return capacidadeNave; }
}
