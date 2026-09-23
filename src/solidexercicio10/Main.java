package solidexercicio10;

import solidexercicio10.presentation.JogoConsole;
import solidexercicio10.presentation.MapaRenderer;
import solidexercicio10.repository.ArquivoRankingRepository;
import solidexercicio10.service.PartidaService;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PartidaService service = new PartidaService(new ArquivoRankingRepository("ranking.json"), new Random());
        new JogoConsole(new Scanner(System.in), service, new MapaRenderer()).iniciar();
    }
}
