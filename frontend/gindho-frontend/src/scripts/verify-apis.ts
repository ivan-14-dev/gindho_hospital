import fs from 'fs';
import path from 'path';

const PROJECT_ROOT = process.cwd();
const API_SERVICE_PATH = path.join(PROJECT_ROOT, 'src', 'services', 'api.service.ts');
const ENV_PATHS = [
  path.join(PROJECT_ROOT, '.env'),
];

interface Endpoint {
  method: string;
  path: string;
  service: string;
  functionName: string;
}

function parseEnvVariables(envPath: string): Record<string, string> {
  const variables: Record<string, string> = {};
  if (!fs.existsSync(envPath)) return variables;

  const content = fs.readFileSync(envPath, 'utf-8');
  for (const line of content.split('\n')) {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith('#')) continue;
    const eqIndex = trimmed.indexOf('=');
    if (eqIndex === -1) continue;
    const key = trimmed.slice(0, eqIndex).trim();
    const value = trimmed.slice(eqIndex + 1).trim();
    if (key) variables[key] = value;
  }
  return variables;
}

const envVariables = ENV_PATHS.flatMap((p) => Object.entries(parseEnvVariables(p)));
const envMap = Object.fromEntries(envVariables);

function parseApiService(filePath: string): Endpoint[] {
  const content = fs.readFileSync(filePath, 'utf-8');
  const endpoints: Endpoint[] = [];

  const lines = content.split('\n');

  for (let i = 0; i < lines.length; i++) {
    const serviceMatch = lines[i].match(/^export const (\w+) = \{/);
    if (serviceMatch) {
      continue;
    }

    const methodMatch = lines[i].match(/^\s+async (\w+)\(/);
    if (methodMatch) {
      const functionName = methodMatch[1];
      let apiCallLine = '';
      for (let j = i; j < Math.min(i + 8, lines.length); j++) {
        apiCallLine += lines[j];
        if (apiCallLine.includes(';')) break;
      }

      const apiMatch = apiCallLine.match(
        /apiClient\.(get|post|put|patch|delete)\(buildApiUrl\('([^']+)', '([^']+)'\)/
      );
      if (apiMatch) {
        endpoints.push({
          method: apiMatch[1].toUpperCase(),
          service: apiMatch[2],
          path: apiMatch[3],
          functionName,
        });
      }
    }
  }

  return endpoints;
}

function getGatewayUrl(): string {
  return envMap.VITE_GATEWAY_URL || envMap.GATEWAY_URL || 'http://localhost:8000';
}

async function checkEndpointHealth(endpoint: Endpoint): Promise<{ endpoint: Endpoint; status: string }> {
  let resolvedPath = endpoint.path;
  for (const match of resolvedPath.matchAll(/\?([^=]+)=([^&]*)/g)) {
    const paramKey = match[1];
    const paramValue = match[2] || '1';
    if (/^\d+$/.test(paramValue)) {
      resolvedPath = resolvedPath.replace(match[0], `?${paramKey}=${paramValue}`);
    }
  }

  resolvedPath = resolvedPath.replace(/:([^/]+)/g, '1');

  const url = `${getGatewayUrl()}${resolvedPath}`;
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 5000);

  try {
    const response = await fetch(url, { method: 'HEAD', signal: controller.signal });
    clearTimeout(timeout);
    if (response.ok) {
      return { endpoint, status: 'OK' };
    }
    return { endpoint, status: `HTTP_${response.status}` };
  } catch (error) {
    clearTimeout(timeout);
    if ((error as Error).name === 'AbortError') {
      return { endpoint, status: 'TIMEOUT' };
    }
    return { endpoint, status: 'UNREACHABLE' };
  }
}

async function main() {
  console.log('\n=== API Services Verification Matrix ===\n');

  if (!fs.existsSync(API_SERVICE_PATH)) {
    console.error(`ERROR: API service file not found at ${API_SERVICE_PATH}`);
    process.exit(1);
  }

  const endpoints = parseApiService(API_SERVICE_PATH);
  if (endpoints.length === 0) {
    console.log('No endpoints found in api.service.ts');
    process.exit(1);
  }

  const gatewayUrl = getGatewayUrl();
  console.log(`Gateway URL: ${gatewayUrl}`);
  console.log(`Total endpoints found: ${endpoints.length}\n`);

  console.log('Checking endpoint health...\n');

  const results = await Promise.all(endpoints.map((ep) => checkEndpointHealth(ep)));

  console.log('='.repeat(90));
  console.log(
    'SERVICE NAME'.padEnd(22) +
    ' | METHOD | PATH'.padEnd(24) +
    ' | FUNCTION'.padEnd(28) +
    ' | STATUS'
  );
  console.log('='.repeat(90));

  for (const item of results) {
    const service = item.endpoint.service.padEnd(22);
    const method = item.endpoint.method.padEnd(6);
    const path = item.endpoint.path.padEnd(24);
    const fn = item.endpoint.functionName.padEnd(28);
    const status = item.status;
    console.log(`${service} | ${method} | ${path} | ${fn} | ${status}`);
  }

  console.log('='.repeat(90));

  const summary: Record<string, { total: number; ok: number; errors: number }> = {};
  for (const item of results) {
    const svc = item.endpoint.service;
    if (!summary[svc]) {
      summary[svc] = { total: 0, ok: 0, errors: 0 };
    }
    summary[svc].total++;
    summary[svc].ok += item.status === 'OK' ? 1 : 0;
    summary[svc].errors += item.status !== 'OK' ? 1 : 0;
  }

  console.log('\n=== Service Summary ===\n');
  console.log('SERVICE'.padEnd(22) + ' | TOTAL | OK | ERRORS');
  console.log('-'.repeat(50));
  for (const [svc, stats] of Object.entries(summary).sort((a, b) => a[0].localeCompare(b[0]))) {
    console.log(
      svc.padEnd(22) + ` | ${String(stats.total).padEnd(5)} | ${String(stats.ok).padEnd(2)} | ${stats.errors}`
    );
  }

  const totalEndpoints = results.length;
  const totalOk = results.filter((r) => r.status === 'OK').length;
  const totalErrors = totalEndpoints - totalOk;

  console.log('\n=== Overall ===\n');
  console.log(`Total endpoints: ${totalEndpoints}`);
  console.log(`OK: ${totalOk}`);
  console.log(`Errors: ${totalErrors}`);
  console.log(`Health: ${((totalOk / totalEndpoints) * 100).toFixed(1)}%\n`);

  process.exit(totalErrors > 0 ? 1 : 0);
}

main();
