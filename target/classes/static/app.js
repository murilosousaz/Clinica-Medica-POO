const API = {
  pacientes: '/api/pacientes',
  medicos: '/api/medicos',
  enfermeiros: '/api/enfermeiros',
  triagens: '/api/triagens',
  consultas: '/api/consultas',
  avaliacoes: '/api/avaliacoes',
  estatisticas: '/api/estatisticas',
  contas: '/api/contas',
  csv: '/api/csv',
  auth: '/api/auth'
};

const state = {
  pacientes: [], medicos: [], enfermeiros: [], triagens: [], consultas: [], fila: [], avaliacoes: [], contas: [], estatisticas: {},
  session: null,
  currentSection: 'dashboard'
};

const pages = {
  dashboard: ['Dashboard', 'Visão geral dos dados e requisitos principais.'],
  pacientes: ['Pacientes', 'Cadastro, plano de saúde e prontuário.'],
  medicos: ['Médicos', 'Especialidades, pesquisa, avaliações e limite diário.'],
  enfermeiros: ['Enfermeiros', 'Equipe responsável pela triagem.'],
  consultas: ['Consultas', 'Agendamento, cancelamento, atendimento e lista de espera.'],
  triagens: ['Triagens', 'Classificação de risco e sinais vitais.'],
  prontuarios: ['Prontuários', 'Histórico clínico do paciente.'],
  avaliacoes: ['Avaliações', 'Texto e estrelas por consulta realizada.'],
  contas: ['Contas', 'Cobranças geradas para planos privados e pacientes sem SUS.'],
  dados: ['CSV & API', 'Exportação/importação e documentação Swagger.']
};

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => Array.from(document.querySelectorAll(selector));

async function apiRequest(url, options = {}) {
  const authHeaders = state.session?.token ? { 'Authorization': `Bearer ${state.session.token}` } : {};
  const response = await fetch(url, { headers: { 'Content-Type': 'application/json', ...authHeaders, ...(options.headers || {}) }, ...options });
  if (!response.ok) {
    let message = `${response.status} ${response.statusText}`;
    try { const payload = await response.json(); message = payload.message || payload.error || payload.detail || message; } catch (_) {}
    throw new Error(message);
  }
  if (response.status === 204) return null;
  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

async function loadResource(key, promise) {
  try { state[key] = await promise; }
  catch (error) { state[key] = Array.isArray(state[key]) ? [] : {}; return `${key}: ${error.message}`; }
  return null;
}

async function loadAll() {
  setLoginHelp('Carregando dados da API...');
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
  fillSelects();
  renderAll();
  const failures = errors.filter(Boolean);
  if (failures.length) setLoginHelp(`API parcialmente disponível: ${failures[0]}`);
  else setLoginHelp('Dados carregados. Entre com CPF e senha ou use o modo apresentação.');
  updateVisibility();
}

function updateVisibility() {
  const logged = Boolean(state.session);
  $('#login-view')?.classList.toggle('hidden', logged);
  $('#app-view')?.classList.toggle('hidden', !logged);
  if (logged) renderSession();
}

function renderAll() {
  renderSession(); applyRoleAccess(); renderDashboard(); renderPacientes(); renderMedicos(); renderEnfermeiros(); renderConsultas(); renderTriagens(); renderAvaliacoes(); renderContas();
  applyFilter($('#screen-filter')?.value || '');
}

function renderSession() {
  const session = state.session;
  const user = getLoggedUser();
  setText('#session-title', user?.nome || (session?.role === 'APRESENTACAO' ? 'Modo apresentação' : 'Sem usuário'));
  setText('#session-detail', roleDescription(session?.role));
}


const roleSections = {
  APRESENTACAO: ['dashboard','pacientes','medicos','enfermeiros','consultas','triagens','prontuarios','avaliacoes','contas','dados'],
  PACIENTE: ['dashboard','medicos','consultas','prontuarios','avaliacoes','contas'],
  MEDICO: ['dashboard','pacientes','medicos','consultas','prontuarios','avaliacoes'],
  ENFERMEIRO: ['dashboard','pacientes','enfermeiros','triagens','prontuarios']
};

function roleDescription(role) {
  return ({
    PACIENTE: 'Perfil paciente: pesquisa médicos, agenda, vê prontuário, avalia e acompanha contas.',
    MEDICO: 'Perfil médico: acompanha pacientes, realiza consultas, consulta prontuários e vê avaliações.',
    ENFERMEIRO: 'Perfil enfermeiro: registra triagens, acompanha pacientes e acessa prontuários para apoio ao atendimento.',
    APRESENTACAO: 'Modo apresentação: acesso livre para demonstrar todos os requisitos.'
  })[role] || 'Sem usuário autenticado';
}

function allowedSections() {
  return roleSections[state.session?.role] || [];
}

function canAccess(section) {
  return allowedSections().includes(section);
}

function applyRoleAccess() {
  const allowed = allowedSections();
  $$('.menu-item').forEach(item => item.classList.toggle('hidden', !allowed.includes(item.dataset.section)));
  $$('.screen').forEach(screen => screen.classList.toggle('role-hidden', !allowed.includes(screen.id)));
  toggleForm('#patient-form', state.session?.role === 'APRESENTACAO' || state.session?.role === 'ENFERMEIRO');
  toggleForm('#doctor-form', state.session?.role === 'APRESENTACAO');
  toggleForm('#nurse-form', state.session?.role === 'APRESENTACAO');
  toggleForm('#appointment-form', state.session?.role === 'APRESENTACAO' || state.session?.role === 'PACIENTE');
  toggleForm('#finish-form', state.session?.role === 'APRESENTACAO' || state.session?.role === 'MEDICO');
  toggleForm('#screening-form', state.session?.role === 'APRESENTACAO' || state.session?.role === 'ENFERMEIRO');
  toggleForm('#review-form', state.session?.role === 'APRESENTACAO' || state.session?.role === 'PACIENTE');
  $('#export-csv')?.toggleAttribute('disabled', !(state.session?.role === 'APRESENTACAO'));
  $('#import-csv')?.toggleAttribute('disabled', !(state.session?.role === 'APRESENTACAO'));
  if (state.currentSection && !allowed.includes(state.currentSection)) {
    goTo(allowed[0] || 'dashboard');
  }
}

function toggleForm(selector, enabled) {
  const form = $(selector); if (!form) return;
  form.classList.toggle('readonly-panel', !enabled);
  form.querySelectorAll('input, select, textarea, button').forEach(el => { el.disabled = !enabled; });
}

function roleScoped(items, patientKey='pacienteId', doctorKey='medicoId', nurseKey='enfermeiroId') {
  const role = state.session?.role;
  const id = String(state.session?.userId || '');
  if (role === 'PACIENTE') return items.filter(item => String(item[patientKey] || item.paciente?.id || '') === id);
  if (role === 'MEDICO') return items.filter(item => String(item[doctorKey] || item.medico?.id || '') === id);
  if (role === 'ENFERMEIRO') return items.filter(item => String(item[nurseKey] || item.enfermeiro?.id || '') === id);
  return items;
}

function renderDashboard() {
  renderRoleDashboardIntro();
  setText('#metric-pacientes', state.pacientes.length); setText('#metric-medicos', state.medicos.length); setText('#metric-consultas', roleScoped(state.consultas).length); setText('#metric-triagens', roleScoped(state.triagens).length); setText('#metric-fila', state.fila.length);
  const statuses = { AGENDADA: 0, REALIZADA: 0, CANCELADA: 0, LISTA_ESPERA: state.fila.length };
  roleScoped(state.consultas).forEach(c => { const key = c.status || 'SEM_STATUS'; statuses[key] = (statuses[key] || 0) + 1; });
  const max = Math.max(1, ...Object.values(statuses));
  $('#status-chart').innerHTML = Object.entries(statuses).map(([status, count]) => `<div class="chart-row searchable"><strong>${labelStatus(status)}</strong><div class="chart-track"><div class="chart-fill" style="width:${Math.max(5, count / max * 100)}%"></div></div><span>${count}</span></div>`).join('');
  const stats = state.estatisticas || {};
  const medico = stats.medicoMaisBemAvaliado;
  const especialidade = stats.especialidadeMaisProcurada;
  $('#stats-list').innerHTML = [
    statItem('Médico mais bem avaliado', medico ? `${medico.nome} • ${Number(medico.media || 0).toFixed(1)} ★` : 'Sem avaliações'),
    statItem('Especialidade mais procurada', especialidade ? `${formatEspecialidade(especialidade.especialidade)} • ${especialidade.consultas} consulta(s)` : 'Sem dados'),
    statItem('Consultas realizadas', String(stats.totalConsultasRealizadas ?? consultasRealizadas().length)),
    statItem('Média geral de avaliações', `${Number(stats.mediaGeralAvaliacoes || mediaGeralAvaliacoes()).toFixed(1)} ★`),
    statItem('Consultas por médico', resumoConsultasPorMedico())
  ].join('');
}


function renderRoleDashboardIntro() {
  const role = state.session?.role || 'APRESENTACAO';
  const title = {
    PACIENTE: 'Área do paciente',
    MEDICO: 'Área médica',
    ENFERMEIRO: 'Área de enfermagem',
    APRESENTACAO: 'Resumo operacional da clínica'
  }[role];
  const desc = {
    PACIENTE: 'Acompanhe suas consultas, pesquise médicos, consulte seu prontuário e avalie atendimentos já realizados.',
    MEDICO: 'Acompanhe sua agenda, realize consultas, visualize prontuários e analise avaliações recebidas.',
    ENFERMEIRO: 'Acompanhe pacientes, registre triagens, consulte histórico clínico quando necessário e monitore seus atendimentos.',
    APRESENTACAO: 'Use esta tela para demonstrar os requisitos gerais: entidades, consultas, lista de espera, prontuário, avaliações, CSV e estatísticas.'
  }[role];
  const hero = $('#dashboard .hero-card');
  if (hero) {
    const h2 = hero.querySelector('h2'); const p = hero.querySelector('p');
    if (h2) h2.textContent = title;
    if (p) p.textContent = desc;
  }
  const actions = $('#dashboard .hero-actions');
  if (!actions) return;
  const buttons = {
    PACIENTE: [['consultas','Agendar consulta'], ['prontuarios','Meu prontuário'], ['avaliacoes','Avaliar consulta']],
    MEDICO: [['consultas','Realizar consulta'], ['prontuarios','Ver prontuários'], ['avaliacoes','Avaliações']],
    ENFERMEIRO: [['triagens','Registrar triagem'], ['pacientes','Pacientes'], ['prontuarios','Prontuários']],
    APRESENTACAO: [['consultas','Agendar consulta'], ['triagens','Registrar triagem'], ['dados','CSV & API']]
  }[role] || [];
  actions.innerHTML = buttons.map(([section,label], index) => `<button class="button ${index ? 'button-secondary' : ''}" data-go="${section}" type="button">${label}</button>`).join('');
  actions.querySelectorAll('[data-go]').forEach(btn => btn.addEventListener('click', () => goTo(btn.dataset.go)));
}

function renderPacientes() {
  setText('#total-pacientes', `${state.pacientes.length} registro(s)`);
  $('#patients-table').innerHTML = state.pacientes.length ? state.pacientes.map(p => `<tr class="searchable"><td><strong>${escapeHtml(p.nome)}</strong><span class="subtext">${valueOr(p.idade)} ano(s)</span></td><td>${escapeHtml(p.cpf || '-')}</td><td>${escapeHtml(planoLabel(p))}</td><td>${escapeHtml(p.telefone || '-')}<span class="subtext">${escapeHtml(p.email || '-')}</span></td></tr>`).join('') : tableEmpty(4, 'Nenhum paciente cadastrado.');
}

function renderMedicos() {
  setText('#total-medicos', `${state.medicos.length} registro(s)`);
  const term = normalize($('#doctor-name-filter')?.value || '');
  const specialty = $('#doctor-specialty-filter')?.value || '';
  const selectedPatient = state.pacientes.find(p => String(p.id) === String($('#doctor-plan-filter')?.value || ''));
  const patientPlan = planoLabel(selectedPatient);
  const doctors = state.medicos.filter(m => (!term || normalize(m.nome).includes(term))
    && (!specialty || especialidadeKey(m.especialidade) === specialty)
    && (!selectedPatient || !temPlano(patientPlan) || (m.planosAtendidos || []).some(plano => normalize(plano) === normalize(patientPlan))));
  $('#doctor-search-list').innerHTML = doctors.length ? doctors.map(m => {
    const avg = Number(m.mediaAvaliacoes || medicoMedia(m.id) || 0); const reviews = ultimasAvaliacoes(m.id, 2);
    return `<article class="doctor-card searchable"><header><div><strong>${escapeHtml(m.nome)}</strong><small>CRM: ${escapeHtml(m.crm || '-')}</small></div>${badge(formatEspecialidade(m.especialidade), 'info')}</header><div class="rating">${stars(avg)} ${avg ? Number(avg).toFixed(1) : 'sem média'}</div><small>Limite diário: ${m.maxPacientesPorDia || limiteMedico(m)} paciente(s) • Valor fora do SUS: ${money(m.valorConsultaParticular || 100)}</small><div class="mini-list">${reviews.length ? reviews.map(r => `<span>“${escapeHtml(r.texto || 'Sem comentário')}” • ${r.estrelas}★</span>`).join('') : '<span>Sem avaliações recentes</span>'}</div></article>`;
  }).join('') : emptyState('Nenhum médico encontrado.');
}

function renderEnfermeiros() {
  setText('#total-enfermeiros', `${state.enfermeiros.length} registro(s)`);
  $('#nurses-list').innerHTML = state.enfermeiros.length ? state.enfermeiros.map(e => `<article class="list-item searchable"><header><div><strong>${escapeHtml(e.nome)}</strong><small>COREN: ${escapeHtml(e.coren || '-')}</small></div>${badge(e.turno || 'Turno', 'neutral')}</header><small>${valueOr(e.anosExperiencia)} ano(s) de experiência</small></article>`).join('') : emptyState('Nenhum enfermeiro cadastrado.');
}

function renderConsultas() {
  setText('#total-consultas', `${state.consultas.length} registro(s)`);
  $('#appointments-table').innerHTML = state.consultas.length ? state.consultas.map(c => `<tr class="searchable"><td>${escapeHtml(c.pacienteNome || '-')}</td><td>${escapeHtml(c.medicoNome || '-')}<span class="subtext">${formatEspecialidade(c.medicoEspecialidade)}</span></td><td>${formatDate(c.dataHora)}</td><td>${badge(labelStatus(c.status), statusType(c.status))}</td><td>${c.status === 'AGENDADA' ? `<button class="button-danger" type="button" onclick="cancelarConsulta('${escapeHtml(c.id)}')">Cancelar</button>` : '-'}</td></tr>`).join('') : tableEmpty(5, 'Nenhuma consulta cadastrada.');
  $('#wait-list').innerHTML = renderFila(state.fila);
  fillSelect('#finish-appointment', state.consultas.filter(c => c.status === 'AGENDADA'), 'Selecione uma consulta', c => `${c.pacienteNome || 'Paciente'} com ${c.medicoNome || 'Médico'} — ${formatDate(c.dataHora)}`);
}

function renderTriagens() {
  setText('#total-triagens', `${state.triagens.length} registro(s)`);
  const ordered = [...state.triagens].sort((a,b) => new Date(b.dataHora || b.data || 0) - new Date(a.dataHora || a.data || 0));
  $('#screenings-list').innerHTML = ordered.length ? ordered.map(t => `<article class="list-item searchable"><header><div><strong>${escapeHtml(t.pacienteNome || 'Paciente')}</strong><small>${escapeHtml(t.enfermeiroNome || 'Enfermeiro')} • ${formatDate(t.dataHora || t.data)}</small></div>${badge(t.prioridade || 'Prioridade', prioridadeType(t.prioridade))}</header><small><strong>Queixa:</strong> ${escapeHtml(t.queixaPrincipal || '-')}</small><small>Temp.: ${valueOr(t.temperatura)} • PA: ${valueOr(t.pressaoArterial)} • FC: ${valueOr(t.frequenciaCardiaca)}</small></article>`).join('') : emptyState('Nenhuma triagem registrada.');
}

function renderAvaliacoes() {
  setText('#total-avaliacoes', `${state.avaliacoes.length} registro(s)`);
  $('#reviews-list').innerHTML = state.avaliacoes.length ? state.avaliacoes.map(a => `<article class="review-card searchable"><header><div><strong>${escapeHtml(a.medicoNome || 'Médico')}</strong><small>${escapeHtml(a.pacienteNome || 'Paciente')}</small></div><span class="rating">${stars(a.estrelas)} ${a.estrelas || 0}★</span></header><p>${escapeHtml(a.texto || 'Sem comentário textual.')}</p></article>`).join('') : emptyState('Nenhuma avaliação registrada.');
}

function renderContas() {
  setText('#total-contas', `${state.contas.length} pendente(s)`);
  $('#accounts-list').innerHTML = state.contas.length ? state.contas.map(c => {
    const pacienteNome = c.pacienteNome || c.paciente?.nome || 'Paciente';
    const plano = c.paciente?.planoSaude?.nome || c.planoSaude || 'plano não informado';
    return `<article class="list-item searchable"><header><div><strong>${escapeHtml(pacienteNome)}</strong><small>Situação: ${escapeHtml(c.situacao || 'PENDENTE')} • Plano: ${escapeHtml(plano)}</small></div>${badge(money(c.valor), 'warn')}</header><small>${escapeHtml(c.descricao || 'Cobrança gerada porque apenas SUS é gratuito.')}</small></article>`;
  }).join('') : emptyState('Nenhuma conta pendente. Regra: SUS não gera conta; planos privados e pacientes sem plano geram cobrança.');
}

function fillSelects() {
  fillSelect('#appointment-patient', state.pacientes, 'Selecione um paciente', p => `${p.nome} — ${planoLabel(p)}`);
  fillSelect('#appointment-doctor', state.medicos, 'Selecione um médico', m => `${m.nome} — ${formatEspecialidade(m.especialidade)}`);
  fillSelect('#screening-patient', state.pacientes, 'Selecione um paciente', p => p.nome);
  fillSelect('#screening-nurse', state.session?.role === 'ENFERMEIRO' ? state.enfermeiros.filter(e => String(e.id) === String(state.session.userId)) : state.enfermeiros, 'Selecione um enfermeiro', e => `${e.nome} — ${e.coren || '-'}`);
  fillSelect('#record-patient', state.pacientes, 'Selecione um paciente', p => p.nome);
  fillSelect('#doctor-plan-filter', state.pacientes, 'Filtrar pelo plano de um paciente', p => `${p.nome} — ${planoLabel(p)}`);
  fillSelect('#review-appointment', consultasRealizadas(), 'Selecione consulta realizada', c => `${c.pacienteNome || 'Paciente'} com ${c.medicoNome || 'Médico'} — ${formatDate(c.dataHora)}`);
  fillSelect('#finish-appointment', state.consultas.filter(c => c.status === 'AGENDADA'), 'Selecione uma consulta', c => `${c.pacienteNome || 'Paciente'} com ${c.medicoNome || 'Médico'} — ${formatDate(c.dataHora)}`);
}

function fillSelect(selector, items, placeholder, labelFn) {
  const select = $(selector); if (!select) return;
  const previous = select.value;
  select.innerHTML = `<option value="">${escapeHtml(placeholder)}</option>` + items.map(item => `<option value="${escapeHtml(item.id)}">${escapeHtml(labelFn(item))}</option>`).join('');
  if (items.some(item => String(item.id) === previous)) select.value = previous;
}

function setupNavigation() {
  $$('.menu-item').forEach(btn => btn.addEventListener('click', () => goTo(btn.dataset.section)));
  $$('[data-go]').forEach(btn => btn.addEventListener('click', () => goTo(btn.dataset.go)));
  $('#sidebar-toggle')?.addEventListener('click', () => $('#sidebar').classList.toggle('open'));
}

function goTo(section) {
  if (state.session && !canAccess(section)) section = defaultSectionForRole(state.session.role);
  state.currentSection = section;
  $$('.menu-item').forEach(item => item.classList.toggle('active', item.dataset.section === section));
  $$('.screen').forEach(screen => screen.classList.toggle('active-screen', screen.id === section));
  const [title, desc] = pages[section] || pages.dashboard;
  setText('#page-title', title); setText('#page-description', desc);
  if ($('#screen-filter')) $('#screen-filter').value = '';
  applyFilter(''); $('#sidebar')?.classList.remove('open');
}
window.goTo = goTo;

function setupForms() {
  bindForm('#login-form', async form => {
    const data = formData(form);
    const login = await apiRequest(`${API.auth}/login`, { method: 'POST', body: JSON.stringify({ cpf: data.cpf, senha: data.senha, perfil: data.perfil }) });
    state.session = { role: login.perfil, userId: login.usuarioId, nome: login.nome, cpf: login.cpf, token: login.token };
    form.reset();
    updateVisibility();
    goTo(defaultSectionForRole(login.perfil));
    showAlert(login.mensagem || 'Login realizado com sucesso.');
  }, false);
  $('#presentation-login')?.addEventListener('click', () => {
    state.session = { role: 'APRESENTACAO', userId: null, nome: 'Modo apresentação', token: null };
    updateVisibility();
    goTo('dashboard');
  });
  $('#logout-button')?.addEventListener('click', () => {
    state.session = null;
    localStorage.removeItem('clinica.session');
    updateVisibility();
    goTo('dashboard');
  });

  bindForm('#patient-form', async form => { const d = formData(form); const nomePlano = (d.planoNome || '').trim(); await apiRequest(API.pacientes, { method: 'POST', body: JSON.stringify({ nome: d.nome, idade: numberOrNull(d.idade), cpf: d.cpf, telefone: d.telefone, email: d.email || null, senha: d.senha, planoSaude: { nome: nomePlano || 'Não tenho', numeroCarnetizacao: d.planoNumero || null, ativo: temPlano(nomePlano) } }) }); form.reset(); showAlert('Paciente cadastrado com senha de acesso.'); });
  bindForm('#doctor-form', async form => { const d = formData(form); d.valorConsultaParticular = decimalOrNull(d.valorConsultaParticular) || 100; d.planosAtendidos = (d.planosAtendidos || '').split(',').map(p => p.trim()).filter(Boolean); await apiRequest(API.medicos, { method: 'POST', body: JSON.stringify(d) }); form.reset(); showAlert('Médico cadastrado.'); });
  bindForm('#nurse-form', async form => { const d = formData(form); d.anosExperiencia = numberOrNull(d.anosExperiencia) || 0; await apiRequest(API.enfermeiros, { method: 'POST', body: JSON.stringify(d) }); form.reset(); showAlert('Enfermeiro cadastrado.'); });
  bindForm('#appointment-form', async form => { await apiRequest(API.consultas, { method: 'POST', body: JSON.stringify(formData(form)) }); form.reset(); showAlert('Consulta processada. Se a agenda estiver lotada, o paciente foi para a lista de espera.'); });
  bindForm('#screening-form', async form => { const d = formData(form); ['temperatura','peso','altura'].forEach(k => d[k] = decimalOrNull(d[k])); ['frequenciaCardiaca','frequenciaRespiratoria'].forEach(k => d[k] = numberOrNull(d[k])); await apiRequest(API.triagens, { method: 'POST', body: JSON.stringify(d) }); form.reset(); showAlert('Triagem registrada.'); });
  bindForm('#finish-form', async form => { const d = formData(form); const id = d.consultaId; delete d.consultaId; d.examesSolicitados = (d.examesSolicitados || '').split(',').map(x => x.trim()).filter(Boolean); await apiRequest(`${API.consultas}/${id}/realizar`, { method: 'PUT', body: JSON.stringify(d) }); form.reset(); showAlert('Consulta realizada e prontuário atualizado.'); });
  bindForm('#record-form', async form => { const { pacienteId } = formData(form); const items = await apiRequest(`${API.consultas}/paciente/${pacienteId}/prontuario`); renderProntuario(items || []); }, false);
  bindForm('#review-form', async form => { const d = formData(form); d.estrelas = Number(d.estrelas); await apiRequest(API.avaliacoes, { method: 'POST', body: JSON.stringify(d) }); form.reset(); showAlert('Avaliação registrada.'); });
  $('#export-csv')?.addEventListener('click', () => runCsv('exportar'));
  $('#import-csv')?.addEventListener('click', () => runCsv('importar'));
  $('#refresh-button')?.addEventListener('click', loadAll);
  $('#screen-filter')?.addEventListener('input', e => applyFilter(e.target.value));
  ['#doctor-name-filter','#doctor-specialty-filter','#doctor-plan-filter'].forEach(sel => $(sel)?.addEventListener('input', renderMedicos));
  $('#doctor-specialty-filter')?.addEventListener('change', renderMedicos);
}

function bindForm(selector, handler, reload = true) {
  const form = $(selector); if (!form) return;
  form.addEventListener('submit', async event => { event.preventDefault(); try { await handler(form); if (reload) await loadAll(); } catch (error) { showAlert(error.message, 'error'); } });
}

async function cancelarConsulta(id) { try { await apiRequest(`${API.consultas}/${id}/cancelar`, { method: 'PUT' }); showAlert('Consulta cancelada. A fila será promovida automaticamente quando aplicável.'); await loadAll(); } catch (e) { showAlert(e.message, 'error'); } }
window.cancelarConsulta = cancelarConsulta;

async function runCsv(action) { try { const result = await apiRequest(`${API.csv}/${action}`, { method: 'POST' }); $('#csv-output').textContent = JSON.stringify(result, null, 2); showAlert(`Operação CSV concluída: ${action}.`); } catch (e) { $('#csv-output').textContent = e.message; showAlert(e.message, 'error'); } }

function renderProntuario(items) {
  $('#medical-record-list').innerHTML = items.length ? items.map(c => `<article class="record-card searchable"><header><div><strong>${escapeHtml(c.medicoNome || 'Médico')}</strong><small>${formatDate(c.dataHora)}</small></div>${badge(money(c.valorPago), 'neutral')}</header><small><strong>Paciente:</strong> ${escapeHtml(c.pacienteNome || '-')}</small><small><strong>Sintomas:</strong> ${escapeHtml(c.sintomas || '-')}</small><small><strong>Diagnóstico:</strong> ${escapeHtml(c.diagnostico || '-')}</small><small><strong>Tratamento:</strong> ${escapeHtml(c.tratamentoSugerido || '-')}</small><small><strong>Medicamentos:</strong> ${escapeHtml(c.medicamentos || '-')}</small><small><strong>Exames:</strong> ${escapeHtml((c.examesSolicitados || []).join(', ') || '-')}</small></article>`).join('') : emptyState('Nenhuma consulta realizada para este paciente.');
}

function renderFila(items) { return items.length ? items.map((f, i) => `<article class="list-item searchable"><header><div><strong>#${i + 1} ${escapeHtml(f.pacienteNome || 'Paciente')}</strong><small>${escapeHtml(f.medicoNome || 'Médico')} • ${formatDate(f.dataConsulta || f.dataDesejada || f.dataHora)}</small></div>${badge('Aguardando', 'warn')}</header></article>`).join('') : emptyState('Lista de espera vazia.'); }

function applyFilter(term) { const normalized = normalize(term); const active = $('.active-screen'); if (!active) return; active.querySelectorAll('.searchable').forEach(el => el.classList.toggle('filtered-out', normalized && !normalize(el.textContent).includes(normalized))); }
function getLoggedUser() { if (!state.session) return null; const sources = { MEDICO: state.medicos, PACIENTE: state.pacientes, ENFERMEIRO: state.enfermeiros }; const source = sources[state.session.role] || []; return source.find(u => String(u.id) === String(state.session.userId)) || { nome: state.session.nome, cpf: state.session.cpf }; }
function consultasRealizadas() { return state.consultas.filter(c => c.status === 'REALIZADA'); }
function medicoMedia(id) { const reviews = state.avaliacoes.filter(a => String(a.medicoId) === String(id) || String(a.medico?.id) === String(id)); if (!reviews.length) return 0; return reviews.reduce((s,a) => s + Number(a.estrelas || 0), 0) / reviews.length; }
function ultimasAvaliacoes(id, limit) { return state.avaliacoes.filter(a => String(a.medicoId) === String(id) || String(a.medico?.id) === String(id)).slice(-limit).reverse(); }
function mediaGeralAvaliacoes() { return state.avaliacoes.length ? state.avaliacoes.reduce((s,a) => s + Number(a.estrelas || 0), 0) / state.avaliacoes.length : 0; }
function resumoConsultasPorMedico() { const counts = {}; state.consultas.forEach(c => { if (c.medicoNome) counts[c.medicoNome] = (counts[c.medicoNome] || 0) + 1; }); const top = Object.entries(counts).sort((a,b) => b[1]-a[1]).slice(0,2); return top.length ? top.map(([n,c]) => `${n}: ${c}`).join(' • ') : 'Sem consultas'; }
function limiteMedico(m) { return normalize(m.especialidade).includes('pediatr') ? 2 : 3; }
function especialidadeKey(v) { const n = normalize(v); if (n.includes('clinico')) return 'CLINICO_GERAL'; if (n.includes('cardio')) return 'CARDIOLOGISTA'; if (n.includes('pediatr')) return 'PEDIATRA'; if (n.includes('dermato')) return 'DERMATOLOGISTA'; return String(v || ''); }
function planoLabel(p) { return p?.planoSaude?.nome || p?.planoNome || 'Não tenho'; }
function temPlano(nome) { const n = normalize(nome); return Boolean(n && n !== 'nao tenho' && n !== 'não tenho' && n !== 'sem plano'); }
function ehSus(nome) { const n = normalize(nome); return n === 'sus' || n === 'sistema unico de saude' || n === 'sistema único de saúde'; }
function statItem(label, value) { return `<div class="stat-item searchable"><strong>${escapeHtml(label)}</strong><span>${escapeHtml(value)}</span></div>`; }
function badge(text, type='neutral') { return `<span class="badge ${type}">${escapeHtml(text)}</span>`; }
function emptyState(text) { return `<div class="empty-state searchable">${escapeHtml(text)}</div>`; }
function tableEmpty(cols, text) { return `<tr><td colspan="${cols}">${emptyState(text)}</td></tr>`; }
function setText(selector, value) { const el = $(selector); if (el) el.textContent = value; }
function setLoginHelp(text) { setText('#login-help', text); }
function showAlert(message, type='success') { const el = $('#alert'); if (!el) return; el.textContent = message; el.className = `alert ${type === 'error' ? 'error' : ''}`; setTimeout(() => el.classList.add('hidden'), 4500); }
function formData(form) { return Object.fromEntries(new FormData(form).entries()); }
function formatEspecialidade(v) { return v ? String(v).toLowerCase().replaceAll('_',' ').replace(/(^|\s)\S/g, l => l.toUpperCase()) : 'Especialidade'; }
function labelStatus(s) { return ({ AGENDADA:'Agendada', REALIZADA:'Realizada', CANCELADA:'Cancelada', LISTA_ESPERA:'Lista de espera', SEM_STATUS:'Sem status' })[s] || s || 'Sem status'; }
function statusType(s) { return ({ AGENDADA:'info', REALIZADA:'ok', CANCELADA:'danger', LISTA_ESPERA:'warn' })[s] || 'neutral'; }
function prioridadeType(v) { return ({ VERMELHO:'danger', LARANJA:'warn', AMARELO:'warn', VERDE:'ok', AZUL:'info' })[v] || 'neutral'; }
function formatDate(v) { if (!v) return '-'; const d = new Date(v); return Number.isNaN(d.getTime()) ? v : new Intl.DateTimeFormat('pt-BR', { dateStyle:'short', timeStyle:'short' }).format(d); }
function money(v) { return new Intl.NumberFormat('pt-BR', { style:'currency', currency:'BRL' }).format(Number(v || 0)); }
function stars(v) { const n = Math.max(0, Math.min(5, Math.round(Number(v || 0)))); return '★★★★★'.slice(0,n) + '☆☆☆☆☆'.slice(0,5-n); }
function valueOr(v) { return v === null || v === undefined || v === '' ? '-' : v; }
function numberOrNull(v) { return v === '' || v === null || v === undefined ? null : Number.parseInt(v, 10); }
function decimalOrNull(v) { return v === '' || v === null || v === undefined ? null : Number.parseFloat(v); }
function normalize(t) { return String(t || '').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, ''); }
function defaultSectionForRole(role) { return ({ PACIENTE: 'dashboard', MEDICO: 'consultas', ENFERMEIRO: 'triagens', APRESENTACAO: 'dashboard' })[role] || 'dashboard'; }
function escapeHtml(v) { return String(v ?? '').replace(/[&<>'"]/g, ch => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;', "'":'&#39;', '"':'&quot;' }[ch])); }

// A aplicação sempre inicia na tela de login.
// Sessões anteriores não são restauradas para atender ao fluxo exigido na apresentação.
localStorage.removeItem('clinica.session');
updateVisibility();
setupNavigation();
setupForms();
loadAll();
