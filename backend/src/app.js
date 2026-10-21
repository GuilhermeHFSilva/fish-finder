import express from 'express';
import cors from 'cors';
import { environment } from './config/environment.js';
import { requestLogger } from './middlewares/requestLogger.js';
import { errorHandler } from './middlewares/errorHandler.js';
import { inferenceRouter } from './routes/inferenceRoutes.js';
import { NotFoundError } from './errors/NotFoundError.js';

export function createApp() {
  const app = express();

  // Middlewares globais
  app.use(cors({ origin: environment.corsOrigins }));
  app.use(express.json({ limit: '10mb' }));
  app.use(requestLogger);

  // Healthcheck
  app.get('/health', (req, res) => {
    res.status(200).json({
      status: 'UP',
      servico: 'FishFinder Backend API',
      horario: new Date().toISOString()
    });
  });

  // Rotas da API
  app.use('/api/inferencias', inferenceRouter);

  // Rota 404 para endpoints não mapeados
  app.use((req, res, next) => {
    next(new NotFoundError(`Rota não encontrada: ${req.method} ${req.originalUrl}`));
  });

  // Tratamento central de erros
  app.use(errorHandler);

  return app;
}
