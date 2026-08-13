'use strict';

const fs = require('fs');
const path = require('path');

function safeId(id) {
  return String(id || '').replace(/[^a-zA-Z0-9._-]/g, '_');
}

function createPerformanceEvidenceStore({ root, maxEntries = 100 } = {}) {
  if (!root) throw new Error('Performance evidence store requires a root directory.');
  const directory = path.join(root, 'reports', 'command-center', 'performance-evidence');
  fs.mkdirSync(directory, { recursive: true });
  const memory = new Map();

  function save(evidence) {
    if (!evidence?.evidenceId) throw new Error('Evidence requires evidenceId.');
    memory.set(evidence.evidenceId, evidence);
    fs.writeFileSync(path.join(directory, `${safeId(evidence.evidenceId)}.json`), JSON.stringify(evidence, null, 2));
    trim();
    return evidence;
  }

  function get(id) {
    if (memory.has(id)) return memory.get(id);
    const file = path.join(directory, `${safeId(id)}.json`);
    if (!fs.existsSync(file)) return null;
    try {
      const evidence = JSON.parse(fs.readFileSync(file, 'utf8'));
      memory.set(evidence.evidenceId, evidence);
      return evidence;
    } catch {
      return null;
    }
  }

  function list() {
    if (!fs.existsSync(directory)) return [];
    return fs.readdirSync(directory).filter(name => name.endsWith('.json')).map(name => {
      try { return JSON.parse(fs.readFileSync(path.join(directory, name), 'utf8')); } catch { return null; }
    }).filter(Boolean).sort((a, b) => String(b.capturedAt).localeCompare(String(a.capturedAt)));
  }

  function latestBefore(executionId) {
    return list().find(item => item.executionId !== executionId) || null;
  }

  function trim() {
    for (const evidence of list().slice(maxEntries)) {
      memory.delete(evidence.evidenceId);
      const file = path.join(directory, `${safeId(evidence.evidenceId)}.json`);
      if (fs.existsSync(file)) fs.unlinkSync(file);
    }
  }

  return { directory, save, get, list, latestBefore };
}

module.exports = { createPerformanceEvidenceStore };
