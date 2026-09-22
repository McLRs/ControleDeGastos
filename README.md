# Controle de Gastos

Aplicativo Android para acompanhar os gastos pessoais do mês: uma lista com
todos os gastos (com filtro por categoria e total somado) e uma tela de
detalhe com as informações completas de cada gasto.

Esta entrega corresponde à etapa **Parcial** do trabalho (Android Views/XML +
navegação com Intent). A Etapa 2 (Jetpack Compose, Room, ViewModel etc.) será
adicionada depois, na entrega correspondente.

## Objetivo do aplicativo

Ajudar a pessoa a visualizar rapidamente quanto já gastou no mês, separado
por categoria (Alimentação, Transporte, Moradia, Lazer, Saúde, Outros), e
consultar os detalhes de um gasto específico (valor, data, forma de
pagamento e uma observação opcional).

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
`app/src/main/java/com/mcl/controledegastos/data/GastoMockData.kt`.

## Estrutura do projeto

```
app/src/main/java/com/mcl/controledegastos/
├── model/      -> Gasto (data class imutável) e os enums CategoriaGasto e FormaPagamento
├── data/       -> GastoMockData: lista de gastos simulados
├── util/       -> formatação de valores em reais (paraMoeda)
├── ui/         -> mapeamento de categoria -> cor/ícone (separado do modelo)
├── ui/list/    -> ListaGastosActivity (Tela 1) + GastoAdapter (RecyclerView)
└── ui/detail/  -> DetalheGastoActivity (Tela 2)
```

## Principais decisões técnicas

- **ViewBinding** (não `findViewById`) é usado em todas as telas e no item
  da lista, configurado em `app/build.gradle.kts` com `viewBinding = true`.
- **Duas telas** em Views/XML: `ListaGastosActivity` (lista + filtro por
  categoria) e `DetalheGastoActivity` (detalhe de um gasto), conectadas por
  uma **Intent explícita**. O objeto `Gasto` selecionado é passado como
  extra da Intent (implementa `Serializable`) e recuperado do lado da
  `DetalheGastoActivity` com `IntentCompat.getSerializableExtra`.
- **Duas interações** que atualizam a interface/avançam o fluxo:
  - tocar em um chip de categoria filtra a lista e recalcula o total
    exibido, sem trocar de tela;
  - tocar em um item da lista abre a tela de detalhe (Intent).
- **Modelo imutável**: `Gasto` é uma `data class` só com `val`. O campo
  `observacao: String?` é opcional — quando é `null`, a seção
  "Observação" da tela de detalhe fica com `visibility = GONE`.
- **Componentes XML reutilizáveis**: `chip_categoria.xml` é inflado
  dinamicamente uma vez para cada categoria (`LayoutInflater` +
  `ChipCategoriaBinding.inflate`), e `divisor.xml` é reaproveitado via
  `<include>`.
- **Views e ViewGroups usados**: `LinearLayout`, `FrameLayout`,
  `HorizontalScrollView` e `RecyclerView` como containers; `TextView`,
  `ImageView` e `Space` como elementos de conteúdo/espaçamento.
- **Dados simulados**: `GastoMockData` fornece 10 gastos de exemplo, sem
  API nem banco de dados (será adicionado na Etapa 2).

## Bibliotecas externas utilizadas

| Biblioteca | Para que serve |
|---|---|
| `androidx.core:core-ktx` | Extensões Kotlin para APIs do Android; usada aqui principalmente pelo `IntentCompat`, que lê o extra da Intent de forma segura entre versões do Android. |
| `androidx.appcompat:appcompat` | Fornece a `AppCompatActivity` e compatibilidade de temas Material em versões mais antigas do Android. |
| `com.google.android.material:material` | Tema `Theme.MaterialComponents` usado pelo app. |
| `androidx.recyclerview:recyclerview` | Lista rolável e eficiente (`RecyclerView`) usada na tela de gastos. |

Nenhuma biblioteca de rede, banco de dados ou chave de API é usada nesta
etapa — os dados são todos simulados em código.
