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
  const accounts = await (await fetch('/api/accounts')).json();
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
  const data = await (await fetch('/api/audit')).json();
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
    <p class="meta">${ERRORS[error.error] ?? error.detail}</p>`;
}

document.getElementById('transfer-form').addEventListener('submit', async event => {
  event.preventDefault();
  const form = new URLSearchParams({
    source: document.getElementById('source').value,
    target: document.getElementById('target').value,
    amount: document.getElementById('amount').value,
    decorators: layers.filter(l => l.active).map(l => l.code).join(','),
  });
  const response = await fetch('/api/transfers', { method: 'POST', body: form });
  const result = await response.json();
  response.ok ? renderReceipt(result) : renderError(result);
  await Promise.all([loadAccounts(), loadAudit()]);
});

renderLayers();
loadAccounts();
loadAudit();
