import { ValidationError } from '../errors/ValidationError.js';

/**
 * Validador estrito de payload de inferência (RF6).
 * Garante que todos os campos obrigatórios e estruturas geométricas obedeçam ao contrato da API.
 */
export function validateInferencePayload(payload) {
  if (!payload || typeof payload !== 'object' || Array.isArray(payload)) {
    throw new ValidationError('O corpo da requisição deve ser um objeto JSON válido.');
  }

  const errors = [];

  validateSessionMetadata(payload, errors);
  validateImageDimensions(payload.dimensao_imagem, errors);
  validateBoundingBoxes(payload.caixas_delimitadoras, payload.total_objetos, errors);

  if (errors.length > 0) {
    throw new ValidationError('Falha na validação do payload de inferência.', errors);
  }

  return payload;
}

function validateSessionMetadata(payload, errors) {
  if (!payload.id_sessao || typeof payload.id_sessao !== 'string') {
    errors.push('id_sessao é obrigatório e deve ser uma string única.');
  }

  if (!payload.nome_modelo || typeof payload.nome_modelo !== 'string') {
    errors.push('nome_modelo é obrigatório e deve ser uma string.');
  }

  if (!payload.data_hora || typeof payload.data_hora !== 'string') {
    errors.push('data_hora é obrigatório e deve ser uma string com timestamp ISO 8601.');
  }

  if (typeof payload.tempo_execucao_ms !== 'number' || payload.tempo_execucao_ms < 0) {
    errors.push('tempo_execucao_ms deve ser um número positivo.');
  }

  if (typeof payload.limiar_confianca_utilizado !== 'number' ||
      payload.limiar_confianca_utilizado < 0 ||
      payload.limiar_confianca_utilizado > 1) {
    errors.push('limiar_confianca_utilizado deve ser um número entre 0.0 e 1.0.');
  }

  if (typeof payload.total_objetos !== 'number' || payload.total_objetos < 0) {
    errors.push('total_objetos deve ser um número inteiro maior ou igual a zero.');
  }
}

function validateImageDimensions(dimensions, errors) {
  if (!dimensions || typeof dimensions !== 'object') {
    errors.push('dimensao_imagem é obrigatório e deve conter largura e altura.');
    return;
  }

  if (typeof dimensions.largura !== 'number' || dimensions.largura <= 0) {
    errors.push('dimensao_imagem.largura deve ser um número positivo.');
  }

  if (typeof dimensions.altura !== 'number' || dimensions.altura <= 0) {
    errors.push('dimensao_imagem.altura deve ser um número positivo.');
  }
}

function validateBoundingBoxes(boxes, declaredTotal, errors) {
  if (!Array.isArray(boxes)) {
    errors.push('caixas_delimitadoras deve ser uma lista (array) de detecções.');
    return;
  }

  if (typeof declaredTotal === 'number' && boxes.length !== declaredTotal) {
    errors.push(`A contagem em caixas_delimitadoras (${boxes.length}) diverge de total_objetos (${declaredTotal}).`);
  }

  boxes.forEach((box, index) => {
    validateSingleBoundingBox(box, index, errors);
  });
}

function validateSingleBoundingBox(box, index, errors) {
  const prefix = `caixas_delimitadoras[${index}]`;

  if (!box || typeof box !== 'object') {
    errors.push(`${prefix} deve ser um objeto válido.`);
    return;
  }

  if (typeof box.id_caixa !== 'number') {
    errors.push(`${prefix}.id_caixa deve ser um identificador numérico.`);
  }

  if (!box.classe || typeof box.classe !== 'string') {
    errors.push(`${prefix}.classe deve ser uma string.`);
  }

  if (typeof box.confianca !== 'number' || box.confianca < 0 || box.confianca > 1) {
    errors.push(`${prefix}.confianca deve ser um número decimal entre 0.0 e 1.0.`);
  }

  validateCoordinates(box.coordenadas, prefix, errors);
  validateMetrics(box.metricas_geometricas, prefix, errors);
}

function validateCoordinates(coords, prefix, errors) {
  if (!coords || typeof coords !== 'object') {
    errors.push(`${prefix}.coordenadas deve ser um objeto com x_min, y_min, x_max e y_max.`);
    return;
  }

  ['x_min', 'y_min', 'x_max', 'y_max'].forEach((key) => {
    if (typeof coords[key] !== 'number') {
      errors.push(`${prefix}.coordenadas.${key} deve ser numérico.`);
    }
  });

  if (typeof coords.x_min === 'number' && typeof coords.x_max === 'number' && coords.x_min > coords.x_max) {
    errors.push(`${prefix}.coordenadas.x_min não pode ser maior que x_max.`);
  }

  if (typeof coords.y_min === 'number' && typeof coords.y_max === 'number' && coords.y_min > coords.y_max) {
    errors.push(`${prefix}.coordenadas.y_min não pode ser maior que y_max.`);
  }
}

function validateMetrics(metrics, prefix, errors) {
  if (!metrics || typeof metrics !== 'object') {
    errors.push(`${prefix}.metricas_geometricas deve ser um objeto com largura_px, altura_px, centroide e area_px2.`);
    return;
  }

  if (typeof metrics.largura_px !== 'number' || metrics.largura_px <= 0) {
    errors.push(`${prefix}.metricas_geometricas.largura_px deve ser positivo.`);
  }

  if (typeof metrics.altura_px !== 'number' || metrics.altura_px <= 0) {
    errors.push(`${prefix}.metricas_geometricas.altura_px deve ser positivo.`);
  }

  if (typeof metrics.area_px2 !== 'number' || metrics.area_px2 <= 0) {
    errors.push(`${prefix}.metricas_geometricas.area_px2 deve ser positivo.`);
  }

  if (!metrics.centroide || typeof metrics.centroide !== 'object' ||
      typeof metrics.centroide.x !== 'number' || typeof metrics.centroide.y !== 'number') {
    errors.push(`${prefix}.metricas_geometricas.centroide deve conter coordenadas x e y numéricas.`);
  }
}
