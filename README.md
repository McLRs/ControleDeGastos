# Controle de Gastos

Aplicativo Android para acompanhar os gastos pessoais mês a mês: uma lista
de gastos (com filtro por categoria, navegação entre meses e total somado),
uma tela de detalhe de cada gasto (com opção de excluir) e uma tela para
cadastrar uma despesa nova — inclusive despesas parceladas.

Esta entrega corresponde à etapa **Parcial** do trabalho (Android Views/XML +
navegação com Intent). A Etapa 2 (Jetpack Compose, Room, ViewModel etc.) será
adicionada depois, na entrega correspondente — por isso os dados ainda vivem
em memória (num repositório simples), sem banco de dados nesta etapa.

## Objetivo do aplicativo

Ajudar a pessoa a visualizar rapidamente quanto já gastou em cada mês,
separado por categoria (Alimentação, Transporte, Moradia, Lazer, Saúde,
Outros); cadastrar um gasto novo (à vista ou parcelado); excluir um gasto
que não deveria estar ali; e consultar os detalhes de um gasto específico
(valor, data, forma de pagamento, parcela e uma observação opcional).

## Como rodar o projeto localmente

1. Abra o Android Studio (versão atual, com o Android SDK já instalado).
2. Em **File > Open**, selecione a pasta raiz deste projeto
   (`ControleDeGastos`).
3. Aguarde o Gradle sincronizar (o Android Studio baixa automaticamente
   qualquer versão do Gradle/SDK que ainda não esteja instalada, desde que
   haja conexão com a internet).
4. Escolha um emulador ou dispositivo físico com **Android 8.0 (API 26)**
   ou superior.
5. Clique em **Run ▶** (ou `Shift+F10`).

Não é necessária nenhuma chave de API, senha ou arquivo `.env`: todos os
dados exibidos são simulados (mock), definidos em
`app/src/main/java/com/mcl/controledegastos/data/GastoMockData.kt`, e as
datas desses gastos de exemplo são calculadas em relação ao dia em que o
app é aberto — assim a lista sempre tem gastos para mostrar no mês atual,
não importa em que data o projeto for avaliado.

## Estrutura do projeto

```
app/src/main/java/com/mcl/controledegastos/
├── model/      -> Gasto (data class imutável) e os enums CategoriaGasto e FormaPagamento
├── data/       -> GastoMockData (dados simulados) e GastoRepository (guarda os gastos em memória)
├── util/       -> formatação de valores em reais (paraMoeda) e de datas (formatadorDeData)
├── ui/         -> mapeamento de categoria -> cor/ícone (separado do modelo)
├── ui/list/    -> ListaGastosActivity (Tela 1) + GastoAdapter (RecyclerView)
├── ui/detail/  -> DetalheGastoActivity (Tela 2)
└── ui/add/     -> NovaDespesaActivity (Tela 3: cadastro de despesa, com ou sem parcelamento)
```

## Funcionalidades

- **Listar gastos do mês**, com total somado no topo.
- **Navegar entre meses** pelas setas do cabeçalho — uma compra parcelada
  cai em meses seguintes, e é assim que as próximas parcelas aparecem.
- **Filtrar por categoria** tocando nos chips (não muda de tela).
- **Adicionar um gasto novo** pelo botão "+" (canto inferior direito):
  descrição, valor, categoria, forma de pagamento, data (seletor de data) e
  observação opcional. Tem a opção **"Compra parcelada?"**: quando marcada,
  o valor informado é tratado como o valor **total** da compra, dividido em
  partes iguais entre o número de parcelas informado — uma parcela por mês,
  a partir da data escolhida.
- **Excluir um gasto**: arrastando o item da lista para o lado (com opção de
  desfazer pelo aviso que aparece embaixo) ou pelo botão "Excluir gasto" na
  tela de detalhe (com confirmação antes de excluir de verdade).
- **Ver o detalhe de um gasto** tocando nele na lista.

## Principais decisões técnicas

- **ViewBinding** (não `findViewById`) é usado em todas as telas e nos itens
  inflados dinamicamente, configurado em `app/build.gradle.kts` com
  `viewBinding = true`.
- **Três telas** em Views/XML — `ListaGastosActivity` (lista + filtro +
  navegação por mês), `NovaDespesaActivity` (cadastro) e
  `DetalheGastoActivity` (detalhe + exclusão) — conectadas por **Intents
  explícitas**. O objeto `Gasto` selecionado na lista é passado como extra
  da Intent (implementa `Serializable`) e recuperado do lado da
  `DetalheGastoActivity` com `IntentCompat.getSerializableExtra`. Já a tela
  de nova despesa não precisa devolver nada pela Intent: ela grava
  diretamente no `GastoRepository`, e a lista recarrega os dados no
  `onResume()`.
- **`GastoRepository`**: um `object` (singleton) que guarda a lista de
  gastos na memória enquanto o app está aberto. É o mais simples possível
  de propósito — ainda não é um banco de dados (isso é assunto da Etapa 2,
  com Room) — mas já resolve adicionar/remover gastos com uma única fonte
  de verdade compartilhada entre as telas.
- **Modelo imutável**: `Gasto` é uma `data class` só com `val`. O campo
  `observacao: String?` é opcional (`null` quando não há observação).
  `parcelaAtual` e `totalParcelas` (ambos com valor padrão `1`) descrevem
  uma compra parcelada: uma compra em 6x vira 6 objetos `Gasto`, um por mês,
  cada um com sua fração do valor total.
- **Divisão de valores em centavos**: ao parcelar uma compra, o valor total
  é convertido para centavos (`Long`) antes de dividir pelo número de
  parcelas, e o resto da divisão é distribuído nas primeiras parcelas — evita
  o erro clássico de arredondamento de `Double` (ex.: R$ 100,00 em 3x vira
  R$ 33,34 + R$ 33,33 + R$ 33,33, e não R$ 33,33 vezes três, que sobraria um
  centavo).
- **Excluir com Snackbar de desfazer**: arrastar um item da lista (via
  `ItemTouchHelper`) remove o gasto do repositório na hora, mas mostra um
  `Snackbar` com a ação "Desfazer" por alguns segundos, que devolve o gasto
  removido caso o usuário tenha arrastado sem querer.
- **Componentes XML reutilizáveis**: `chip_categoria.xml` é inflado
  dinamicamente uma vez para cada categoria (`LayoutInflater` +
  `ChipCategoriaBinding.inflate`), e `divisor.xml` é reaproveitado via
  `<include>`.
- **Views e ViewGroups usados**: `LinearLayout`, `FrameLayout`, `ScrollView`,
  `HorizontalScrollView` e `RecyclerView` como containers; `TextView`,
  `ImageView`, `Spinner`, `Space` e os componentes do Material Design
  (`TextInputLayout`, `SwitchMaterial`, `MaterialButton`,
  `FloatingActionButton`) como elementos de conteúdo/entrada.
- **Dados simulados**: `GastoMockData` fornece gastos de exemplo (incluindo
  um já parcelado, para mostrar a funcionalidade assim que o app abre), sem
  API nem banco de dados (será adicionado na Etapa 2).

## Bibliotecas externas utilizadas

| Biblioteca | Para que serve |
|---|---|
| `androidx.core:core-ktx` | Extensões Kotlin para APIs do Android; usada aqui principalmente pelo `IntentCompat`, que lê o extra da Intent de forma segura entre versões do Android. |
| `androidx.appcompat:appcompat` | Fornece a `AppCompatActivity`, `AlertDialog` (confirmação de exclusão) e compatibilidade de temas Material em versões mais antigas do Android. |
| `com.google.android.material:material` | Tema `Theme.MaterialComponents` e os componentes de UI da tela de nova despesa (`TextInputLayout`, `SwitchMaterial`, `MaterialButton`, `FloatingActionButton`, `Snackbar`). |
| `androidx.recyclerview:recyclerview` | Lista rolável e eficiente (`RecyclerView`) usada na tela de gastos, com `ItemTouchHelper` para o gesto de arrastar-para-excluir. |

Nenhuma biblioteca de rede, banco de dados ou chave de API é usada nesta
etapa — os dados são todos simulados/guardados em memória em código.
