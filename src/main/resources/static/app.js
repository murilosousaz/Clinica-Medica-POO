const API = {
  pacientes: '/api/pacientes',
  medicos: '/api/medicos',
  enfermeiros: '/api/enfermeiros',
  triagens: '/api/triagens',
  consultas: '/api/consultas',
  avaliacoes: '/api/avaliacoes',
  estatisticas: '/api/estatisticas',
  contas: '/api/contas',
  csv: '/api/csv'
};

const state = {
  pacientes: [],
  medicos: [],
  enfermeiros: [],
  triagens: [],
  consultas: [],
  fila: [],
  avaliacoes: [],
  estatisticas: {},
  contas: []
};

const pageText = {
  dashboard: ['Dashboard', 'Resumo operacional da clínica e dos requisitos principais.'],
  cadastros: ['Cadastros', 'Cadastro único de pacientes, médicos e enfermeiros.'],
  triagem: ['Triagem', 'Classificação de risco e sinais vitais.'],
  agenda: ['Agenda', 'Agendamento com limite diário e lista de espera.'],
  atendimento: ['Atendimento', 'Conclusão da consulta e geração de prontuário.'],
  prontuario: ['Prontuário', 'Histórico clínico completo do paciente.'],
  avaliacoes: ['Avaliações', 'Feedback textual e estrelas para o médico.'],
  dados: ['Dados CSV', 'Exportação e importação para atender às diretrizes da disciplina.']
};

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => Array.from(document.querySelectorAll(selector));

async function apiRequest(url, options = {}) {
  const response = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });

  if (!response.ok) {
    let message = `${response.status} ${response.statusText}`;
    try {
      const data = await response.json();
      message = data.message || data.error || data.detail || message;
    } catch (_) {}
    throw new Error(message);
  }

  if (response.status === 204) return null;
  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

async function loadResource(key, promise) {
  try {
    state[key] = await promise;
    return null;
  } catch (error) {
    return `${key}: ${error.message}`;
  }
}

async function loadAll() {
  setApiStatus('Carregando', 'Atualizando dados...', false);
  const errors = await Promise.all([
    loadResource('pacientes', apiRequest(API.pacientes)),
    loadResource('medicos', apiRequest(API.medicos)),
    loadResource('enfermeiros', apiRequest(API.enfermeiros)),
    loadResource('triagens', apiRequest(API.triagens)),
    loadResource('consultas', apiRequest(API.consultas)),
    loadResource('fila', apiRequest(`${API.consultas}/lista-espera`)),
    loadResource('avaliacoes', apiRequest(API.avaliacoes)),
    loadResource('estatisticas', apiRequest(API.estatisticas)),
    loadResource('contas', apiRequest(`${API.contas}/pendentes`))
  ]);

  renderAll();
  const failures = errors.filter(Boolean);
  if (failures.length) {
    setApiStatus('Parcial', `${failures.length} recurso(s) indisponível(is)`, true);
    showAlert(failures.slice(0, 2).join(' | '), 'error');
  } else {
    setApiStatus('Conectada', 'Backend respondendo normalmente', false);
  }
}

function setApiStatus(title, detail, isError) {
  $('#api-status').textContent = title;
  $('#api-detail').textContent = detail;
  $('.status-card').classList.toggle('error', Boolean(isError));
  $('.status-card').classList.toggle('ok', !isError && title === 'Conectada');
}

function showAlert(message, type = 'success') {
  const alert = $('#alert');
  alert.textContent = message;
  alert.className = `alert ${type === 'error' ? 'error' : ''}`.trim();
  window.clearTimeout(showAlert.timer);
  showAlert.timer = window.setTimeout(() => alert.classList.add('hidden'), 5000);
}

function renderAll() {
  renderMetrics();
  renderDashboard();
  renderCadastros();
  renderTriagens();
  renderAgenda();
  renderAtendimento();
  renderAvaliacoes();
  fillSelects();
  applyFilter($('#screen-filter').value || '');
}

function renderMetrics() {
  setText('#metric-pacientes', state.pacientes.length);
  setText('#metric-medicos', state.medicos.length);
  setText('#metric-triagens', state.triagens.length);
  setText('#metric-consultas', state.consultas.length);
  setText('#metric-realizadas', state.consultas.filter((c) => c.status === 'REALIZADA').length);
  setText('#total-pacientes', state.pacientes.length);
  setText('#total-medicos', state.medicos.length);
  setText('#total-triagens', state.triagens.length);
  setText('#total-consultas', state.consultas.length);
  setText('#total-avaliacoes', state.avaliacoes.length);
}

function renderDashboard() {
  const statusBase = { AGENDADA: 0, REALIZADA: 0, CANCELADA: 0 };
  state.consultas.forEach((consulta) => {
    const status = consulta.status || 'SEM_STATUS';
    statusBase[status] = (statusBase[status] || 0) + 1;
  });
  const rows = Object.entries(statusBase);
  const max = Math.max(1, ...rows.map(([, value]) => value));

  $('#status-chart').innerHTML = rows.map(([status, value]) => `
    <div class="chart-row searchable">
      <strong>${escapeHtml(labelStatus(status))}</strong>
      <div class="chart-track"><div class="chart-fill" style="width:${Math.max(5, (value / max) * 100)}%"></div></div>
      <span>${value}</span>
    </div>`).join('');

  const stats = state.estatisticas || {};
  const medico = stats.medicoMaisBemAvaliado;
  const especialidade = stats.especialidadeMaisProcurada;
  $('#stats-list').innerHTML = [
    statItem('Médico mais bem avaliado', medico ? `${medico.nome} • ${Number(medico.media || 0).toFixed(1)} ★` : 'Sem avaliações'),
    statItem('Especialidade mais procurada', especialidade ? `${especialidade.especialidade} • ${especialidade.consultas} consulta(s)` : 'Sem dados'),
    statItem('Consultas realizadas', String(stats.totalConsultasRealizadas ?? state.consultas.filter((c) => c.status === 'REALIZADA').length)),
    statItem('Média geral de avaliações', `${Number(stats.mediaGeralAvaliacoes || 0).toFixed(1)} ★`),
    statItem('Fila de espera', `${state.fila.length} paciente(s)`)
  ].join('');

  const upcoming = [...state.consultas]
    .filter((c) => c.status === 'AGENDADA')
    .sort((a, b) => new Date(a.dataHora || 0) - new Date(b.dataHora || 0))
    .slice(0, 5);
  $('#upcoming-list').innerHTML = upcoming.length ? upcoming.map((c) => listItem(
    `${c.pacienteNome || 'Paciente'} com ${c.medicoNome || 'Médico'}`,
    `${formatDate(c.dataHora)} • ${money(c.valorPago)}`,
    badge('Agendada', 'info')
  )).join('') : emptyState('Nenhuma consulta agendada.');

  $('#wait-dashboard').innerHTML = renderFila(state.fila.slice(0, 5));
}

function renderCadastros() {
  $('#patients-table').innerHTML = state.pacientes.length ? state.pacientes.map((p) => `
    <tr class="searchable">
      <td><strong>${escapeHtml(p.nome)}</strong><span class="subtext">${p.id}</span></td>
      <td>${escapeHtml(p.cpf || '-')}</td>
      <td>${escapeHtml(planoLabel(p))}</td>
      <td>${escapeHtml(p.telefone || '-')}<span class="subtext">${escapeHtml(p.email || 'Sem e-mail')}</span></td>
    </tr>`).join('') : tableEmpty(4, 'Nenhum paciente cadastrado.');

  $('#doctors-list').innerHTML = state.medicos.length ? state.medicos.map((m) => {
    const avg = medicoMedia(m.id);
    return `
      <article class="doctor-card searchable">
        <header><div><strong>${escapeHtml(m.nome)}</strong><small>CRM: ${escapeHtml(m.crm || '-')}</small></div>${badge(formatEspecialidade(m.especialidade), 'info')}</header>
        <small>Limite diário: ${limiteMedico(m)} consulta(s)</small>
        <small>Avaliação: ${stars(avg)} ${avg ? Number(avg).toFixed(1) : 'sem avaliações'}</small>
      </article>`;
  }).join('') : emptyState('Nenhum médico cadastrado.');
}

function renderTriagens() {
  const triagens = [...state.triagens].sort((a, b) => new Date(b.dataHora || 0) - new Date(a.dataHora || 0));
  $('#screenings-list').innerHTML = triagens.length ? triagens.map((t) => `
    <article class="record-card searchable">
      <header><div><strong>${escapeHtml(t.pacienteNome || 'Paciente')}</strong><small>${formatDate(t.dataHora)}</small></div>${badge(t.prioridade || 'Prioridade', prioridadeType(t.prioridade))}</header>
      <small>Enfermeiro: ${escapeHtml(t.enfermeiroNome || '-')}</small>
      <small>Queixa: ${escapeHtml(t.queixaPrincipal || '-')}</small>
      <small>PA ${escapeHtml(t.pressaoArterial || '-')} • Temp. ${valueOr(t.temperatura)} • FC ${valueOr(t.frequenciaCardiaca)} • FR ${valueOr(t.frequenciaRespiratoria)}</small>
    </article>`).join('') : emptyState('Nenhuma triagem registrada.');
}

function renderAgenda() {
  const consultas = [...state.consultas].sort((a, b) => new Date(b.dataHora || 0) - new Date(a.dataHora || 0));
  $('#appointments-table').innerHTML = consultas.length ? consultas.map((c) => `
    <tr class="searchable">
      <td><strong>${escapeHtml(c.pacienteNome || '-')}</strong></td>
      <td>${escapeHtml(c.medicoNome || '-')}</td>
      <td>${formatDate(c.dataHora)}</td>
      <td>${badge(labelStatus(c.status), statusType(c.status))}</td>
      <td>${c.status === 'AGENDADA' ? `<button class="button button-danger" type="button" onclick="cancelarConsulta('${c.id}')">Cancelar</button>` : '<span class="subtext">—</span>'}</td>
    </tr>`).join('') : tableEmpty(5, 'Nenhuma consulta na agenda.');

  $('#wait-list').innerHTML = renderFila(state.fila);
}

function renderAtendimento() {
  const pendentes = state.consultas.filter((c) => c.status === 'AGENDADA');
  $('#pending-list').innerHTML = pendentes.length ? pendentes.map((c) => `
    <article class="record-card searchable">
      <header><div><strong>${escapeHtml(c.pacienteNome || 'Paciente')}</strong><small>${formatDate(c.dataHora)}</small></div>${badge('Pendente', 'warn')}</header>
      <small>Médico: ${escapeHtml(c.medicoNome || '-')}</small>
      <small>Observação: ${escapeHtml(c.observacoes || 'Sem observações')}</small>
    </article>`).join('') : emptyState('Nenhuma consulta pendente para atendimento.');
}

function renderAvaliacoes() {
  $('#reviews-list').innerHTML = state.avaliacoes.length ? state.avaliacoes.map((a) => `
    <article class="review-card searchable">
      <header><div><strong>${escapeHtml(a.medicoNome || 'Médico')}</strong><small>Paciente: ${escapeHtml(a.pacienteNome || '-')}</small></div><span>${stars(a.estrelas)}</span></header>
      <small>${escapeHtml(a.texto || 'Sem comentário textual.')}</small>
    </article>`).join('') : emptyState('Nenhuma avaliação registrada.');
}

function renderFila(items) {
  return items.length ? items.map((f, index) => `
    <article class="record-card searchable">
      <header><div><strong>${index + 1}. ${escapeHtml(f.pacienteNome || 'Paciente')}</strong><small>${formatDateOnly(f.dataConsulta)} ${escapeHtml(f.horarioDesejado || '')}</small></div>${badge(f.status || 'Aguardando', 'warn')}</header>
      <small>Médico: ${escapeHtml(f.medicoNome || '-')}</small>
      <small>${escapeHtml(f.notificacao || 'Aguardando vaga disponível.')}</small>
    </article>`).join('') : emptyState('Lista de espera vazia.');
}

function fillSelects() {
  fillSelect('#screening-patient', state.pacientes, 'Selecione um paciente');
  fillSelect('#screening-nurse', state.enfermeiros, 'Selecione um enfermeiro');
  fillSelect('#appointment-patient', state.pacientes, 'Selecione um paciente');
  fillSelect('#appointment-doctor', state.medicos, 'Selecione um médico', (m) => `${m.nome} — ${formatEspecialidade(m.especialidade)}`);
  fillSelect('#record-patient', state.pacientes, 'Selecione um paciente');

  const agendadas = state.consultas.filter((c) => c.status === 'AGENDADA');
  fillSelect('#finish-appointment', agendadas, 'Selecione uma consulta', (c) => `${c.pacienteNome} • ${c.medicoNome} • ${formatDate(c.dataHora)}`);

  const realizadasNaoAvaliadas = state.consultas.filter((c) => c.status === 'REALIZADA' && !c.avaliacaoEstrelas);
  fillSelect('#review-appointment', realizadasNaoAvaliadas, 'Selecione uma consulta realizada', (c) => `${c.pacienteNome} • ${c.medicoNome} • ${formatDate(c.dataHora)}`);
}

function fillSelect(selector, items, placeholder, labelFn = (item) => item.nome) {
  const select = $(selector);
  if (!select) return;
  const selected = select.value;
  select.innerHTML = `<option value="">${escapeHtml(placeholder)}</option>` + items.map((item) => `<option value="${item.id}">${escapeHtml(labelFn(item))}</option>`).join('');
  if (items.some((item) => String(item.id) === selected)) select.value = selected;
}

function setupNavigation() {
  $$('.menu-item').forEach((button) => {
    button.addEventListener('click', () => goTo(button.dataset.section));
  });
  $$('[data-go]').forEach((button) => button.addEventListener('click', () => goTo(button.dataset.go)));
  $('#sidebar-toggle').addEventListener('click', () => $('#sidebar').classList.toggle('open'));
}

function goTo(sectionId) {
  $$('.menu-item').forEach((item) => item.classList.toggle('active', item.dataset.section === sectionId));
  $$('.screen').forEach((screen) => screen.classList.toggle('active-screen', screen.id === sectionId));
  const [title, description] = pageText[sectionId] || pageText.dashboard;
  $('#page-title').textContent = title;
  $('#page-description').textContent = description;
  $('#screen-filter').value = '';
  applyFilter('');
  $('#sidebar').classList.remove('open');
}

function setupForms() {
  bindForm('#patient-form', async (form) => {
    const data = formData(form);
    const plano = (data.plano || '').trim();
    await apiRequest(API.pacientes, {
      method: 'POST',
      body: JSON.stringify({
        nome: data.nome,
        idade: numberOrNull(data.idade),
        cpf: data.cpf,
        telefone: data.telefone,
        email: data.email || null,
        planoSaude: { nome: plano || 'Não tenho', numeroCarnetizacao: null, ativo: Boolean(plano && plano.toLowerCase() !== 'não tenho' && plano.toLowerCase() !== 'nao tenho') }
      })
    });
    form.reset();
    showAlert('Paciente cadastrado com sucesso.');
  });

  bindForm('#doctor-form', async (form) => {
    await apiRequest(API.medicos, { method: 'POST', body: JSON.stringify(formData(form)) });
    form.reset();
    showAlert('Médico cadastrado com sucesso.');
  });

  bindForm('#nurse-form', async (form) => {
    const data = formData(form);
    data.anosExperiencia = numberOrNull(data.anosExperiencia) || 0;
    await apiRequest(API.enfermeiros, { method: 'POST', body: JSON.stringify(data) });
    form.reset();
    showAlert('Enfermeiro cadastrado com sucesso.');
  });

  bindForm('#screening-form', async (form) => {
    const data = formData(form);
    ['temperatura', 'peso', 'altura'].forEach((key) => data[key] = decimalOrNull(data[key]));
    ['frequenciaCardiaca', 'frequenciaRespiratoria'].forEach((key) => data[key] = numberOrNull(data[key]));
    await apiRequest(API.triagens, { method: 'POST', body: JSON.stringify(data) });
    form.reset();
    showAlert('Triagem registrada com sucesso.');
  });

  bindForm('#appointment-form', async (form) => {
    const data = formData(form);
    await apiRequest(API.consultas, { method: 'POST', body: JSON.stringify(data) });
    form.reset();
    showAlert('Consulta agendada. Se a agenda estiver cheia, o paciente irá para a lista de espera.');
  });

  bindForm('#finish-form', async (form) => {
    const data = formData(form);
    const consultaId = data.consultaId;
    delete data.consultaId;
    data.examesSolicitados = (data.examesSolicitados || '').split(',').map((item) => item.trim()).filter(Boolean);
    await apiRequest(`${API.consultas}/${consultaId}/realizar`, { method: 'PUT', body: JSON.stringify(data) });
    form.reset();
    showAlert('Consulta realizada e prontuário atualizado.');
  });

  bindForm('#record-form', async (form) => {
    const { pacienteId } = formData(form);
    const prontuario = await apiRequest(`${API.consultas}/paciente/${pacienteId}/prontuario`);
    renderProntuario(prontuario);
  }, false);

  bindForm('#review-form', async (form) => {
    const data = formData(form);
    data.estrelas = Number(data.estrelas);
    await apiRequest(API.avaliacoes, { method: 'POST', body: JSON.stringify(data) });
    form.reset();
    showAlert('Avaliação registrada com sucesso.');
  });

  $('#export-csv').addEventListener('click', () => runCsv('exportar'));
  $('#import-csv').addEventListener('click', () => runCsv('importar'));
  $('#refresh-button').addEventListener('click', () => loadAll());
  $('#screen-filter').addEventListener('input', (event) => applyFilter(event.target.value));
}

function bindForm(selector, handler, reload = true) {
  const form = $(selector);
  if (!form) return;
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
      await handler(form);
      if (reload) await loadAll();
    } catch (error) {
      showAlert(error.message, 'error');
    }
  });
}

async function cancelarConsulta(id) {
  try {
    await apiRequest(`${API.consultas}/${id}/cancelar`, { method: 'PUT' });
    showAlert('Consulta cancelada. Se houver fila, o primeiro paciente será promovido automaticamente.');
    await loadAll();
  } catch (error) {
    showAlert(error.message, 'error');
  }
}
window.cancelarConsulta = cancelarConsulta;

async function runCsv(action) {
  try {
    const result = await apiRequest(`${API.csv}/${action}`, { method: 'POST' });
    $('#csv-output').textContent = JSON.stringify(result, null, 2);
    showAlert(`Operação CSV executada: ${action}.`);
  } catch (error) {
    $('#csv-output').textContent = error.message;
    showAlert(error.message, 'error');
  }
}

function renderProntuario(items) {
  $('#medical-record-list').innerHTML = items.length ? items.map((c) => `
    <article class="record-card searchable">
      <header><div><strong>${escapeHtml(c.medicoNome || 'Médico')}</strong><small>${formatDate(c.dataHora)}</small></div>${badge(money(c.valorPago), 'neutral')}</header>
      <small><strong>Sintomas:</strong> ${escapeHtml(c.sintomas || '-')}</small>
      <small><strong>Diagnóstico:</strong> ${escapeHtml(c.diagnostico || '-')}</small>
      <small><strong>Tratamento:</strong> ${escapeHtml(c.tratamentoSugerido || '-')}</small>
      <small><strong>Medicamentos:</strong> ${escapeHtml(c.medicamentos || '-')}</small>
      <small><strong>Exames:</strong> ${escapeHtml((c.examesSolicitados || []).join(', ') || '-')}</small>
    </article>`).join('') : emptyState('Esse paciente ainda não possui consultas realizadas.');
}

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function applyFilter(rawTerm) {
  const term = normalize(rawTerm);
  const activeScreen = $('.active-screen');
  if (!activeScreen) return;
  activeScreen.querySelectorAll('.searchable').forEach((node) => {
    node.classList.toggle('filtered-out', term && !normalize(node.textContent).includes(term));
  });
}

function setText(selector, value) {
  const element = $(selector);
  if (element) element.textContent = value;
}

function statItem(label, value) {
  return `<div class="stat-item searchable"><strong>${escapeHtml(label)}</strong><span>${escapeHtml(value)}</span></div>`;
}

function listItem(title, subtitle, right = '') {
  return `<article class="list-item searchable"><header><div><strong>${escapeHtml(title)}</strong><small>${escapeHtml(subtitle)}</small></div>${right}</header></article>`;
}

function badge(text, type = 'neutral') {
  return `<span class="badge ${type}">${escapeHtml(String(text || '-'))}</span>`;
}

function emptyState(message) {
  return `<div class="empty-state">${escapeHtml(message)}</div>`;
}

function tableEmpty(colspan, message) {
  return `<tr><td colspan="${colspan}">${emptyState(message)}</td></tr>`;
}

function planoLabel(paciente) {
  const plano = paciente.planoSaude;
  if (!plano) return 'Não tenho';
  if (typeof plano === 'string') return plano;
  return plano.nome || 'Não tenho';
}

function medicoMedia(medicoId) {
  const reviews = state.avaliacoes.filter((a) => String(a.medicoId) === String(medicoId));
  if (!reviews.length) return 0;
  return reviews.reduce((sum, item) => sum + Number(item.estrelas || 0), 0) / reviews.length;
}

function limiteMedico(medico) {
  return String(medico.especialidade || '').toUpperCase().includes('PEDIATRA') ? 2 : 3;
}

function formatEspecialidade(value) {
  if (!value) return 'Especialidade';
  return String(value).toLowerCase().replaceAll('_', ' ').replace(/(^|\s)\S/g, (letter) => letter.toUpperCase());
}

function labelStatus(status) {
  const labels = { AGENDADA: 'Agendada', REALIZADA: 'Realizada', CANCELADA: 'Cancelada', LISTA_ESPERA: 'Lista de espera', SEM_STATUS: 'Sem status' };
  return labels[status] || status || 'Sem status';
}

function statusType(status) {
  return { AGENDADA: 'info', REALIZADA: 'ok', CANCELADA: 'danger', LISTA_ESPERA: 'warn' }[status] || 'neutral';
}

function prioridadeType(value) {
  return { VERMELHO: 'danger', LARANJA: 'warn', AMARELO: 'warn', VERDE: 'ok', AZUL: 'info' }[value] || 'neutral';
}

function formatDate(value) {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(date);
}

function formatDateOnly(value) {
  if (!value) return '-';
  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short' }).format(date);
}

function money(value) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value || 0));
}

function stars(value) {
  const amount = Math.round(Number(value || 0));
  return '★★★★★'.slice(0, amount) + '☆☆☆☆☆'.slice(0, 5 - amount);
}

function valueOr(value) {
  return value === null || value === undefined || value === '' ? '-' : value;
}

function numberOrNull(value) {
  return value === '' || value === null || value === undefined ? null : Number.parseInt(value, 10);
}

function decimalOrNull(value) {
  return value === '' || value === null || value === undefined ? null : Number.parseFloat(value);
}

function normalize(text) {
  return String(text || '').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
}

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>'"]/g, (char) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[char]));
}

setupNavigation();
setupForms();
loadAll();
