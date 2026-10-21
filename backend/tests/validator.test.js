import test from 'node:test';
import assert from 'node:assert/strict';
import { validateInferencePayload } from '../src/validators/inferenceValidator.js';
import { ValidationError } from '../src/errors/ValidationError.js';

const validPayload = {
  id_sessao: '550e8400-e29b-41d4-a716-446655440000',
  nome_modelo: 'tilapia-yolov8n.onnx',
  data_hora: '2026-10-19T19:00:00Z',
  tempo_execucao_ms: 48.5,
  dimensao_imagem: {
    largura: 640,
    altura: 640
  },
  total_objetos: 1,
  limiar_confianca_utilizado: 0.50,
  caixas_delimitadoras: [
    {
      id_caixa: 1,
      classe: 'tilapia',
      confianca: 0.92,
      coordenadas: {
        x_min: 120.0,
        y_min: 180.0,
        x_max: 340.0,
        y_max: 410.0
      },
      metricas_geometricas: {
        largura_px: 220.0,
        altura_px: 230.0,
        centroide: {
          x: 230.0,
          y: 295.0
        },
        area_px2: 50600.0
      }
    }
  ]
};

test('Validator: Aceita payload válido conforme especificação do README', () => {
  const result = validateInferencePayload(validPayload);
  assert.equal(result.id_sessao, '550e8400-e29b-41d4-a716-446655440000');
  assert.equal(result.total_objetos, 1);
});

test('Validator: Rejeita payload vazio ou não-objeto', () => {
  assert.throws(() => validateInferencePayload(null), ValidationError);
  assert.throws(() => validateInferencePayload('invalid string'), ValidationError);
});

test('Validator: Rejeita quando total_objetos diverge da quantidade de caixas', () => {
  const divergentPayload = {
    ...validPayload,
    total_objetos: 5 // Caixas têm 1 item
  };

  assert.throws(() => validateInferencePayload(divergentPayload), (err) => {
    assert(err instanceof ValidationError);
    assert(err.details.some((d) => d.includes('diverge de total_objetos')));
    return true;
  });
});

test('Validator: Rejeita coordenadas invertidas (x_min > x_max)', () => {
  const invertedCoordsPayload = {
    ...validPayload,
    caixas_delimitadoras: [
      {
        ...validPayload.caixas_delimitadoras[0],
        coordenadas: {
          x_min: 400.0,
          y_min: 100.0,
          x_max: 200.0,
          y_max: 300.0
        }
      }
    ]
  };

  assert.throws(() => validateInferencePayload(invertedCoordsPayload), (err) => {
    assert(err instanceof ValidationError);
    assert(err.details.some((d) => d.includes('x_min não pode ser maior que x_max')));
    return true;
  });
});

test('Validator: Rejeita limiar de confiança fora do intervalo 0.0 - 1.0', () => {
  const invalidThresholdPayload = {
    ...validPayload,
    limiar_confianca_utilizado: 1.5
  };

  assert.throws(() => validateInferencePayload(invalidThresholdPayload), ValidationError);
});
