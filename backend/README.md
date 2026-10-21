# 🐟 FishFinder — Servidor Backend REST API

API REST desenvolvida em Node.js (ESM) para recepção, validação, persistência estruturada e auditoria histórica das telemetrias de inferência On-Device realizadas pelo aplicativo móvel **FishFinder** (RF6 e RF7).

---

## 🚀 Como Executar

### Pré-requisitos
- **Node.js**: v18+ (testado no Node.js v24.18.0)
- **NPM**: v9+

### Instalação de Dependências
```bash
cd backend
npm install
```

### Iniciar o Servidor
```bash
# Modo padrão (porta 3000)
npm start

# Modo desenvolvimento (hot reload com node --watch)
npm run dev
```

O servidor iniciará em `http://localhost:3000` (ou `http://0.0.0.0:3000`).

---

## 🧪 Como Rodar os Testes Automatizados

Os testes utilizam o test-runner nativo do Node.js (`node:test` e `node:assert`), executando em milissegundos sem dependências pesadas de terceiros:

```bash
npm test
```

---

## 📡 Endpoints da API

### 1. `POST /api/inferencias`
Registra uma nova sessão de inferência e suas caixas delimitadoras calculadas no dispositivo móvel.

* **Status Sucesso:** `201 Created`
* **Status Erro de Validação:** `400 Bad Request`
* **Exemplo de Payload:**
```json
{
  "id_sessao": "sessao_20261021_143000",
  "nome_modelo": "tilapia.onnx",
  "data_hora": "2026-10-21T17:30:00Z",
  "tempo_execucao_ms": 48.5,
  "dimensao_imagem": {
    "largura": 1920,
    "altura": 1080
  },
  "total_objetos": 1,
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
    }
  ]
}
```

### 2. `GET /api/inferencias`
Retorna a lista cronológica decrescente de todas as sessões registradas.

* **Status Sucesso:** `200 OK`

### 3. `GET /api/inferencias/:id`
Retorna os detalhes e as caixas delimitadoras de uma sessão específica.

* **Status Sucesso:** `200 OK`
* **Status Não Encontrado:** `404 Not Found`

### 4. `GET /api/inferencias/stats/analytics`
Retorna métricas consolidadas (total de sessões, total de objetos, tempo médio e confiança média).

### 5. `GET /health`
Healthcheck do serviço (`status: "UP"`).

---

## 💾 Persistência
Os dados são armazenados de forma estruturada e atômica no arquivo:
`backend/data/inferences.json`
