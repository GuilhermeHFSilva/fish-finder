# 🐟 Fish Finder — On-Device Computer Vision & Analytics

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg?logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?logo=android)](https://developer.android.com/jetpack/compose)
[![ONNX Runtime](https://img.shields.io/badge/Inference-ONNX%20Runtime%20Mobile-005CED.svg)](https://onnxruntime.ai/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20Clean-success.svg)](#requisitos-arquiteturais-e-técnicos)
[![Theme](https://img.shields.io/badge/Theme-Light%20Mode-FFA500.svg)](#requisitos-de-interface-uiux)

Aplicativo móvel nativo Android (**Full-Stack Mobile**) voltado para **Visão Computacional e Inferência On-Device** em tempo real, integrado a um servidor backend para persistência de métricas e análises estatísticas de detecções.

---

## 🎯 Objetivo Principal

- **Inferência On-Device**: Desenvolver um aplicativo móvel nativo em **Kotlin** capaz de executar modelos neurais de detecção de objetos (YOLO exportados para ONNX) **100% localmente no dispositivo móvel** (aproveitando CPU, GPU ou NPU), sem dependência de processamento em nuvem.
- **Ecossistema Full-Stack**: Integrar o aplicativo a um **Servidor Backend (API REST)** para envio automático, persistência e auditoria das inferências realizadas, incluindo histórico cronológico e cálculos geométricos precisos de cada detecção.

---

## 📋 Requisitos Funcionais (RFs)

| Identificador | Nome | Descrição |
| :--- | :--- | :--- |
| **RF1** | **Gerenciamento de Modelos ONNX** | O aplicativo deve suportar o carregamento de um modelo ONNX padrão embutido (assets) ou permitir a seleção dinâmica de arquivos `.onnx` diretamente do armazenamento local do dispositivo. |
| **RF2** | **Aquisição de Imagens** | Integração nativa e funcional com a câmera do dispositivo e com a galeria de fotos/mídia do Android. |
| **RF3** | **Ajuste Dinâmico de Parâmetros** | Controle interativo via slider para ajuste em tempo real do Limiar de Confiança (*Confidence Threshold*) e filtro dinâmico de rótulos/classes a serem detectadas. |
| **RF4** | **Pipeline de Inferência Local** | Execução do modelo neural 100% On-Device através do ONNX Runtime, com aceleração de hardware local (CPU / NPU / GPU), garantindo privacidade e baixa latência sem envio da imagem bruta para a nuvem. |
| **RF5** | **Cálculo de Métricas Geométricas** | Para cada caixa delimitadora (*bounding box*) identificada, o aplicativo calcula automaticamente: Largura ($w$), Altura ($h$), Coordenadas do Centróide $(x_c, y_c)$ e Área ($A = w \times h$) em pixels. |
| **RF6** | **Comunicação Backend** | Transmissão automática assíncrona dos resultados em formato JSON via `HTTP POST` para a API REST do servidor backend logo após a conclusão da inferência. |
| **RF7** | **Persistência e Consulta** | O servidor backend deve persistir as informações recebidas (metadados da sessão, tempo de execução, contagem de objetos, data/hora e métricas de cada caixa) em banco de dados relacional/NoSQL ou arquivos JSON estruturados. |
| **RF8** | **Telas de Histórico e Detalhes** | Exibição de histórico cronológico das inferências realizadas com status de sincronização, e tela detalhada contendo a auditoria geométrica individual de cada caixa detectada ao selecionar uma sessão. |

---

## 🏗️ Requisitos Arquiteturais e Técnicos

### Arquitetura Android (RF9)
- **Padrão MVVM (Model-View-ViewModel)** com separação estrita de responsabilidades:
  - **UI Layer (View)**: Composables declarativos observando fluxos de estado imutáveis.
  - **State Holder (ViewModel)**: Gerenciamento do estado da UI via `StateFlow` / `SharedFlow` e orquestração de casos de uso.
  - **Domain / Data Layer**: Repositórios, data sources locais (ONNX Runtime, Room/DataStore) e data source remoto (Retrofit/Ktor).
- **Concorrência & Assincronismo**: Uso intensivo de **Kotlin Coroutines** (`Dispatchers.Default` para inferência e pré/pós-processamento de imagens; `Dispatchers.IO` para chamadas de rede e leitura de arquivos; `Dispatchers.Main` para emissão de estados da UI).
- **Reatividade**: Gerenciamento de estado unidirecional (**UDF - Unidirectional Data Flow**) com `StateFlow`.

### Backend & Integração REST
- **API REST**: Desenvolvida para receber dados de telemetria das inferências e servir consultas históricas.
- **Endpoints Principais**:
  - `POST /api/inferencias` — Registra uma nova sessão de inferência e suas respectivas caixas delimitadoras.
  - `GET /api/inferencias` — Retorna a listagem cronológica das sessões de inferência realizadas.
  - `GET /api/inferencias/{id}` — Retorna os dados detalhados e a lista de caixas/métricas de uma sessão específica.

---

## 📦 Estrutura do Payload JSON

Logo após a inferência local, o aplicativo Android formata e transmite o seguinte payload para o backend:

```json
{
  "id_sessao": "550e8400-e29b-41d4-a716-446655440000",
  "nome_modelo": "tilapia-yolov8n.onnx",
  "data_hora": "2026-10-19T19:00:00Z",
  "tempo_execucao_ms": 48.5,
  "dimensao_imagem": {
    "largura": 640,
    "altura": 640
  },
  "total_objetos": 2,
  "limiar_confianca_utilizado": 0.50,
  "caixas_delimitadoras": [
    {
      "id_caixa": 1,
      "classe": "tilapia",
      "confianca": 0.92,
      "coordenadas": {
        "x_min": 120.0,
        "y_min": 180.0,
        "x_max": 340.0,
        "y_max": 410.0
      },
      "metricas_geometricas": {
        "largura_px": 220.0,
        "altura_px": 230.0,
        "centroide": {
          "x": 230.0,
          "y": 295.0
        },
        "area_px2": 50600.0
      }
    },
    {
      "id_caixa": 2,
      "classe": "tilapia",
      "confianca": 0.86,
      "coordenadas": {
        "x_min": 360.0,
        "y_min": 210.0,
        "x_max": 590.0,
        "y_max": 450.0
      },
      "metricas_geometricas": {
        "largura_px": 230.0,
        "altura_px": 240.0,
        "centroide": {
          "x": 475.0,
          "y": 330.0
        },
        "area_px2": 55200.0
      }
    }
  ]
}
```

---

## 🎨 Requisitos de Interface (UI/UX)

- **Jetpack Compose Obrigatório**: Toda a interface gráfica é construída de forma 100% declarativa com **Jetpack Compose**. É terminantemente vedado o uso de XML Views tradicionais.
- **Tema Exclusivo Light Mode**: Conforme as especificações do projeto, a paleta visual e os componentes adotam estritamente o modo claro (*Light Theme*), garantindo alto contraste e clareza na visualização em campo.
- **Design System Customizado**:
  - Paleta visual aquática moderna e harmoniosa (tons de azul marinho, ciano, superfícies neutras e destaques energéticos para anotação das caixas).
  - Tipografia contemporânea com pesos bem definidos para hierarquia clara.
  - Componentes reutilizáveis customizados (Cards de métricas, Sliders estilizados, Badges de confiança e botões com feedback tátil).
- **Navegação Principal (Bottom Navigation Bar)** com duas abas mestras:
  1. 🔍 **Detectar**: Fluxo de entrada, captura, parametrização e visualização gráfica imediata do resultado.
  2. 📜 **Histórico**: Acesso a sessões passadas sincronizadas e detalhes analíticos.

---

## 📱 Estrutura das 4 Telas Obrigatórias

```
┌────────────────────────────────────────────────────────┐
│                      FISH FINDER                       │
├──────────────────────────┬─────────────────────────────┤
│      Aba: DETECTAR       │       Aba: HISTÓRICO        │
├──────────────────────────┼─────────────────────────────┤
│  [Tela 1] Config & Input │  [Tela 3] Lista Histórico   │
│            ↓             │              ↓              │
│  [Tela 2] Res. Gráfico   │  [Tela 4] Detalhe Geométrico│
└──────────────────────────┴─────────────────────────────┘
```

1. **Tela 1 — Configuração & Entrada:**
   - Seletor de modelo neural (`.onnx` padrão embutido ou seletor de arquivos do sistema).
   - Botões de ação para captura direta via **Câmera** ou importação da **Galeria de Fotos**.
   - Slider dinâmico para configuração do **Limiar de Confiança (Confidence Threshold)** com exibição em tempo real do percentual mínimo.
   - Filtros de classes/rótulos habilitados para a inferência.

2. **Tela 2 — Resultado Gráfico:**
   - Canvas com a imagem adquirida e sobreposição gráfica das **caixas delimitadoras (*bounding boxes*)** coloridas e rotuladas com suas respectivas confianças.
   - Card de resumo exibindo o **Total de Objetos Detectados** e o **Tempo de Execução da Inferência (ms)**.
   - Ações de reprocessamento com novos parâmetros ou visualização das métricas.

3. **Tela 3 — Histórico de Inferências:**
   - Lista cronológica das sessões de inferência com paginação ou *pull-to-refresh*.
   - Cada item apresenta: Data/Hora formatada, Quantidade de peixes/objetos identificados, Nome do modelo utilizado e **Indicador visual de sincronização com o backend** (Enviado / Pendente / Erro).

4. **Tela 4 — Detalhamento Geométrico das Caixas:**
   - Auditoria individual e minuciosa de cada caixa delimitadora detectada na sessão selecionada.
   - Exibição de cards informativos para cada bounding box:
     - **ID da Caixa** e **Classe**
     - **Grau de Confiança (%)** com barra de progresso
     - **Largura ($w$)** e **Altura ($h$)** em pixels
     - **Coordenadas do Centróide $(x_c, y_c)$**
     - **Área total ($w \times h$)** em pixels quadrados

---

## 🧠 Pipeline de Visão Computacional (Modelo de IA)

1. **Dataset**: Dataset público de peixes/tilápias selecionado e estruturado via Roboflow.
2. **Treinamento**: Rede neural baseada na arquitetura YOLO (You Only Look Once) treinada para localização e classificação de instâncias.
3. **Conversão & Otimização**: Exportação do modelo para o formato padronizado **ONNX (Open Neural Network Exchange)**, permitindo inferência ultra-rápida via ONNX Runtime Mobile.
4. **Pré-Processamento**: Normalização de tensores de imagem, redimensionamento com preservação de aspect ratio (letterboxing) para $640 \times 640$.
5. **Pós-Processamento**: Supressão de Não-Máximos (NMS - Non-Maximum Suppression) e cálculo vetorizado das coordenadas e métricas geométricas.

---

## 🛠️ Tecnologias e Bibliotecas

- **Linguagem**: [Kotlin](https://kotlinlang.org/)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Runtime de IA**: [ONNX Runtime Android](https://onnxruntime.ai/docs/get-started/with-java.html)
- **Arquitetura**: MVVM + Clean Architecture principles
- **Assincronismo**: Kotlin Coroutines & Flow / StateFlow
- **Injeção de Dependências**: Hilt / Koin (ou Factory padrão)
- **Cliente HTTP**: Retrofit 2 / Ktor Client com serialização JSON (Kotlinx Serialization / Gson)
- **Câmera**: CameraX / Activity Result API

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Android Studio Ladybug | Hedgehog ou superior.
- JDK 17 ou 21 configurado.
- Dispositivo Android físico ou emulador com Android 8.0+ (API level 26+).

### Passos
1. Clone o repositório:
   ```bash
   git clone https://github.com/GuilhermeHFSilva/fish-finder.git
   cd fish-finder
   ```
2. Abra o projeto no Android Studio.
3. Aguarde o sincronismo do Gradle (`Sync Project with Gradle Files`).
4. Configure a URL base da API do backend no arquivo de configuração do app (`app/src/main/...`).
5. Compile e execute o aplicativo em seu dispositivo ou emulador:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 👥 Equipe e Regras de Entrega

- Projeto desenvolvido para a disciplina de Visão Computacional / Desenvolvimento Mobile.
- Trabalho realizado em equipe de até 4 integrantes.
- **Entregáveis**:
  - Código-fonte completo do Aplicativo Android (este repositório).
  - Repositório do Servidor Backend para persistência dos dados.
  - APK compilado pronto para instalação e testes de campo.
