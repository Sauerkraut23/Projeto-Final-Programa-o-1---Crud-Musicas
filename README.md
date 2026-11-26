## Resumo
CRUD web para cadastrar itens colecionáveis de música vinis, CDs, fitas K7, DVDs, pôsteres,
camisetas e instrumentos musicais, e publicar postagens sobre eles para outras contas verem.

## O que foi usado
Java 25.0.5.0.3 · Jakarta Servlet 6 / JSP / JSTL · Tomcat 11.0.29 (Deve funcionar em tomcat 10.1 +) · PostgreSQL + pgAdmin 4 ·
Maven · Bootstrap 5 · jBCrypt (hash de senha)

## Estrutura
banco/projeto_final_programacao_1.sql > Código do Banco de Dados para rodar no PgAdmin4, eu usei o PostgreSQL
src/main/java
  util/        Arquivos, Senha, package-info > Upload de imagens e o BCrypt
  model/       Postagem, Projeto, Usuario > Dados 
  filter/      Autenticacao, package-info > Barramento do Login
  dao/         CadastroProjetos, PostagemDAO, UsuarioDAO, ConexaoDB, package-info > JBDC e o SQL
  controller/  ArquivoController, InicioController, PostagemController,
               ItemController, UsuarioController > rotas e regras de cada tela
src/main/resources/db.properties > conexão com o Banco de Dados
src/main/webapp/WEB-INF/views/*.jsp > Telas que aparecem no projeto

## Como rodar
1. No pgAdmin 4: rode a **PARTE 1** do `banco/projeto_final_programacao_1.sql` (conectado em `postgres`),
   depois a **PARTE 2** dentro do banco novo.
2. Em `db.properties`, coloque a senha do seu PostgreSQL.
3. Rode os comandos no cmd do VScode `mvn clean package`.
4. Copie o `.war` para a pasta `webapps` do Tomcat, com esse comando `copy colecionaveis-musica\target\crud-musica.war C:\tomcat\webapps\` e inicie o Tomcat no terminal CMD do VSCode, usando 
`set JAVA_HOME=C:\Program Files\Java\jdk-25.0.5.0.3`, (a versão do java deve ser alterada conforme a versão que você tenha ou queira usar), e depois rodar essa sequência de comandos `cd C:\tomcat\bin` e 
`catalina.bat run`.
5. Abra <http://localhost:8080/crud-musicas/> em qualquer navegador.