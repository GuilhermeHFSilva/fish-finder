import { Router } from 'express';
import { inferenceController } from '../controllers/inferenceController.js';

export const inferenceRouter = Router();

// Estatísticas globais (deve vir antes de /:id para evitar colisão de rota)
inferenceRouter.get('/stats/analytics', inferenceController.stats);

// Endpoints principais da especificação
inferenceRouter.post('/', inferenceController.create);
inferenceRouter.get('/', inferenceController.list);
inferenceRouter.get('/:id', inferenceController.getById);
