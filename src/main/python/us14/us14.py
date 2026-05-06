# =============================================================
# US14 - Análise Estatística Descritiva
# =============================================================
# Célula 1 - Importações
# =============================================================

import pandas as pd
import matplotlib.pyplot as plt
import statistics as st
from scipy import stats


# =============================================================
# Célula 2 - Leitura e Preparação dos Dados
# =============================================================

df = pd.read_csv('dataset1_declarations.csv')

# Converter a coluna de datas para o tipo datetime
df['declaration_date'] = pd.to_datetime(df['declaration_date'])

# Selecionar apenas a declaração mais recente de cada agente
df_recent = (
    df.sort_values('declaration_date', ascending=False)
      .drop_duplicates(subset='agent_id', keep='first')
      .reset_index(drop=True)
)

# Calcular as variáveis de interesse
# total_income inclui salário bruto + rendimentos secundários (consultoria e membros de conselho)
df_recent['total_income'] = (df_recent['gross_salary']
                             + df_recent['side_income_consulting']
                             + df_recent['side_income_board_memberships'])
df_recent['total_assets'] = (df_recent['assets_in_real_estate']
                             + df_recent['assets_in_vehicles']
                             + df_recent['assets_in_stocks'])

print('Número de agentes políticos (declaração mais recente):', len(df_recent))
df_recent[['agent_id', 'declaration_date', 'total_income', 'total_assets']].head()


# =============================================================
# Célula 3 - Medidas de Localização Central
# =============================================================

for variavel, label in [('total_income', 'Rendimento Total'), ('total_assets', 'Ativo Total')]:
    dados = df_recent[variavel].dropna()

    media   = dados.mean()
    mediana = dados.median()
    moda    = dados.mode()[0]

    print(f'{"="*55}')
    print(f'  {label.upper()}')
    print(f'{"="*55}')
    print(f'  Média    (x̄)  : {media:>15,.2f} €')
    print(f'  Mediana  (x̃)  : {mediana:>15,.2f} €')
    print(f'  Moda          : {moda:>15,.2f} €')
    print()


# =============================================================
# Célula 4 - Medidas de Localização Não Central (Quantis)
# =============================================================

for variavel, label in [('total_income', 'Rendimento Total'), ('total_assets', 'Ativo Total')]:
    dados = df_recent[variavel].dropna().tolist()

    quartis   = st.quantiles(dados, n=4)
    decis     = st.quantiles(dados, n=10)
    percentis = st.quantiles(dados, n=100)

    print(f'{"="*55}')
    print(f'  {label.upper()} — Quantis')
    print(f'{"="*55}')
    print(f'  Quartil 1 (q1 = p25)  : {quartis[0]:>12,.2f} €')
    print(f'  Quartil 2 (q2 = p50)  : {quartis[1]:>12,.2f} €  ← Mediana')
    print(f'  Quartil 3 (q3 = p75)  : {quartis[2]:>12,.2f} €')
    print()
    print(f'  Decil 1  (d1 = p10)   : {decis[0]:>12,.2f} €')
    print(f'  Decil 5  (d5 = p50)   : {decis[4]:>12,.2f} €')
    print(f'  Decil 9  (d9 = p90)   : {decis[8]:>12,.2f} €')
    print()
    print(f'  Percentil 10  (p10)   : {percentis[9]:>12,.2f} €')
    print(f'  Percentil 25  (p25)   : {percentis[24]:>12,.2f} €')
    print(f'  Percentil 75  (p75)   : {percentis[74]:>12,.2f} €')
    print(f'  Percentil 90  (p90)   : {percentis[89]:>12,.2f} €')
    print()


# =============================================================
# Célula 5 - Medidas de Variabilidade
# =============================================================

for variavel, label in [('total_income', 'Rendimento Total'), ('total_assets', 'Ativo Total')]:
    dados = df_recent[variavel].dropna()

    amplitude     = dados.max() - dados.min()
    quartis_v     = st.quantiles(dados.tolist(), n=4)
    q1, q3        = quartis_v[0], quartis_v[2]
    amp_iq        = q3 - q1
    variancia     = dados.var()       # variância amostral (divisor n-1)
    desvio_padrao = dados.std()       # desvio padrão amostral
    coef_variacao = desvio_padrao / abs(dados.mean())

    print(f'{"="*55}')
    print(f'  {label.upper()} — Variabilidade')
    print(f'{"="*55}')
    print(f'  Amplitude total (r)    : {amplitude:>15,.2f} €')
    print(f'  Amplitude interquartil : {amp_iq:>15,.2f} €')
    print(f'  Variância amostral (s²): {variancia:>15,.2f}')
    print(f'  Desvio padrão (s)      : {desvio_padrao:>15,.2f} €')
    print(f'  Coef. de variação (cv) : {coef_variacao:>14.4f}  ({coef_variacao*100:.2f}%)')
    print()


# =============================================================
# Célula 6 - Medidas de Assimetria e Curtose
# =============================================================

for variavel, label in [('total_income', 'Rendimento Total'), ('total_assets', 'Ativo Total')]:
    dados = df_recent[variavel].dropna()

    # bias=False  → coeficiente amostral (como nas aulas)
    # fisher=False → a4 real, não o excesso de curtose
    assimetria = stats.skew(dados, bias=False)
    curtose    = stats.kurtosis(dados, fisher=False)

    if assimetria > 0:
        interp_assim = 'enviesada à DIREITA (cauda à direita)'
    elif assimetria < 0:
        interp_assim = 'enviesada à ESQUERDA (cauda à esquerda)'
    else:
        interp_assim = 'SIMÉTRICA'

    if curtose > 3:
        interp_kurt = 'mais ESGUIA do que a Normal (caudas mais pesadas)'
    elif curtose < 3:
        interp_kurt = 'mais ACHATADA do que a Normal (caudas mais leves)'
    else:
        interp_kurt = 'igual à distribuição NORMAL'

    print(f'{"="*60}')
    print(f'  {label.upper()} — Assimetria e Curtose')
    print(f'{"="*60}')
    print(f'  Coef. de assimetria (a3) : {assimetria:.4f}')
    print(f'  Interpretação            : Distribuição {interp_assim}')
    print()
    print(f'  Coef. de curtose (a4)    : {curtose:.4f}')
    print(f'  Interpretação            : Distribuição {interp_kurt}')
    print()


# =============================================================
# Célula 7 - Histogramas
# =============================================================

fig, axes = plt.subplots(1, 2, figsize=(14, 5))

variaveis = [
    ('total_income', 'Rendimento Total (€)', axes[0]),
    ('total_assets', 'Ativo Total (€)',      axes[1])
]

for variavel, xlabel, ax in variaveis:
    dados   = df_recent[variavel].dropna()
    media   = dados.mean()
    mediana = dados.median()

    ax.hist(dados, bins='auto', color='steelblue', edgecolor='white', alpha=0.85)

    ax.axvline(media,   color='red',    linestyle='--', linewidth=1.5,
               label=f'Média = {media:,.0f} €')
    ax.axvline(mediana, color='orange', linestyle='-',  linewidth=1.5,
               label=f'Mediana = {mediana:,.0f} €')

    ax.set_title(f'Distribuição de {xlabel}', fontsize=12)
    ax.set_xlabel(xlabel, fontsize=10)
    ax.set_ylabel('Frequência absoluta', fontsize=10)
    ax.legend(fontsize=9)
    ax.grid(axis='y', linestyle='--', alpha=0.4)

plt.suptitle('US14 — Histogramas: Rendimento Total e Ativo Total\n'
             '(declaração mais recente por agente)', fontsize=13, y=1.02)
plt.tight_layout()
plt.savefig('US14_histogramas.svg', format='svg', bbox_inches='tight')
plt.show()


# =============================================================
# Célula 8 - Boxplots
# =============================================================

fig, axes = plt.subplots(1, 2, figsize=(14, 6))

variaveis = [
    ('total_income', 'Rendimento Total (€)', axes[0]),
    ('total_assets', 'Ativo Total (€)',      axes[1])
]

for variavel, ylabel, ax in variaveis:
    dados = df_recent[variavel].dropna()

    ax.boxplot(dados,
               vert=True,
               patch_artist=True,
               boxprops=dict(facecolor='lightsteelblue', color='steelblue'),
               medianprops=dict(color='red', linewidth=2),
               whiskerprops=dict(color='steelblue'),
               capprops=dict(color='steelblue'),
               flierprops=dict(marker='o', color='steelblue', alpha=0.5))

    quartis_b = st.quantiles(dados.tolist(), n=4)
    q1, q3    = quartis_b[0], quartis_b[2]
    amp_iq    = q3 - q1
    lim_inf = q1 - 1.5 * amp_iq
    lim_sup = q3 + 1.5 * amp_iq
    outliers = dados[(dados < lim_inf) | (dados > lim_sup)]

    ax.set_title(f'Boxplot — {ylabel}', fontsize=12)
    ax.set_ylabel(ylabel, fontsize=10)
    ax.set_xticks([])
    ax.grid(axis='y', linestyle='--', alpha=0.4)

    print(f'{ylabel}: {len(outliers)} outlier(s) detetado(s)')
    if len(outliers) > 0:
        print(f'  Valores: {sorted(outliers.tolist())}')

plt.suptitle('US14 — Boxplots: Rendimento Total e Ativo Total\n'
             '(declaração mais recente por agente)', fontsize=13, y=1.02)
plt.tight_layout()
plt.savefig('US14_boxplots.svg', format='svg', bbox_inches='tight')
plt.show()


# =============================================================
# Célula 9 - Resumo Estatístico Completo
# =============================================================

resumo = pd.DataFrame()

for variavel, label in [('total_income', 'Rendimento Total'), ('total_assets', 'Ativo Total')]:
    dados      = df_recent[variavel].dropna()
    dados_list = dados.tolist()
    quartis    = st.quantiles(dados_list, n=4)

    resumo[label] = pd.Series({
        'n (observações)'         : len(dados),
        'Média (x̄)'              : round(dados.mean(), 2),
        'Mediana (x̃)'            : round(dados.median(), 2),
        'Moda'                    : round(dados.mode()[0], 2),
        'Quartil 1 (q1)'          : round(quartis[0], 2),
        'Quartil 3 (q3)'          : round(quartis[2], 2),
        'Amplitude total (r)'     : round(dados.max() - dados.min(), 2),
        'Ampl. interquartil (rq)' : round(quartis[2] - quartis[0], 2),
        'Variância amostral (s²)' : round(dados.var(), 2),
        'Desvio padrão (s)'       : round(dados.std(), 2),
        'Coef. variação (cv)'     : round(dados.std() / abs(dados.mean()), 4),
        'Coef. assimetria (a3)'   : round(stats.skew(dados, bias=False), 4),
        'Coef. curtose (a4)'      : round(stats.kurtosis(dados, fisher=False), 4),
    })

print('RESUMO ESTATÍSTICO COMPLETO — US14')
print('=' * 60)
print(resumo.to_string())
