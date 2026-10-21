import { inferenceService } from '../services/inferenceService.js';

/**
 * Controller REST para os endpoints de inferência (RF6 / RF7).
 */
export class InferenceController {
  constructor(service = inferenceService) {
    this.service = service;
  }

  create = async (req, res, next) => {
    try {
      const savedInference = await this.service.registerInference(req.body);
      return res.status(201).json({
        sucesso: true,
        mensagem: 'Inferência registrada com sucesso.',
        dados: savedInference
      });
    } catch (error) {
      next(error);
    }
  };

  list = async (req, res, next) => {
    try {
      const inferences = await this.service.listInferences();
      return res.status(200).json({
        sucesso: true,
        total: inferences.length,
        dados: inferences
      });
    } catch (error) {
      next(error);
    }
  };

  getById = async (req, res, next) => {
    try {
      const { id } = req.params;
      const inference = await this.service.getInferenceDetails(id);
      return res.status(200).json({
        sucesso: true,
        dados: inference
      });
    } catch (error) {
      next(error);
    }
  };

  stats = async (req, res, next) => {
    try {
      const stats = await this.service.getStatistics();
      return res.status(200).json({
        sucesso: true,
        dados: stats
      });
    } catch (error) {
      next(error);
    }
  };
}

export const inferenceController = new InferenceController();
