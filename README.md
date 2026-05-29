# 💡 Lumina Stock - Ecossistema Corporativo

Sistema de Gestão de Estoque estruturado em uma arquitetura de microserviços, desenvolvido em Java com Spring Boot.

## 🏗️ Arquitetura do Sistema

O ecossistema é composto por serviços independentes que se comunicam via APIs REST (RestTemplate):

* **Lumina Web (Porta 8080):** Aplicação principal (Front-end Thymeleaf e lógica de negócios).
* **Lumina Auth (Porta 8083):** Microserviço focado em Segurança (IAM), responsável por gerar QR Codes e validar a matemática dos tokens TOTP (Google Authenticator/Authy).
* **Lumina Report (Portas 8081/8082):** Microserviço dedicado à geração e auditoria de planilhas Excel.

## 🔒 Segurança (Zero Trust)
Implementação de *Identity and Access Management* com Duplo Fator de Autenticação (NFA). Um `HandlerInterceptor` atua como muralha de segurança, isolando usuários não autenticados em uma área restrita até a validação do 2FA.

## 🚀 Como Executar o Projeto

1. Certifique-se de ter o **Java 17+** e o **MySQL** instalados.
2. Crie o banco de dados no MySQL: `CREATE DATABASE lumina_stock;`
3. Inicie os microserviços na seguinte ordem para evitar falhas de comunicação:
   * Inicie o `lumina-auth` (Porta 8083)
   * Inicie o `lumina-report`
   * Inicie o `lumina-web` (Porta 8080)
4. Acesse a aplicação em: `http://localhost:8080`