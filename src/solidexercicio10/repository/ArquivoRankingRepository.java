package solidexercicio10.repository;

import solidexercicio10.model.RankingEntry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArquivoRankingRepository implements RankingRepository {
    private final Path arquivo;

    public ArquivoRankingRepository(String caminho) {
        this.arquivo = Paths.get(caminho);
    }

    @Override
    public void salvar(RankingEntry e) {
        String json = String.format(
                "{\"nome\":\"%s\",\"pontos\":%d,\"dataHora\":\"%s\",\"passageiros\":%d,\"dificuldade\":\"%s\",\"duracao\":%d,\"movimentos\":%d}%n",
                e.getNomeJogador().replace("\"", ""), e.getPontos(), e.getDataHora(), e.getPassageirosColetados(),
                e.getDificuldade(), e.getDuracaoSegundos(), e.getMovimentos());
        try {
            Files.write(arquivo, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public List<RankingEntry> listarOrdenado() {
        List<RankingEntry> lista = new ArrayList<>();
        if (!Files.exists(arquivo)) return lista;
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                if (linha.trim().isEmpty()) continue;
                try {
                    lista.add(new RankingEntry(
                            valor(linha, "nome"),
                            Integer.parseInt(valor(linha, "pontos")),
                            valor(linha, "dataHora"),
                            Integer.parseInt(valor(linha, "passageiros")),
                            valor(linha, "dificuldade"),
                            Long.parseLong(valor(linha, "duracao")),
                            Integer.parseInt(valor(linha, "movimentos"))));
                } catch (RuntimeException ignorada) {
                }
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        Collections.sort(lista);
        return lista;
    }

    @Override
    public boolean resetar() {
        try {
            return Files.deleteIfExists(arquivo);
        } catch (IOException ex) {
            return false;
        }
    }

    private static String valor(String json, String chave) {
        String busca = "\"" + chave + "\":";
        int inicio = json.indexOf(busca);
        if (inicio == -1) return "0";
        inicio += busca.length();
        if (json.charAt(inicio) == '"') {
            return json.substring(inicio + 1, json.indexOf('"', inicio + 1));
        }
        int fim = json.indexOf(',', inicio);
        if (fim == -1) fim = json.indexOf('}', inicio);
        return json.substring(inicio, fim);
    }
}
