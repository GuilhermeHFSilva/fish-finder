import { createApp } from './app.js';
import { environment } from './config/environment.js';
import { inferenceRepository } from './repositories/inferenceRepository.js';

async function bootstrap() {
  await inferenceRepository.init();
  const app = createApp();

  const server = app.listen(environment.port, environment.host, () => {
    console.log(`=======================================================`);
    console.log(`🐟 FishFinder Backend API em execução!`);
    console.log(`📡 Endereço: http://${environment.host}:${environment.port}`);
    console.log(`🚀 Endpoints disponíveis:`);
    console.log(`   - POST http://localhost:${environment.port}/api/inferencias`);
    console.log(`   - GET  http://localhost:${environment.port}/api/inferencias`);
    console.log(`   - GET  http://localhost:${environment.port}/api/inferencias/:id`);
    console.log(`   - GET  http://localhost:${environment.port}/api/inferencias/stats/analytics`);
    console.log(`   - GET  http://localhost:${environment.port}/health`);
    console.log(`💾 Armazenamento: ${environment.storageFilePath}`);
    console.log(`=======================================================`);
  });

  const shutdown = async (signal) => {
    console.log(`\n[${signal}] Encerrando servidor graciosamente...`);
    server.close(() => {
      console.log('Servidor encerrado.');
      process.exit(0);
    });
  };

  process.on('SIGINT', () => shutdown('SIGINT'));
  process.on('SIGTERM', () => shutdown('SIGTERM'));
}

bootstrap().catch((error) => {
  console.error('Falha crítica ao iniciar o servidor:', error);
  process.exit(1);
});
