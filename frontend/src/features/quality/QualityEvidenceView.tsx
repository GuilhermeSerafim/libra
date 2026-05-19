import {
  BadgeCheck,
  FileImage,
  GitBranch,
  Microscope,
  ShieldCheck,
  TestTube2,
} from 'lucide-react';

const metrics = [
  {
    label: 'Quality Gate',
    value: 'Passed',
    detail: 'O projeto atende aos criterios configurados no SonarQube.',
  },
  {
    label: 'Coverage on New Code',
    value: '100%',
    detail: 'Todo codigo novo analisado esta coberto por testes.',
  },
  {
    label: 'Overall Coverage',
    value: '90.1%',
    detail: 'Cobertura geral medida por JaCoCo e importada no SonarQube.',
  },
  {
    label: 'Duplications on New Code',
    value: '0.0%',
    detail: 'Nao ha duplicacao relevante no codigo novo.',
  },
  {
    label: 'Issues abertas',
    value: '0',
    detail: 'Bugs, vulnerabilidades e code smells reportados: zero.',
  },
  {
    label: 'Testes importados',
    value: '32',
    detail: 'Resultados de testes integrados ao fluxo de qualidade.',
  },
];

const evidencePaths = [
  'docs/evidencias/sonarqube/quality-gate-passed.png',
  'docs/evidencias/sonarqube/overall-code-quality.png',
  'docs/evidencias/sonarqube/new-code-coverage-duplication.png',
  'docs/evidencias/sonarqube/sonarqube-dashboard-overview.png',
];

const concepts = [
  {
    title: 'SonarQube',
    description:
      'Centraliza a analise estatica e mostra se o projeto passa no Quality Gate.',
  },
  {
    title: 'JaCoCo e cobertura',
    description:
      'Mede quanto do codigo foi exercitado pelos testes e alimenta o indicador de cobertura.',
  },
  {
    title: 'Testcontainers',
    description:
      'Permite testes de integracao com servicos reais em containers, reduzindo diferencas entre ambiente local e CI.',
  },
  {
    title: 'WireMock/VCR',
    description:
      'Isola chamadas externas com simulacoes ou gravacoes controladas, deixando testes mais previsiveis.',
  },
  {
    title: 'CI GitHub Actions',
    description:
      'Executa testes e analises automaticamente a cada mudanca, evitando depender de verificacao manual.',
  },
];

const oralExamPoints = [
  'Comece pelo Quality Gate Passed: ele resume que a entrega atingiu os criterios minimos de qualidade definidos.',
  'Explique que cobertura nao prova ausencia de bugs, mas mostra que os cenarios automatizados exercitam o codigo.',
  'Relacione os 0 bugs, 0 vulnerabilidades e 0 code smells com manutencao, seguranca e confiabilidade.',
  'Use Testcontainers para falar de testes de integracao com dependencias reais e ambiente reproduzivel.',
  'Use WireMock/VCR para explicar controle de dependencias externas, estabilidade dos testes e repetibilidade.',
  'Feche com GitHub Actions: a qualidade e verificada no fluxo de CI, antes de considerar a entrega pronta.',
];

export function QualityEvidenceView() {
  return (
    <section className="quality-page" aria-labelledby="quality-title">
      <header className="page-header">
        <div>
          <h1 id="quality-title">Evidencias de qualidade</h1>
          <p>
            Painel de apoio para entrega e prova oral, conectando metricas,
            ferramentas e conceitos de Qualidade de Software.
          </p>
        </div>
        <div className="quality-status" aria-label="Status do Quality Gate">
          <ShieldCheck size={20} aria-hidden="true" />
          Quality Gate Passed
        </div>
      </header>

      <div className="quality-metrics" aria-label="Metricas do SonarQube">
        {metrics.map((metric) => (
          <article className="metric-card quality-metric-card" key={metric.label}>
            <span className="quality-metric-label">{metric.label}</span>
            <strong className="quality-metric-value">{metric.value}</strong>
            <p>{metric.detail}</p>
          </article>
        ))}
      </div>

      <div className="quality-grid">
        <section className="panel quality-panel" aria-labelledby="evidence-title">
          <div className="quality-section-heading">
            <FileImage size={20} aria-hidden="true" />
            <h2 id="evidence-title">Evidencias no repositorio</h2>
          </div>
          <p>
            Os caminhos abaixo podem ser citados na apresentacao para comprovar
            as metricas vistas no SonarQube.
          </p>
          <ul className="quality-evidence-list">
            {evidencePaths.map((path) => (
              <li key={path}>
                <code>{path}</code>
              </li>
            ))}
          </ul>
        </section>

        <section className="panel quality-panel" aria-labelledby="concepts-title">
          <div className="quality-section-heading">
            <Microscope size={20} aria-hidden="true" />
            <h2 id="concepts-title">Conceitos aplicados</h2>
          </div>
          <div className="quality-concepts">
            {concepts.map((concept) => (
              <article className="quality-concept" key={concept.title}>
                <h3>{concept.title}</h3>
                <p>{concept.description}</p>
              </article>
            ))}
          </div>
        </section>
      </div>

      <section className="panel quality-panel" aria-labelledby="oral-title">
        <div className="quality-section-heading">
          <BadgeCheck size={20} aria-hidden="true" />
          <h2 id="oral-title">Como explicar na prova oral</h2>
        </div>
        <ol className="quality-oral-list">
          {oralExamPoints.map((point) => (
            <li key={point}>{point}</li>
          ))}
        </ol>
      </section>

      <section className="quality-flow" aria-label="Fluxo de qualidade">
        <div>
          <TestTube2 size={22} aria-hidden="true" />
          Testes automatizados
        </div>
        <div>
          <GitBranch size={22} aria-hidden="true" />
          GitHub Actions
        </div>
        <div>
          <ShieldCheck size={22} aria-hidden="true" />
          SonarQube aprovado
        </div>
      </section>
    </section>
  );
}
