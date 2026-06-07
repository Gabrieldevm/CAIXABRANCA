# Atividade Individual: Análise e Teste de Caixa Branca

## 1. Introdução
O presente documento detalha a revisão e refatoração de um algoritmo Java focado em autenticação de usuários. O objetivo principal da atividade é aplicar conceitos avançados de Teste de Caixa Branca, inspecionando falhas de segurança estáticas e mapeando os caminhos lógicos da aplicação para assegurar que ela tenha qualidade estrutural e suporte testes unitários com eficácia.

## 2. Análise Estática do Código
Durante a varredura estática, o código se mostrou deficiente em diversos aspectos arquiteturais e de boas práticas:
* **Injeção de SQL (SQL Injection):** O problema mais severo. A técnica de concatenar a senha e o login diretamente na variável `sql` abria as portas para invasões no banco de dados.
* **Fuga de Memória (Memory Leak):** A não utilização dos comandos `.close()` no encerramento da execução da query gerava acúmulo de conexões ociosas, o que fatalmente derrubaria o servidor.
* **Quebra de Encapsulamento:** Variáveis utilizadas estritamente dentro da validação (`nome` e `result`) foram declaradas globalmente na classe.
* **Omissão de Exceções:** Os blocos `catch` originais suprimiam os erros em vez de logá-los, dificultando o rastreio (debug).

## 3. Grafo de Fluxo
A representação das decisões do método refatorado encontra-se modelada abaixo. 

<img width="1184" height="768" alt="DIAGRAMAGRAFO drawio" src="https://github.com/user-attachments/assets/35b7af48-de3a-49a1-847c-89b7b527bece" />


## 4. Complexidade Ciclomática
Para assegurar a cobertura de todos os cenários nos testes estruturais, a complexidade foi calculada baseando-se no grafo acima:
* **Fórmula Aplicada:** V(G) = E - N + 2
* **Valores:** Onde $E$ (arestas) = 12 e $N$ (nós) = 9.
* **Cálculo Matemático:** V(G) = 12 - 9 + 2 = **5**

Resultado: Existem 5 caminhos lógicos distintos dentro do método atualizado.

## 5. Caminhos Básicos
Identificamos os 5 cenários independentes para testes:
* **Caminho 1 (Validação de Input):** Nós `1 -> 2 -> 9`. Usuário envia campos nulos ou em branco e é barrado logo no início.
* **Caminho 2 (Queda do Banco):** Nós `1 -> 3 -> 4 -> 9`. Credenciais preenchidas, porém a conexão com o banco não é estabelecida (timeout).
* **Caminho 3 (Exceção de Execução):** Nós `1 -> 3 -> 5 -> 8 -> 9`. Ocorre um problema interno de sintaxe SQL ou rede, acionando o catch e retornando false.
* **Caminho 4 (Login Inválido):** Nós `1 -> 3 -> 5 -> 6 -> 9`. Processo corre perfeitamente, mas a dupla usuário/senha não consta nos registros.
* **Caminho 5 (Login Bem-sucedido):** Nós `1 -> 3 -> 5 -> 6 -> 7 -> 9`. Caminho ótimo. Credenciais batem, variável muda de estado e o token/true é retornado.

## 6. Melhorias Implementadas
O código fonte foi completamente reescrito para adequação ao mercado:
1. **Try-with-Resources:** Adotamos este padrão do Java para instanciar a `Connection`, `PreparedStatement` e o `ResultSet`. Isso elimina a necessidade do bloco *finally*, pois o Java fecha os recursos automaticamente em cascata.
2. **Defesa contra NullPointer:** Inserida trava lógica para impedir que a verificação continue se não houver conexão ativa com o banco.
3. **Prepared Statement:** Substituição obrigatória da manipulação manual de strings, blindando o login contra injeções e ataques web.

## 7. Conclusão
A prática do Teste de Caixa Branca evidencia que softwares que "parecem funcionar" na interface muitas vezes carregam bombas-relógio em sua arquitetura. Identificar gargalos de conexão e brechas de segurança através do rastreio do fluxo de execução é papel fundamental do Desenvolvedor/QA. O código entregue agora possui estabilidade, encapsulamento e segurança robustos para entrar em fase de produção.

## AUTOR: **GABRIEL**
