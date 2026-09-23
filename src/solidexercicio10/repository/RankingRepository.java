package solidexercicio10.repository;

import solidexercicio10.model.RankingEntry;

import java.util.List;

public interface RankingRepository {
    void salvar(RankingEntry entry);
    List<RankingEntry> listarOrdenado();
    boolean resetar();
}
