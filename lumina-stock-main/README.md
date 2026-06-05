💡 Lumina Stock - Sistema de Gestão e Inteligência
Versão Web Fullstack (Spring Boot) | Versão Desktop (Java Swing)

O Lumina Stock é uma solução robusta para gerenciamento de lojas de iluminação. O projeto demonstra a evolução completa de um software, partindo de uma arquitetura Desktop clássica para uma aplicação Web moderna, focada em experiência do usuário (UX), Vendas Rápidas (PDV) e Inteligência de Negócios (BI).

📸 Visão Geral
O sistema foi desenhado para facilitar o dia a dia do lojista, oferecendo não apenas o controle de estoque, mas ferramentas visuais para entender quais produtos trazem mais retorno financeiro.

Funcionalidades Principais (Web)
🌐 Landing Page & Hub: Página inicial profissional e Menu centralizado para navegação intuitiva.

🛒 PDV com Carrinho (Ponto de Venda):

Adição de múltiplos itens ao carrinho sem recarregar a página (AJAX/Fetch API).

Cálculo automático de subtotal.

Botão flutuante (FAB) para acesso rápido ao checkout.

Baixa automática no estoque e contabilização de vendas ao finalizar.

📊 Dashboard de BI (Relatórios):

Gráfico de Rosca (Chart.js) interativo mostrando a participação de cada produto no lucro total.

Card de faturamento total acumulado.

Tabela detalhada de performance.

📦 Gestão de Estoque (CRUD):

Cadastro, Edição e Exclusão de produtos.

Alertas visuais para estoque baixo (texto vermelho).

🎨 UI/UX Responsiva: Interface construída com Bootstrap 5, totalmente adaptada para celulares e computadores, com tema Dark/Light nos menus.

🛠️ Tecnologias Utilizadas
Este projeto utiliza a stack padrão de mercado para desenvolvimento Java Corporativo:

Back-end
Java 17: Linguagem base.

Spring Boot 3: Framework principal.

Spring Data JPA: Camada de persistência (substituindo DAOs manuais).

Hibernate: ORM para mapeamento do banco de dados.

Maven: Gerenciamento de dependências.

Front-end
Thymeleaf: Template Engine para renderização no servidor.

Bootstrap 5: Framework CSS para layout e responsividade.

Bootstrap Icons: Biblioteca de ícones.

JavaScript (Vanilla): Lógica do carrinho de compras e comunicação assíncrona com o Back-end.

Chart.js: Biblioteca para geração dos gráficos de lucro.

Banco de Dados
MySQL 8: Banco de dados relacional.

🚀 Como Executar o Projeto
Siga os passos abaixo para rodar a aplicação na sua máquina local.

1. Configuração do Banco de Dados
Certifique-se de ter o MySQL instalado e rodando. Crie o banco de dados:

SQL

CREATE DATABASE lumina_stock;
USE lumina_stock;

-- O Spring Boot criará a tabela automaticamente, mas a estrutura é esta:
CREATE TABLE produto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    tipo VARCHAR(255),
    preco_custo DOUBLE,
    preco_venda DOUBLE,
    quantidade_estoque INT,
    quantidade_vendida INT DEFAULT 0
);
2. Configuração da Aplicação
Abra o arquivo src/main/resources/application.properties e configure suas credenciais do MySQL:

Properties

spring.datasource.url=jdbc:mysql://localhost:3306/lumina_stock
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI
3. Rodando o Servidor
Se estiver usando uma IDE (NetBeans/IntelliJ):

Abra o projeto.

Execute a classe principal LuminaWebApplication.java.

Aguarde o console mostrar: Started LuminaWebApplication.

4. Acessando
Abra seu navegador e acesse: 👉 http://localhost:8080

🧠 Destaques de Implementação
Carrinho de Compras (Front-end)
A lógica de vendas utiliza um array JavaScript local para gerenciar o estado do carrinho. Ao finalizar, um JSON é enviado para o endpoint /finalizar-venda, onde o Java processa a lista, valida o estoque de cada item e comita a transação.

Algoritmo de Lucratividade (Back-end)
O relatório utiliza uma query personalizada no Repositório para calcular o lucro real, e não apenas a margem:

Java

// Ordena pelo (Lucro Unitário * Volume de Vendas)
@Query("SELECT p FROM Produto p ORDER BY ((p.precoVenda - p.precoCusto) * p.quantidadeVendida) DESC")
👤 Autor
Desenvolvido por Equipe D. Projeto educacional focado em arquitetura de software, migração de sistemas e desenvolvimento Fullstack.