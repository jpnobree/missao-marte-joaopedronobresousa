# Revisão SOLID: `solidexercicio10`

O arquivo `src/README.md` (tutorial) não estava no repositório. A refatoração segue as camadas descritas no enunciado (`model`, `service`, `presentation`, `repository` e `Main`). O código original em `src/*.java` não foi alterado.

## 1. Observações por princípio

### S: Responsabilidade Única

```
Local: solidexercicio10.Main / service.PartidaService / presentation.JogoConsole / repository.ArquivoRankingRepository
Princípio relacionado: SRP
Observação: o Main original (285 linhas) misturava menu, loop do jogo, regras, estatísticas e leitura/escrita de JSON. Agora cada parte tem uma classe: o Main só monta as dependências, o PartidaService decide o resultado de um turno, o JogoConsole lê o teclado e imprime, e o ArquivoRankingRepository cuida do arquivo.
Impacto para manutenção, testes ou evolução: mudar o formato do arquivo ou uma mensagem da tela não mexe na regra do turno. O PartidaService pode ser testado sem console.
Proposta: manter como está.
Prioridade: baixa
```

```
Local: presentation.JogoConsole
Princípio relacionado: SRP
Observação: o JogoConsole ainda acumula menu principal, seleção de dificuldade, loop da partida, mensagens de resultado e tela de ranking (cerca de 170 linhas).
Impacto para manutenção, testes ou evolução: por enquanto dá para ler, mas cada nova tela deixa a classe maior.
Proposta: separar uma classe `MenuConsole` se surgirem novas telas. Hoje a divisão seria prematura.
Prioridade: baixa
```

### O: Aberto/Fechado

```
Local: model.Passageiro e subclasses; presentation.MapaRenderer
Princípio relacionado: OCP
Observação: para criar um novo tipo de passageiro (ex.: Medico), basta uma nova subclasse com getPontuacao(). PartidaService, Missao e MapaRenderer não mudam, porque tratam tudo como Passageiro/Posicionavel.
Impacto para manutenção, testes ou evolução: um novo tipo não gera efeitos colaterais no fluxo.
Proposta: nenhuma.
Prioridade: baixa
```

```
Local: service.PartidaService.novaMissao
Princípio relacionado: OCP
Observação: a posição dos 5 passageiros e do inimigo está fixa no código, como no original. Um novo "cenário" exige editar o método.
Impacto para manutenção, testes ou evolução: com um único mapa, isso não pesa. Com várias fases, o método cresceria.
Proposta: receber a lista de passageiros por parâmetro ou por um `Supplier<List<Passageiro>>` quando houver mais de um cenário.
Prioridade: média
```

### L: Substituição de Liskov

```
Local: model.Passageiro, Professor, Engenheiro, Astronauta
Princípio relacionado: LSP
Observação: no original, Passageiro era concreta, com pontuação padrão 10, e Professor sobrescrevia com o mesmo valor. Agora Passageiro é abstrata e cada subclasse só fornece o bônus, sem alterar outro contrato (posição, nome). Missao.resgatarNaPosicaoDaNave usa apenas a abstração.
Impacto para manutenção, testes ou evolução: qualquer subclasse funciona no lugar da base. O teste "LSP: subclasses usadas como Passageiro" soma a pontuação de todos pela referência base.
Proposta: nenhuma.
Prioridade: baixa
```

### I: Segregação de Interfaces

```
Local: model.Posicionavel e model.Movel
Princípio relacionado: ISP
Observação: a posição (Posicionavel) e o movimento (Movel) ficaram separados. Asteroide só é Posicionavel e não precisa de um mover() vazio. O MapaRenderer depende só de Posicionavel.
Impacto para manutenção, testes ou evolução: nenhum cliente é obrigado a implementar métodos que não usa.
Proposta: nenhuma. Porém, ver discordância D1: nenhum cliente usa Movel de forma polimórfica.
Prioridade: baixa
```

```
Local: repository.RankingRepository
Princípio relacionado: ISP
Observação: a interface tem 3 métodos (salvar, listarOrdenado, resetar), e o PartidaService usa os três. Dividir em leitura e escrita não traria ganho.
Impacto para manutenção, testes ou evolução: um fake em memória (no teste) é criado com uma classe anônima de poucas linhas.
Proposta: manter.
Prioridade: baixa
```

### D: Inversão de Dependência

```
Local: service.PartidaService (construtor) e solidexercicio10.Main
Princípio relacionado: DIP
Observação: PartidaService recebe RankingRepository e Random pelo construtor. Só o Main conhece ArquivoRankingRepository e o nome "ranking.json".
Impacto para manutenção, testes ou evolução: o teste usa um repositório em memória e um Random com semente fixa. Trocar o JSON por um banco de dados não muda o serviço.
Proposta: nenhuma.
Prioridade: baixa
```

## 2. Melhorias adicionais

```
Local: repository.ArquivoRankingRepository (valor / salvar)
Princípio relacionado: SRP (robustez)
Observação: o "parser" JSON é feito à mão com indexOf, como no original. Um nome com vírgula ou aspas quebra a leitura (as aspas são removidas ao salvar como mitigação). O ranking.json existente tem uma primeira linha mista ([{"name"...}]{"nome"...}) que só é lida por sorte.
Impacto para manutenção, testes ou evolução: há risco de perder registros silenciosamente, já que linhas inválidas são ignoradas.
Proposta: usar um formato sem ambiguidade (CSV com escape ou java.util.Properties) ou uma biblioteca JSON, caso o projeto adote Maven/Gradle.
Prioridade: alta
```

```
Local: presentation.JogoConsole.lerInteiroPositivo
Princípio relacionado: não se aplica (correção)
Observação: no original, digitar um texto no tamanho do mapa derrubava o programa (NumberFormatException). Agora a entrada é validada.
Impacto para manutenção, testes ou evolução: o jogo não fecha por erro de digitação.
Proposta: já implementado.
Prioridade: alta
```

```
Local: service.PartidaService.jogarTurno
Princípio relacionado: não se aplica (regra de jogo)
Observação: no original, uma colisão na fase de pouso levava a nave para (0,0), e a mesma rodada já contava como pouso bem-sucedido (colidir era um "atalho" para vencer). Na refatoração, a colisão encerra o turno com COLISAO. Isso é uma mudança intencional de comportamento.
Impacto para manutenção, testes ou evolução: a regra fica coerente, e o teste "colisao volta para base" cobre o caso.
Proposta: confirmar com a equipe se o atalho era desejado.
Prioridade: média
```

```
Local: model.Missao.getDuracaoSegundos
Princípio relacionado: DIP
Observação: usa System.currentTimeMillis() diretamente, então a duração não pode ser testada de forma determinística.
Impacto para manutenção, testes ou evolução: é pequeno, porque a duração só aparece nas estatísticas.
Proposta: injetar java.time.Clock se a duração passar a valer pontos.
Prioridade: baixa
```

## 3. Decisão do tutorial com a qual concordamos

```
Local: repository.RankingRepository + ArquivoRankingRepository
Princípio relacionado: DIP / SRP
Observação: separar o contrato do ranking da implementação em arquivo foi a decisão de maior retorno. Era a parte mais acoplada do original (I/O dentro do Main) e a única dependência externa real (disco).
Impacto para manutenção, testes ou evolução: o teste do serviço roda sem tocar no disco, e a persistência pode ser trocada sem mexer no jogo.
Proposta: manter.
Prioridade: baixa
```

## 4. Decisões das quais discordamos ou faríamos diferente

```
Local (D1): model.Movel
Princípio relacionado: ISP / YAGNI
Observação: Movel foi criada porque o tutorial/enunciado pede, mas nenhum código trata Nave e Inimigo de forma polimórfica como Movel. A nave é movida pelo jogador e o inimigo por moverAleatorio, com regras diferentes.
Impacto para manutenção, testes ou evolução: é uma abstração sem cliente, que custa pouco mas não resolve nenhum problema concreto hoje.
Proposta: remover Movel até existir um laço do tipo "mover todos os móveis" ou um novo tipo de entidade móvel.
Prioridade: baixa
```

```
Local (D2): camada service para um jogo de console pequeno
Princípio relacionado: SRP
Observação: para ~20 classes, as quatro camadas são um pouco pesadas. Parte das regras (colisão, resgate) ficou em Missao, e o PartidaService apenas orquestra o turno. Uma divisão em model + console + repositório já atenderia o SRP.
Impacto para manutenção, testes ou evolução: há mais arquivos para navegar. Em compensação, o turno inteiro pode ser testado sem Scanner, e por isso mantivemos a camada.
Proposta: manter o service, mas não criar interfaces para ele (ex.: IPartidaService), pois haveria só uma implementação.
Prioridade: baixa
```

```
Local (D3): model.RankingEntry no pacote model
Princípio relacionado: SRP
Observação: RankingEntry não é uma entidade do jogo, e sim um registro de persistência. Deixamos no model para que o repository não dependa do service e vice-versa. O método toJSON() saiu da entidade e foi para o repositório.
Impacto para manutenção, testes ou evolução: é neutro.
Proposta: mover para o pacote repository se o ranking crescer (paginação, filtros).
Prioridade: baixa
```

## 5. Testes realizados

Ambiente: OpenJDK 23 compilando com `--release 8` (compatível com o JRE 1.8 da máquina).

| # | Teste | Como | Resultado |
|---|-------|------|-----------|
| 1 | Compilação do pacote `solidexercicio10` | `javac --release 8 -encoding UTF-8 ...` | OK, sem erros |
| 2 | Pontos iniciais por dificuldade | `PartidaServiceTeste` | OK |
| 3 | Movimento e resgate (Astronauta +20) | `PartidaServiceTeste` | OK |
| 4 | LSP: soma de pontuação via `Passageiro` | `PartidaServiceTeste` | OK (60) |
| 5 | Consumo de combustível na fase de pouso | `PartidaServiceTeste` | OK |
| 6 | Vitória ao pousar em (0,0) | `PartidaServiceTeste` | OK |
| 7 | Registro no ranking (repositório fake) | `PartidaServiceTeste` | OK |
| 8 | Colisão devolve a nave à base | `PartidaServiceTeste` | OK |
| 9 | 3 colisões destroem a nave | `PartidaServiceTeste` | OK |
| 10 | Mapa desenha a nave `@` | `PartidaServiceTeste` | OK |
| 11 | Arquivo: ordenação por pontos e reset | `PartidaServiceTeste` (arquivo temporário) | OK |
| 12 | Menu: iniciar missão, piloto, dificuldade, tamanho, mover, desistir, opção inválida, sair | execução com entrada simulada (`printf ... \| java solidexercicio10.Main`) | OK |
| 13 | Ranking Top 5 com o `ranking.json` existente, reset e ranking vazio | execução com entrada simulada, em uma cópia do arquivo | OK: listou Nobre (91) e Joao (65); após o reset, "Nenhum registro encontrado" |

Saída do teste automatizado: `OK: todos os testes passaram` (15 verificações).

## 6. Resumo de prioridades

| Melhoria | Prioridade |
|----------|------------|
| Parser JSON manual / formato frágil do ranking | alta |
| Validação do tamanho do mapa (já feita) | alta |
| Colisão na fase de pouso não conta mais como vitória (confirmar regra) | média |
| Cenário de passageiros fixo em `novaMissao` | média |
| Remover `Movel` sem cliente polimórfico | baixa |
| Injetar `Clock` na duração | baixa |
| Dividir `JogoConsole` se surgirem novas telas | baixa |
| Mover `RankingEntry` para `repository` | baixa |
