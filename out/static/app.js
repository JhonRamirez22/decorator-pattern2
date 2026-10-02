// Spanish labels for the codes sent by the backend.
const LAYERS = {
  FRAUD: { name: 'Antifraude', desc: 'Retiene montos altos a destinatarios nuevos' },
  DAILY_LIMIT: { name: 'Tope diario', desc: 'Máximo $3.000.000 al día por canal' },
  ACH_FEE: { name: 'Comisión ACH', desc: 'Cobro si el destino es otro banco' },
  GMF: { name: 'GMF 4x1000', desc: 'Impuesto sobre todo lo debitado' },
  AUDIT: { name: 'Auditoría', desc: 'Registra intento y resultado' },
  NOTIFICATION: { name: 'Notificación SMS', desc: 'Avisa al cliente el resultado' },
};

const CONCEPTS = {
  TRANSFERRED_AMOUNT: 'Valor transferido',
  ACH_INTERBANK_FEE: 'Comisión transferencia interbancaria',
  GMF_4X1000: 'Gravamen a los movimientos financieros',
  GMF_EXEMPT_ACCOUNT: 'Cuenta exenta de GMF',
  AUDIT_RECORDED: 'Operación registrada en bitácora',
  SMS_SENT: 'SMS enviado al titular',
  NEW_RECIPIENT_HIGH_AMOUNT: 'Destinatario nuevo y monto alto: retenida para verificación',
  DAILY_LIMIT_EXCEEDED: 'Supera el tope diario del canal',
};

const STATUS = { APPROVED: 'APROBADA', REJECTED: 'RECHAZADA', ON_HOLD: 'RETENIDA' };
const ERRORS = {
  InsufficientFundsException: 'Fondos insuficientes en la cuenta origen.',
  AccountNotFoundException: 'La cuenta no existe.',
  IllegalArgumentException: 'Datos inválidos: revisa el monto y que las cuentas sean distintas.',
};

// Outermost layer first, as the backend expects.
let layers = ['AUDIT', 'NOTIFICATION', 'FRAUD', 'DAILY_LIMIT', 'GMF', 'ACH_FEE']
  .map(code => ({ code, active: true }));
let templates = [];

async function fetchJson(url, options) {
  let response;
  try {
    response = await fetch(url, options);
  } catch {
    throw { error: 'NetworkError', detail: 'No se pudo conectar con el servidor. Intenta de nuevo.' };
  }
  let data;
  try {
    data = await response.json();
  } catch {
    throw { error: 'ServerError', detail: 'El servidor devolvió una respuesta inválida.' };
  }
  if (!response.ok) throw data;
  return data;
}

function currentConfiguration() {
  return new URLSearchParams({
    source: document.getElementById('source').value,
    target: document.getElementById('target').value,
    amount: document.getElementById('amount').value,
    decorators: layers.filter(layer => layer.active).map(layer => layer.code).join(','),
  });
}

async function loadTemplates(selectedId) {
  templates = await fetchJson('/api/templates');
  const select = document.getElementById('template-select');
  const previous = selectedId ?? select.value;
  select.replaceChildren();
  for (const template of templates) {
    const option = document.createElement('option');
    option.value = template.id;
    option.textContent = template.name + (template.builtIn ? ' · predefinida' : '');
    select.appendChild(option);
  }
  if (templates.some(template => template.id === previous)) select.value = previous;
}

document.getElementById('load-template').addEventListener('click', () => {
  const template = templates.find(item => item.id === document.getElementById('template-select').value);
  if (!template) return;
  document.getElementById('source').value = template.source;
  document.getElementById('target').value = template.target;
  document.getElementById('amount').value = template.amount;
  layers = [
    ...template.decorators.map(code => ({ code, active: true })),
    ...Object.keys(LAYERS).filter(code => !template.decorators.includes(code))
      .map(code => ({ code, active: false })),
  ];
  renderLayers();
  document.getElementById('template-feedback').textContent = `Plantilla cargada: ${template.name}. Puedes ajustar sus valores.`;
});

document.getElementById('save-template').addEventListener('click', async () => {
  const name = document.getElementById('template-name').value.trim();
  const feedback = document.getElementById('template-feedback');
  if (!name) {
    feedback.textContent = 'Escribe un nombre para la nueva plantilla.';
    document.getElementById('template-name').focus();
    return;
  }
  const button = document.getElementById('save-template');
  button.disabled = true;
  try {
    const form = currentConfiguration();
    form.set('prototypeId', document.getElementById('template-select').value);
    form.set('name', name);
    const saved = await fetchJson('/api/templates', { method: 'POST', body: form });
    await loadTemplates(saved.id);
    document.getElementById('template-name').value = '';
    feedback.textContent = `Plantilla guardada: ${saved.name}. La plantilla base conserva su configuración.`;
  } catch (error) {
    feedback.textContent = ERRORS[error.error] ?? error.detail ?? 'No se pudo guardar la plantilla.';
  } finally {
    button.disabled = false;
  }
});

const money = value => new Intl.NumberFormat('es-CO', {
  style: 'currency', currency: 'COP', maximumFractionDigits: 0,
}).format(value);

function renderLayers() {
  const list = document.getElementById('layers');
  list.innerHTML = '';
  layers.forEach((layer, index) => {
    const item = document.createElement('li');
    item.className = 'layer' + (layer.active ? '' : ' off');
    item.style.marginInline = `${index * 4}px`;
    item.innerHTML = `
      <input type="checkbox" ${layer.active ? 'checked' : ''} aria-label="Activar ${LAYERS[layer.code].name}">
      <span class="name">${LAYERS[layer.code].name}<span class="desc">${LAYERS[layer.code].desc}</span></span>
      <button type="button" aria-label="Subir">↑</button>
      <button type="button" aria-label="Bajar">↓</button>`;
    const [toggle, up, down] = item.querySelectorAll('input, button');
    toggle.addEventListener('change', () => { layer.active = toggle.checked; renderLayers(); });
    up.addEventListener('click', () => move(index, -1));
    down.addEventListener('click', () => move(index, 1));
    list.appendChild(item);
  });
}

function move(index, offset) {
  const target = index + offset;
  if (target < 0 || target >= layers.length) return;
  [layers[index], layers[target]] = [layers[target], layers[index]];
  renderLayers();
}

async function loadAccounts() {
  const accounts = await fetchJson('/api/accounts');
  const body = document.getElementById('accounts');
  body.innerHTML = accounts.map(a => `
    <tr>
      <td>${a.id}<small>${a.holder}</small></td>
      <td>${a.bank}${a.gmfExempt ? ' <span class="tag">Exenta GMF</span>' : ''}</td>
      <td class="num">${money(a.balance)}<small>Hoy: ${money(a.transferredToday)}</small></td>
    </tr>`).join('');

  for (const id of ['source', 'target']) {
    const select = document.getElementById(id);
    const previous = select.value;
    select.innerHTML = accounts.map(a => `<option value="${a.id}">${a.id} · ${a.holder} (${a.bank})</option>`).join('');
    if (previous) select.value = previous;
  }
  if (!document.getElementById('target').dataset.init) {
    document.getElementById('target').value = accounts[2].id;
    document.getElementById('target').dataset.init = '1';
  }
}

async function loadAudit() {
  const data = await fetchJson('/api/audit');
  const entries = [
    ...data.audit.map(text => `<li>${text}</li>`),
    ...data.sms.map(text => `<li class="sms">${text}</li>`),
  ];
  document.getElementById('audit').innerHTML = entries.reverse().join('') || '<li>Sin registros.</li>';
}

function renderReceipt(result) {
  const receipt = document.getElementById('receipt');
  receipt.className = 'receipt';
  const now = new Date().toLocaleString('es-CO');
  const rows = result.lines.map(line => `
    <div class="row">
      <span>${CONCEPTS[line.concept] ?? line.concept}<small>${LAYERS[line.layer]?.name ?? 'Transferencia base'}</small></span>
      <span>${Number(line.charge) > 0 ? money(line.charge) : '—'}</span>
    </div>`).join('');
  const chain = result.chain.length
    ? result.chain.map(code => LAYERS[code].name).join(' ( ') + ' ( Base ' + ')'.repeat(result.chain.length)
    : 'Base sin decoradores';
  receipt.innerHTML = `
    <span class="stamp ${result.status}">${STATUS[result.status]}</span>
    <h2>Comprobante</h2>
    <p class="meta">${now}</p>
    ${rows}
    <div class="row total"><span>Total debitado</span><span>${money(result.totalDebited)}</span></div>
    <p class="chain">Cadena: ${chain}</p>`;
}

function renderError(error) {
  const receipt = document.getElementById('receipt');
  receipt.className = 'receipt';
  receipt.innerHTML = `
    <span class="stamp REJECTED">ERROR</span>
    <h2>Comprobante</h2>
    <p class="meta"></p>`;
  receipt.querySelector('.meta').textContent = ERRORS[error.error] ?? error.detail ?? 'Ocurrió un error.';
}

document.getElementById('transfer-form').addEventListener('submit', async event => {
  event.preventDefault();
  const button = document.getElementById('transfer-button');
  button.disabled = true;
  try {
    const result = await fetchJson('/api/transfers', { method: 'POST', body: currentConfiguration() });
    renderReceipt(result);
    await Promise.all([loadAccounts(), loadAudit()]);
  } catch (error) {
    renderError(error);
  } finally {
    button.disabled = false;
  }
});

async function initialize() {
  renderLayers();
  try {
    await loadAccounts();
    await Promise.all([loadTemplates(), loadAudit()]);
    document.getElementById('transfer-button').disabled = false;
  } catch (error) {
    renderError(error);
  }
}

initialize();
