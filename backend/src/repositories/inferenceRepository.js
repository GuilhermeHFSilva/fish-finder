import fs from 'node:fs/promises';
import path from 'node:path';
import { environment } from '../config/environment.js';

/**
 * Repositório de persistência estruturada em arquivo JSON (RF7).
 * Garante durabilidade e auditoria histórica com operações atômicas.
 */
export class InferenceRepository {
  constructor(filePath = environment.storageFilePath) {
    this.filePath = filePath;
    this.dirPath = path.dirname(filePath);
  }

  async init() {
    await fs.mkdir(this.dirPath, { recursive: true });
    try {
      await fs.access(this.filePath);
    } catch {
      await fs.writeFile(this.filePath, JSON.stringify([], null, 2), 'utf-8');
    }
  }

  async findAll() {
    const sessions = await this._readAll();
    // Ordenação cronológica decrescente (mais recentes primeiro)
    return sessions.sort((a, b) => new Date(b.data_hora).getTime() - new Date(a.data_hora).getTime());
  }

  async findById(id) {
    const sessions = await this._readAll();
    return sessions.find((session) => session.id_sessao === id) || null;
  }

  async save(session) {
    const sessions = await this._readAll();
    const existingIndex = sessions.findIndex((s) => s.id_sessao === session.id_sessao);

    if (existingIndex >= 0) {
      sessions[existingIndex] = session;
    } else {
      sessions.push(session);
    }

    await this._writeAll(sessions);
    return session;
  }

  async count() {
    const sessions = await this._readAll();
    return sessions.length;
  }

  async _readAll() {
    await this.init();
    try {
      const data = await fs.readFile(this.filePath, 'utf-8');
      return JSON.parse(data || '[]');
    } catch (error) {
      return [];
    }
  }

  async _writeAll(data) {
    await this.init();
    const tempPath = `${this.filePath}.tmp`;
    await fs.writeFile(tempPath, JSON.stringify(data, null, 2), 'utf-8');
    await fs.rename(tempPath, this.filePath);
  }
}

export const inferenceRepository = new InferenceRepository();
