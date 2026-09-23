package solidexercicio10.model;

public abstract class Passageiro extends EntidadeMapa {
    private final String nome;

    protected Passageiro(String nome, int x, int y) {
        super(x, y);
        this.nome = nome;
    }

    public String getNome() { return nome; }

    public abstract int getPontuacao();
}
