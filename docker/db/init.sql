/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.6.21-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: tfg_db
-- ------------------------------------------------------
-- Server version	10.6.21-MariaDB-0ubuntu0.22.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `campania_entidad`
--

DROP TABLE IF EXISTS `campania_entidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `campania_entidad` (
  `campania_id` bigint(20) NOT NULL,
  `entidad_id` bigint(20) NOT NULL,
  KEY `FKf953ru1v9kp2s603ali3tx5e0` (`entidad_id`),
  KEY `FKgtbx0grutrh9tr871sxedpo07` (`campania_id`),
  CONSTRAINT `FKf953ru1v9kp2s603ali3tx5e0` FOREIGN KEY (`entidad_id`) REFERENCES `entidades_receptoras` (`id`),
  CONSTRAINT `FKgtbx0grutrh9tr871sxedpo07` FOREIGN KEY (`campania_id`) REFERENCES `campanias` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campania_entidad`
--

LOCK TABLES `campania_entidad` WRITE;
/*!40000 ALTER TABLE `campania_entidad` DISABLE KEYS */;
INSERT INTO `campania_entidad` VALUES (2,1),(2,2),(1,2),(1,4),(6,2),(6,4);
/*!40000 ALTER TABLE `campania_entidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campanias`
--

DROP TABLE IF EXISTS `campanias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `campanias` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `meta_recaudacion` double DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `entidad_receptora_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo8quhuqxln8n191qvyy9v6qag` (`entidad_receptora_id`),
  CONSTRAINT `FKo8quhuqxln8n191qvyy9v6qag` FOREIGN KEY (`entidad_receptora_id`) REFERENCES `entidades_receptoras` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campanias`
--

LOCK TABLES `campanias` WRITE;
/*!40000 ALTER TABLE `campanias` DISABLE KEYS */;
INSERT INTO `campanias` VALUES (1,'Recaudación para afectados',NULL,'Campaña de ayuda',NULL),(2,'Campaña para recaudar fondos para víctimas de desastres naturales.',5000,'Ayuda a los damnificados',1),(4,'Campaña de prueba',9199.99,'Prueba word',2),(5,'Campaña de emergencia para enviar suministros médicos y alimentos a familias afectadas por la guerra.',50000,'Ayuda para Ucrania',1),(6,'Campaña de emergencia para enviar suministros médicos y alimentos a familias afectadas por la guerra.',50000,'Ayuda para Palestina',1),(7,'Campaña de emergencia para enviar suministros médicos y alimentos a familias afectadas por el covid.',50000,'Ayuda para las familias afectadas por covid',2),(8,'Se busca cualquier tipo de ayuda para las victimas de la guerra de Namek',NULL,'Campaña para las víctimas de guerra',NULL),(9,'Porfavor ayudenle',4444,'Ayuda a Paco Porras',2),(10,'Ayudenle, se ha roto dos costillas',1234,'Ayuda a Paco Porras',2),(11,'Ayudenle, se ha roto dos costillas',1234,'Ayuda a Paco Porras',2),(12,'Paco Porras necesita tu ayuda.',12345,'Ayuda a Paco Porras',2),(13,'Campaña de prueba',123,'Campaña de prueba',2),(14,'Mide medio metro y necesita crecer',12345,'Ayuda para Ana',4),(15,'ultima',1234,'Campaña ultima',4),(18,'Rauw quiere ayudar a la gente',123456,'Campaña de Rauw',4);
/*!40000 ALTER TABLE `campanias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distribucion_fondos`
--

DROP TABLE IF EXISTS `distribucion_fondos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `distribucion_fondos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `destinatario` varchar(255) DEFAULT NULL,
  `fecha` date DEFAULT NULL,
  `monto` double DEFAULT NULL,
  `entidad_receptora_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjs60omr4m3qk6sc7vau6d366t` (`entidad_receptora_id`),
  CONSTRAINT `FKjs60omr4m3qk6sc7vau6d366t` FOREIGN KEY (`entidad_receptora_id`) REFERENCES `entidades_receptoras` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribucion_fondos`
--

LOCK TABLES `distribucion_fondos` WRITE;
/*!40000 ALTER TABLE `distribucion_fondos` DISABLE KEYS */;
INSERT INTO `distribucion_fondos` VALUES (1,'Familias afectadas',NULL,100,NULL),(2,'Familias afectadas','2025-05-01',100,2);
/*!40000 ALTER TABLE `distribucion_fondos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `donaciones`
--

DROP TABLE IF EXISTS `donaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `donaciones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha` date DEFAULT NULL,
  `monto` double DEFAULT NULL,
  `entidad_receptora_id` bigint(20) DEFAULT NULL,
  `usuario_id` bigint(20) DEFAULT NULL,
  `campania_id` bigint(20) DEFAULT NULL,
  `datos_pago` text DEFAULT NULL,
  `metodo_pago` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeqlqnxvr8noqvhfkf2cs84pdn` (`entidad_receptora_id`),
  KEY `FKjpnhxmv4mgfkq83md41i1kf88` (`usuario_id`),
  KEY `FKqvvbqnf5m23394q31uxvimvcj` (`campania_id`),
  CONSTRAINT `FKeqlqnxvr8noqvhfkf2cs84pdn` FOREIGN KEY (`entidad_receptora_id`) REFERENCES `entidades_receptoras` (`id`),
  CONSTRAINT `FKjpnhxmv4mgfkq83md41i1kf88` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FKqvvbqnf5m23394q31uxvimvcj` FOREIGN KEY (`campania_id`) REFERENCES `campanias` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donaciones`
--

LOCK TABLES `donaciones` WRITE;
/*!40000 ALTER TABLE `donaciones` DISABLE KEYS */;
INSERT INTO `donaciones` VALUES (1,NULL,50,NULL,NULL,NULL,NULL,NULL),(2,NULL,50,NULL,NULL,NULL,NULL,NULL),(3,NULL,100,NULL,NULL,NULL,NULL,NULL),(4,NULL,100,NULL,NULL,NULL,NULL,NULL),(5,NULL,100,NULL,NULL,NULL,NULL,NULL),(6,'2025-03-18',100,NULL,NULL,NULL,NULL,NULL),(20,'2025-04-03',533,2,21,NULL,NULL,NULL),(21,'2024-04-08',100,2,21,2,NULL,NULL),(22,'2025-04-08',333,2,21,2,NULL,NULL),(23,'2025-04-08',5555,2,21,2,NULL,NULL),(24,'2025-04-08',5555,2,21,2,NULL,NULL),(25,'2025-04-08',1234,1,21,2,NULL,NULL),(26,'2025-04-08',9876,2,21,2,NULL,NULL),(27,'2025-04-09',55,2,21,2,NULL,NULL),(28,'2025-04-09',33,2,21,2,NULL,NULL),(29,'2025-04-13',123,2,22,2,'\"{\\\"numero\\\":\\\"1234567890\\\",\\\"caducidad\\\":\\\"33/33\\\",\\\"cvv\\\":\\\"123\\\"}\"','tarjeta'),(30,'2025-04-13',555,4,23,6,'\"{\\\"telefono\\\":\\\"12345678\\\"}\"','bizum'),(31,'2025-04-14',11,4,26,6,'\"{\\\"numero\\\":\\\"12343234323\\\",\\\"caducidad\\\":\\\"11/11\\\",\\\"cvv\\\":\\\"111\\\"}\"','tarjeta'),(32,'2025-04-15',10,2,27,6,'\"{\\\"telefono\\\":\\\"2334344556\\\"}\"','bizum'),(33,'2025-04-19',22,4,27,6,'\"{\\\"telefono\\\":\\\"333wewe23324324\\\"}\"','bizum');
/*!40000 ALTER TABLE `donaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entidades_receptoras`
--

DROP TABLE IF EXISTS `entidades_receptoras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `entidades_receptoras` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `usuario_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7vodimwrvhg142cbtkqfpvpry` (`usuario_id`),
  CONSTRAINT `FK1i0l9ee4obmc5cvbtd2j318uj` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entidades_receptoras`
--

LOCK TABLES `entidades_receptoras` WRITE;
/*!40000 ALTER TABLE `entidades_receptoras` DISABLE KEYS */;
INSERT INTO `entidades_receptoras` VALUES (1,'Entidad de prueba','ONG',NULL),(2,'Cruz Roja','ONG',NULL),(4,'Ayuntamiento de Madrid','AYUNTAMIENTO',15),(5,'Ayuntamiento de Berlín','AYUNTAMIENTO',28),(6,'Save the children','ONG',29),(7,'Ayuntamiento de Ámsterdam','AYUNTAMIENTO',30);
/*!40000 ALTER TABLE `entidades_receptoras` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `join_request`
--

DROP TABLE IF EXISTS `join_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `join_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL,
  `requested_at` datetime NOT NULL DEFAULT current_timestamp(),
  `campania_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `join_request`
--

LOCK TABLES `join_request` WRITE;
/*!40000 ALTER TABLE `join_request` DISABLE KEYS */;
INSERT INTO `join_request` VALUES (1,'APPROVED','2025-04-20 19:48:40',0,0),(2,'APPROVED','2025-04-22 00:44:26',0,0),(3,'APPROVED','2025-04-22 00:46:44',0,0),(4,'APPROVED','2025-04-23 16:20:49',18,15),(5,'APPROVED','2025-04-23 16:22:01',18,15);
/*!40000 ALTER TABLE `join_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `rol` enum('DONANTE','ONG','AYUNTAMIENTO','AUDITOR','ADMIN') NOT NULL DEFAULT 'DONANTE',
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (4,'test@example.com',NULL,'DONANTE','$2a$10$LP.UfK5o4n4B8LPsJn/S5erzggGqAcEq7fd2OWNIHeQLDYJLQKZeS'),(5,'nuevo@example.com',NULL,'DONANTE','$2a$10$UkiQQKYVmJSER.5GBTMxPOTUvC3bta.AdJqby3EHFIJ/GpF06eeii'),(6,'donante@example.com',NULL,'DONANTE','$2a$10$IozJcwnmWqdvFy/jlQ3Tt.9zS3um5JCxTMOsWx1xuyHipMJfifwES'),(7,'messi@example.com',NULL,'DONANTE','$2a$10$HnYXGYuoa.Nf2GD4WnFHyOLUVUGv5Li/L5aX8zCLXp1iuuH57xmI6'),(10,'fer@example.com',NULL,'DONANTE','$2a$10$Hah99F.NmK8czr/HpF9U4eVcKirknS52szriECe.awhbu.dznbBI6'),(11,'grizi@example.com',NULL,'DONANTE','$2a$10$0ZSwD0CSHwA9DeBSJMWQKuPbpv2n8u/qBSFOCqM5OmjSWyiclK9pm'),(13,'aytobergen@example.com',NULL,'AYUNTAMIENTO','$2a$10$NlSIMZCA0F9gB7oJxjqg8ufIaBSsBxrJlGSzK.WiA65jQw2VcqRwW'),(14,'aytobadajoz@example.com','Ayuntamiento de Badajoz','AYUNTAMIENTO','$2a$10$DK77bExpciTNiYpxLrrN4O60m7oLF.dTMosfG/VDp8eqRReSRe3U6'),(15,'aytomadrid@example.com','Ayuntamiento de Madrid ','AYUNTAMIENTO','$2a$10$LOYubid2vpBkdSTp0xVQMuEti8Q/K2f0V1x1QPOYD6TSGdmKLPb3u'),(17,'rafagt2003@gmail.com','Rafael','AUDITOR','$2a$10$g4vG7JzmzBLO5QC6gPW9H.RqgQ.glqCTRTbwSMc.LvgdaloIZUo0e'),(21,'cucu@gmail.com','Pepito','DONANTE','$2a$10$seCLU4RdIQYez9zWlRLIqemaPFf./PinlseHs2QXYkqnDZkbCGmWi'),(22,'dario@gmail.com','Dario Alvarez Barrado','DONANTE','$2a$10$iyWCbaqfF0z0N52GzEjW7eRrSf31clPHiwHVOlABVnMm02nkzvw8W'),(23,'nacho@gmail.com','Ignacio Alcalde Torrescusa','DONANTE','$2a$10$51k57nkoHIZToIjZJ9R5oO.SJiTnHmfwArbqfRyHKgazg5mBUX4v6'),(25,'julia@bergen.com','Julia','DONANTE','$2a$10$OvQeIKrO67WjRja4lusuVOdju1DdaihKYHfM3KDBUuRgFP7lcc6qu'),(26,'rafaelguiberteautinoco@gmail.com','Rafael Guiberteau Tinoco','DONANTE',''),(27,'raguibert@alumnos.unex.es','Rafael Guiberteau Tinoco','DONANTE',''),(28,'berlin@ayto.com','Ayuntamiento de Berlín','AYUNTAMIENTO','$2a$10$KQVZ8hp0bNFKhnGs8xS0u.QtJ0DY2J0xgHUlGp1eUlcPxt6uyL.AC'),(29,'save@thechildren.com','Save the children','ONG','$2a$10$6Q/iG9xjTsYbKiPu/lTnIOWDBE9/LH/y/a54Il.HjyZuOh77nmVHW'),(30,'ams@ayto.com','Ayuntamiento de Ámsterdam','AYUNTAMIENTO','$2a$10$B8UhQkQ8UJJ8bELfMpNHY.k78vl1pAVMgIr.V7CnO1lvwbOSM5.Uu'),(31,'mlama@gmail.com','Manolo Lama','ADMIN','$2a$10$NTqzyQgmSboIBFlXmY2pROQt8WB1KHt3ehJ/CgPX78fUSuWV7KhmS');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-01 17:58:45
