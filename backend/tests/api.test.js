import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { createApp } from '../src/app.js';
import { InferenceRepository } from '../src/repositories/inferenceRepository.js';
import { InferenceService } from '../src/services/inferenceService.js';
import { InferenceController } from '../src/controllers/inferenceController.js';
import express from 'express';
import { inferenceRouter } from '../src/routes/inferenceRoutes.js';
import { errorHandler } from '../src/middlewares/errorHandler.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const testStoragePath = path.join(__dirname, 'test-inferences.json');

const samplePayload = {
  id_sessao: 'test-session-001',
  nome_modelo: 'tilapia.onnx',
  data_hora: '2026-10-21T18:00:00Z',
  tempo_execucao_ms: 55.2,
  dimensao_imagem: {
    largura: 1920,
    altura: 1080
  },
  total_objetos: 1,
  limiar_confianca_utilizado: 0.25,
  caixas_delimitadoras: [
    {
      id_caixa: 1,
      classe: 'tilapia',
      confianca: 0.94,
      coordenadas: {
        x_min: 150.0,
        y_min: 200.0,
        x_max: 300.0,
        y_max: 350.0
      },
      metricas_geometricas: {
        largura_px: 150.0,
        altura_px: 150.0,
        centroide: {
          x: 225.0,
          y: 275.0
        },
        area_px2: 22500.0
      }
    }
  ]
};

async function setupTestServer() {
  await fs.rm(testStoragePath, { force: true });
  const repo = new InferenceRepository(testStoragePath);
  await repo.init();
  const service = new InferenceService(repo);
  const controller = new InferenceController(service);

  const app = express();
  app.use(express.json());

  app.get('/health', (req, res) => res.status(200).json({ status: 'UP' }));

  const router = express.Router();
  router.get('/stats/analytics', controller.stats);
  router.post('/', controller.create);
  router.get('/', controller.list);
  router.get('/:id', controller.getById);

  app.use('/api/inferencias', router);
  app.use(errorHandler);

  const server = app.listen(0);
  const port = server.address().port;
  const baseUrl = `http://localhost:${port}`;

  return {
    server,
    baseUrl,
    cleanup: async () => {
      await new Promise((resolve) => server.close(resolve));
      await fs.rm(testStoragePath, { force: true });
    }
  };
}

test('API E2E: GET /health retorna status 200 e UP', async () => {
  const { baseUrl, cleanup } = await setupTestServer();
  try {
    const res = await fetch(`${baseUrl}/health`);
    assert.equal(res.status, 200);
    const body = await res.json();
    assert.equal(body.status, 'UP');
  } finally {
    await cleanup();
  }
});

test('API E2E: POST /api/inferencias cadastra sessão com 201 Created', async () => {
  const { baseUrl, cleanup } = await setupTestServer();
  try {
    const res = await fetch(`${baseUrl}/api/inferencias`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(samplePayload)
    });

    assert.equal(res.status, 201);
    const body = await res.json();
    assert.equal(body.sucesso, true);
    assert.equal(body.dados.id_sessao, 'test-session-001');
    assert.ok(body.dados.recebido_em);
  } finally {
    await cleanup();
  }
});

test('API E2E: GET /api/inferencias lista histórico ordenado', async () => {
  const { baseUrl, cleanup } = await setupTestServer();
  try {
    // Insere sessão
    await fetch(`${baseUrl}/api/inferencias`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(samplePayload)
    });

    const res = await fetch(`${baseUrl}/api/inferencias`);
    assert.equal(res.status, 200);
    const body = await res.json();
    assert.equal(body.sucesso, true);
    assert.equal(body.total, 1);
    assert.equal(body.dados[0].id_sessao, 'test-session-001');
  } finally {
    await cleanup();
  }
});

test('API E2E: GET /api/inferencias/:id retorna detalhes da sessão ou 404', async () => {
  const { baseUrl, cleanup } = await setupTestServer();
  try {
    // Insere sessão
    await fetch(`${baseUrl}/api/inferencias`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(samplePayload)
    });

    // Busca com sucesso
    const resFound = await fetch(`${baseUrl}/api/inferencias/test-session-001`);
    assert.equal(resFound.status, 200);
    const bodyFound = await resFound.json();
    assert.equal(bodyFound.dados.id_sessao, 'test-session-001');

    // Busca inexistente
    const resNotFound = await fetch(`${baseUrl}/api/inferencias/id-inexistente`);
    assert.equal(resNotFound.status, 404);
    const bodyNotFound = await resNotFound.json();
    assert.equal(bodyNotFound.sucesso, false);
  } finally {
    await cleanup();
  }
});

test('API E2E: POST /api/inferencias com dados inválidos retorna 400 Bad Request', async () => {
  const { baseUrl, cleanup } = await setupTestServer();
  try {
    const res = await fetch(`${baseUrl}/api/inferencias`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id_sessao: 'incompleto' })
    });

    assert.equal(res.status, 400);
    const body = await res.json();
    assert.equal(body.sucesso, false);
    assert.ok(Array.isArray(body.detalhes));
  } finally {
    await cleanup();
  }
});

test('API E2E: GET /api/inferencias/stats/analytics calcula agregações corretas', async () => {
  const { baseUrl, cleanup } = await setupTestServer();
  try {
    await fetch(`${baseUrl}/api/inferencias`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(samplePayload)
    });

    const res = await fetch(`${baseUrl}/api/inferencias/stats/analytics`);
    assert.equal(res.status, 200);
    const body = await res.json();
    assert.equal(body.sucesso, true);
    assert.equal(body.dados.total_sessoes, 1);
    assert.equal(body.dados.total_objetos_detectados, 1);
    assert.equal(body.dados.tempo_medio_execucao_ms, 55.2);
  } finally {
    await cleanup();
  }
});
