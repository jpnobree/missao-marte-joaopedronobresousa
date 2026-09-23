package solidexercicio10.service;

public enum ResultadoTurno {
    CONTINUA,
    RESGATE,
    TODOS_A_BORDO,
    COLISAO,
    NAVE_DESTRUIDA,
    SEM_COMBUSTIVEL,
    VITORIA;

    public boolean encerraMissao() {
        return this == NAVE_DESTRUIDA || this == SEM_COMBUSTIVEL || this == VITORIA;
    }
}
