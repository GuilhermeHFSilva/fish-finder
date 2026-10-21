import path from 'node:path';
import { fileURLToPath } from 'node:url';

const currentFilename = fileURLToPath(import.meta.url);
const currentDirname = path.dirname(currentFilename);
const rootBackendDir = path.resolve(currentDirname, '..', '..');

export const environment = {
  port: Number(process.env.PORT) || 3000,
  host: process.env.HOST || '0.0.0.0',
  storageFilePath: process.env.STORAGE_FILE_PATH || path.join(rootBackendDir, 'data', 'inferences.json'),
  corsOrigins: process.env.CORS_ORIGIN || '*'
};
