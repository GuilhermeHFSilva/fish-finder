import { AppError } from '../errors/AppError.js';
import { ValidationError } from '../errors/ValidationError.js';

/**
 * Middleware centralizado de tratamento de erros.
 * Garante que a API sempre responda com JSON padronizado e códigos HTTP semânticos.
 */
export function errorHandler(error, req, res, next) {
  if (error instanceof ValidationError) {
    return res.status(error.statusCode).json({
      sucesso: false,
      erro: error.message,
      detalhes: error.details
    });
  }

  if (error instanceof AppError) {
    return res.status(error.statusCode).json({
      sucesso: false,
      erro: error.message
    });
  }

  // Falhas inesperadas (500)
  console.error('[ERRO INTERNO NÃO TRATADO]', error);
  return res.status(500).json({
    sucesso: false,
    erro: 'Ocorreu um erro interno no servidor.'
  });
}
