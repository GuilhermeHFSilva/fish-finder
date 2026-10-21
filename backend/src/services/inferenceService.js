import { inferenceRepository } from '../repositories/inferenceRepository.js';
import { validateInferencePayload } from '../validators/inferenceValidator.js';
import { NotFoundError } from '../errors/NotFoundError.js';

/**
 * Serviço de regras de negócio e processamento de telemetria de inferências (RF6 / RF7).
 */
export class InferenceService {
  constructor(repository = inferenceRepository) {
    this.repository = repository;
  }

  /**
   * Valida e persiste uma nova sessão de inferência recebida do app móvel.
   */
  async registerInference(payload) {
    const validatedPayload = validateInferencePayload(payload);

    const recordToSave = {
      ...validatedPayload,
      recebido_em: new Date().toISOString()
    };

    return await this.repository.save(recordToSave);
  }

  /**
   * Retorna a listagem cronológica de todas as sessões registradas.
   */
  async listInferences() {
    return await this.repository.findAll();
  }

  /**
   * Retorna os detalhes e caixas delimitadoras de uma sessão específica.
   */
  async getInferenceDetails(sessionId) {
    const session = await this.repository.findById(sessionId);
    if (!session) {
      throw new NotFoundError(`Sessão de inferência com id '${sessionId}' não foi encontrada.`);
    }
    return session;
  }

  /**
   * Retorna resumo estatístico agregado para dashboards e auditoria.
   */
  async getStatistics() {
    const all = await this.repository.findAll();
    const totalSessions = all.length;

    if (totalSessions === 0) {
      return {
        total_sessoes: 0,
        total_objetos_detectados: 0,
        tempo_medio_execucao_ms: 0,
        confianca_media_geral: 0
      };
    }

    const totalObjects = all.reduce((acc, s) => acc + (s.total_objetos || 0), 0);
    const totalTime = all.reduce((acc, s) => acc + (s.tempo_execucao_ms || 0), 0);

    const allBoxes = all.flatMap((s) => s.caixas_delimitadoras || []);
    const avgConfidence = allBoxes.length > 0
      ? allBoxes.reduce((acc, b) => acc + (b.confianca || 0), 0) / allBoxes.length
      : 0;

    return {
      total_sessoes: totalSessions,
      total_objetos_detectados: totalObjects,
      tempo_medio_execucao_ms: Number((totalTime / totalSessions).toFixed(2)),
      confianca_media_geral: Number((avgConfidence).toFixed(4))
    };
  }
}

export const inferenceService = new InferenceService();
