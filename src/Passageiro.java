public class Passageiro {
    private String nome;
    private String profissao;
    private int x;
    private int y;

    public Passageiro(String nome, String profissao, int x, int y) {
        this.nome = nome;
        this.profissao = profissao;
        this.x = x;
        this.y = y;
    }

    public String getNome() { return nome; }
    public String getProfissao() { return profissao; }
    public int getX() { return x; }
    public int getY() { return y; }

    public int getPontuacao() {
        return 10; // Default (Exercício 4)
    }
}