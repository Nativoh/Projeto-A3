-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: controle_financeiro
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int DEFAULT NULL,
  `nome` varchar(100) NOT NULL,
  `tipo` enum('INCOME','EXPENSE') NOT NULL,
  `cor` varchar(7) DEFAULT '#6B7280',
  `icone` varchar(50) DEFAULT NULL,
  `criado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `categorias_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` (`id`, `usuario_id`, `nome`, `tipo`, `cor`, `icone`, `criado_em`) VALUES (1,NULL,'Salario','INCOME','#6B7280','briefcase','2026-04-27 22:38:06'),(2,NULL,'Freelance','INCOME','#6B7280','laptop','2026-04-27 22:38:06'),(3,NULL,'Alimentacao','EXPENSE','#6B7280','utensils','2026-04-27 22:38:06'),(4,NULL,'Transporte','EXPENSE','#6B7280','car','2026-04-27 22:38:06'),(5,NULL,'Moradia','EXPENSE','#6B7280','home','2026-04-27 22:38:06'),(6,NULL,'Saude','EXPENSE','#6B7280','heart','2026-04-27 22:38:06'),(7,NULL,'Lazer','EXPENSE','#6B7280','smile','2026-04-27 22:38:06'),(8,NULL,'Educacao','EXPENSE','#6B7280','book','2026-04-27 22:38:06');
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contribuicoes_meta`
--

DROP TABLE IF EXISTS `contribuicoes_meta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contribuicoes_meta` (
  `id` int NOT NULL AUTO_INCREMENT,
  `meta_id` int NOT NULL,
  `valor` decimal(15,2) NOT NULL,
  `observacao` varchar(255) DEFAULT NULL,
  `data_contribuicao` date NOT NULL,
  `criado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `meta_id` (`meta_id`),
  CONSTRAINT `contribuicoes_meta_ibfk_1` FOREIGN KEY (`meta_id`) REFERENCES `metas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contribuicoes_meta`
--

LOCK TABLES `contribuicoes_meta` WRITE;
/*!40000 ALTER TABLE `contribuicoes_meta` DISABLE KEYS */;
/*!40000 ALTER TABLE `contribuicoes_meta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dicas_financeiras`
--

DROP TABLE IF EXISTS `dicas_financeiras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dicas_financeiras` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(200) NOT NULL,
  `conteudo` text NOT NULL,
  `categoria` enum('GASTO','POUPANCA','INVESTIMENTO','CONSCIENTE') NOT NULL,
  `condicao_gatilho` enum('RISCO','ATENCAO','SAUDAVEL') NOT NULL,
  `ativo` tinyint(1) NOT NULL DEFAULT '1',
  `criado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dicas_financeiras`
--

LOCK TABLES `dicas_financeiras` WRITE;
/*!40000 ALTER TABLE `dicas_financeiras` DISABLE KEYS */;
INSERT INTO `dicas_financeiras` (`id`, `titulo`, `conteudo`, `categoria`, `condicao_gatilho`, `ativo`, `criado_em`) VALUES (1,'Atenção com os gastos!!','Seus gastos estão acima de 80% da sua renda Revise seus orçamentos','GASTO','RISCO',1,'2026-04-27 22:38:06'),(2,'Situação critica','Você ultrapassou 90% da renda em despesas. Corte gastos não essenciais.','GASTO','RISCO',1,'2026-04-27 22:38:06'),(3,'Parabéns! Hora de investir','Seus gastos estão controlados. Considere aplicar o saldo em renda fixa.','INVESTIMENTO','SAUDAVEL',1,'2026-04-27 22:38:06');
/*!40000 ALTER TABLE `dicas_financeiras` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metas`
--

DROP TABLE IF EXISTS `metas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `nome` varchar(100) NOT NULL,
  `descricao` text,
  `valor_alvo` decimal(15,2) NOT NULL,
  `valor_atual` decimal(15,2) NOT NULL DEFAULT '0.00',
  `prazo` date NOT NULL,
  `status` enum('ATIVA','CONCLUIDA','CANCELADA') NOT NULL DEFAULT 'ATIVA',
  `criado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `atualizado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_metas_usuario_status` (`usuario_id`,`status`),
  CONSTRAINT `metas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metas`
--

LOCK TABLES `metas` WRITE;
/*!40000 ALTER TABLE `metas` DISABLE KEYS */;
/*!40000 ALTER TABLE `metas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orcamentos`
--

DROP TABLE IF EXISTS `orcamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orcamentos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `categoria_id` int NOT NULL,
  `valor_limite` decimal(15,2) NOT NULL,
  `mes` tinyint NOT NULL,
  `ano` smallint NOT NULL,
  `criado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_orcamento` (`usuario_id`,`categoria_id`,`mes`,`ano`),
  KEY `categoria_id` (`categoria_id`),
  KEY `idx_orcamento_usuario_mes` (`usuario_id`,`mes`,`ano`),
  CONSTRAINT `orcamentos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `orcamentos_ibfk_2` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orcamentos`
--

LOCK TABLES `orcamentos` WRITE;
/*!40000 ALTER TABLE `orcamentos` DISABLE KEYS */;
/*!40000 ALTER TABLE `orcamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transacoes`
--

DROP TABLE IF EXISTS `transacoes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transacoes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `categoria_id` int NOT NULL,
  `valor` decimal(15,2) NOT NULL,
  `tipo` enum('INCOME','EXPENSE') NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `data_transacao` date NOT NULL,
  `parcelado` tinyint(1) DEFAULT NULL,
  `numero_parcela` int DEFAULT NULL,
  `total_parcela` int DEFAULT NULL,
  `grupo_parcela` varchar(36) DEFAULT NULL,
  `criado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `atualizado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_transacoes_usuario_data` (`usuario_id`,`data_transacao`),
  KEY `idx_transacoes_categoria` (`categoria_id`),
  CONSTRAINT `transacoes_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `transacoes_ibfk_2` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transacoes`
--

LOCK TABLES `transacoes` WRITE;
/*!40000 ALTER TABLE `transacoes` DISABLE KEYS */;
/*!40000 ALTER TABLE `transacoes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `funcao` enum('ADMIN','USER') NOT NULL DEFAULT 'USER',
  `renda_mensal` decimal(15,2) DEFAULT '0.00',
  `perfil_investidor` enum('CONSERVADOR','MODERADO','AGRESSIVO') DEFAULT NULL,
  `ativo` tinyint(1) NOT NULL DEFAULT '1',
  `criado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `atualizado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` (`id`, `nome`, `email`, `senha`, `funcao`, `renda_mensal`, `perfil_investidor`, `ativo`, `criado_em`, `atualizado_em`) VALUES (1,'Administrador','administrador@teste.com','123','ADMIN',0.00,NULL,1,'2026-04-27 22:38:06','2026-04-27 22:38:06'),(2,'Usuario Teste','usuario@teste.com','123','USER',0.00,NULL,1,'2026-05-21 20:58:43','2026-05-21 20:58:43');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vw_resumo_mensal`
--

DROP TABLE IF EXISTS `vw_resumo_mensal`;
/*!50001 DROP VIEW IF EXISTS `vw_resumo_mensal`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_resumo_mensal` AS SELECT 
 1 AS `usuario_id`,
 1 AS `ano`,
 1 AS `mes`,
 1 AS `total_receitas`,
 1 AS `total_despesas`,
 1 AS `saldo`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `vw_resumo_mensal`
--

/*!50001 DROP VIEW IF EXISTS `vw_resumo_mensal`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_resumo_mensal` AS select `t`.`usuario_id` AS `usuario_id`,year(`t`.`data_transacao`) AS `ano`,month(`t`.`data_transacao`) AS `mes`,sum((case when (`t`.`tipo` = 'INCOME') then `t`.`valor` else 0 end)) AS `total_receitas`,sum((case when (`t`.`tipo` = 'EXPENSE') then `t`.`valor` else 0 end)) AS `total_despesas`,sum((case when (`t`.`tipo` = 'INCOME') then `t`.`valor` when (`t`.`tipo` = 'EXPENSE') then -(`t`.`valor`) else 0 end)) AS `saldo` from `transacoes` `t` group by `t`.`usuario_id`,year(`t`.`data_transacao`),month(`t`.`data_transacao`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-21 21:25:13
