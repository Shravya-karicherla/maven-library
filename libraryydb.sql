-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: library
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `book_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `author` varchar(100) DEFAULT NULL,
  `available_quantity` int DEFAULT NULL,
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'Amma dairy lo konni pegeelu','Ravi Mantri',10),(2,'Half GirlFriend','kamalhasan',10),(3,'Ruskinbond','ruby',10),(5,'circle of life','sudha murthi',10),(19,'xyz','abc',5),(20,'xyzx','abcc',3),(21,'xyzxs','abccs',3),(22,'oooo','ppppp',3),(23,'world','class',3),(24,'eagle','sgs',3),(25,'rabbit','r',3),(26,'bird','rg',3),(27,'crime','thri',3),(28,'land','mafiya',3),(29,'hitech chitram','babu',3),(30,'charminar','ma',3),(31,'naa anveshana','na',3),(32,'chandamama katulu','jabili',3),(34,'panchatantram','jabilli',2),(35,'doraman','nobita',3);
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrowers`
--

DROP TABLE IF EXISTS `borrowers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrowers` (
  `borrower_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `book_id` int DEFAULT NULL,
  `borrow_date` date DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `fine` decimal(10,2) DEFAULT '0.00',
  `finestatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`borrower_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `borrowers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrowers`
--

LOCK TABLES `borrowers` WRITE;
/*!40000 ALTER TABLE `borrowers` DISABLE KEYS */;
INSERT INTO `borrowers` VALUES (1,2,1,'2025-03-11','2025-04-11','2025-05-11',300.00,'PAID'),(2,2,2,'2025-03-11','2025-04-11','2025-03-11',0.00,'returned in time'),(3,2,3,'2025-03-11','2025-04-11','2025-03-11',0.00,'returned in time'),(4,2,4,'2025-03-11','2025-04-11','2025-05-11',300.00,'PAID'),(5,3,2,'2025-03-11','2025-04-11','2025-05-11',300.00,'PAID'),(6,3,1,'2025-03-11','2025-04-11','2025-05-11',300.00,'PAID'),(7,3,3,'2025-03-11','2025-04-11','2025-05-11',300.00,'PAID'),(8,4,3,'2025-03-11','2025-04-11','2025-03-11',0.00,'returned in time'),(9,4,5,'2025-03-11','2025-04-11','2025-07-11',910.00,'PAID'),(10,4,6,'2025-03-11','2025-04-11','2025-07-11',910.00,'PAID'),(11,2,3,'2025-03-11','2025-04-11','2025-03-12',0.00,'returned in time'),(12,2,2,'2025-03-11','2025-04-11','2025-03-13',0.00,'returned in time'),(13,2,5,'2025-03-11','2025-04-11','2025-03-13',0.00,'returned in time'),(14,2,1,'2025-03-13','2025-04-13','2025-03-17',0.00,'returned in time'),(15,2,2,'2025-03-13','2025-04-13','2025-03-18',0.00,'returned in time'),(16,2,15,'2025-03-13','2025-04-13','2025-03-18',0.00,'returned in time'),(17,3,16,'2025-03-13','2025-04-13','2025-03-13',0.00,'returned in time'),(18,3,15,'2025-03-13','2025-04-13','2025-03-13',0.00,'returned in time'),(19,3,15,'2025-03-13','2025-04-13','2025-05-13',300.00,'PAID'),(20,9,16,'2025-03-13','2025-04-13','2025-03-20',0.00,'returned in time'),(21,9,15,'2025-03-13','2025-04-13','2025-05-13',300.00,'PAID'),(22,2,1,'2025-03-18','2025-04-18','2025-03-19',0.00,'returned in time'),(23,2,1,'2025-03-19','2025-04-19','2025-03-19',0.00,'returned in time'),(24,4,1,'2025-03-20','2025-04-20','2025-03-20',0.00,'returned in time'),(25,4,5,'2025-03-20','2025-04-20','2025-03-20',0.00,'returned in time'),(26,4,22,'2025-03-20','2025-04-20','2025-03-20',0.00,'returned in time'),(27,9,1,'2025-03-20','2025-03-20','2025-03-20',0.00,'returned in time'),(28,9,2,'2025-03-20','2025-03-20','2025-03-20',0.00,'returned in time'),(29,9,3,'2025-03-20','2025-03-20','2025-05-20',610.00,'PAID'),(30,9,1,'2025-03-20','2025-04-20','2025-05-20',300.00,'PAID'),(31,9,2,'2025-03-20','2025-04-20','2025-05-20',300.00,'PAID'),(32,9,3,'2025-03-20','2025-04-20','2025-05-20',300.00,'PAID'),(33,3,1,'2025-03-20','2025-04-20','2025-07-20',910.00,'PAID'),(34,3,2,'2025-03-20','2025-04-20','2025-07-20',910.00,'PAID'),(35,3,3,'2025-03-20','2025-04-20','2025-07-20',910.00,'PAID'),(36,5,1,'2025-03-20','2025-04-20','2025-07-20',910.00,'UNPAID'),(37,5,2,'2025-03-20','2025-04-20','2025-07-20',910.00,'UNPAID'),(38,5,3,'2025-03-20','2025-04-20','2025-07-20',910.00,'PAID'),(39,6,1,'2025-03-20','2025-04-20','2025-07-20',910.00,'PAID'),(40,6,2,'2025-03-20','2025-04-20','2025-07-20',910.00,'PAID'),(41,6,22,'2025-03-20','2025-04-20','2025-07-20',910.00,'PAID'),(42,6,27,'2025-03-20','2025-04-20','2025-07-20',910.00,'PAID'),(43,6,30,'2025-03-20','2025-04-20','2025-07-20',910.00,'PAID'),(44,6,1,'2025-03-20','2025-04-20','2025-07-20',910.00,'UNPAID'),(45,6,2,'2025-03-20','2025-04-20','2025-07-20',910.00,'UNPAID'),(46,6,3,'2025-03-20','2025-04-20','2025-07-20',910.00,'UNPAID'),(47,10,1,'2025-03-20','2025-04-20','2025-07-20',910.00,'UNPAID'),(48,10,2,'2025-03-20','2025-04-20','2025-07-20',910.00,'UNPAID'),(49,10,3,'2025-03-20','2025-04-20','2025-07-20',910.00,'UNPAID'),(50,2,3,'2025-03-21','2025-04-21','2025-03-25',0.00,'returned in time'),(51,2,1,'2025-03-25','2025-04-25','2025-03-25',0.00,'returned in time'),(52,2,2,'2025-03-31','2025-04-30','2025-03-31',0.00,'returned in time');
/*!40000 ALTER TABLE `borrowers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `borrower_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `fine` decimal(10,2) DEFAULT NULL,
  `finestatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,5,3,300.00,'PAID'),(2,6,3,300.00,'PAID'),(3,1,2,300.00,'PAID'),(4,4,2,300.00,'PAID'),(5,7,3,300.00,'PAID'),(6,9,4,910.00,'PAID'),(7,10,4,910.00,'PAID'),(8,21,9,300.00,'PAID'),(9,29,9,610.00,'PAID'),(10,30,9,300.00,'PAID'),(11,31,9,300.00,'PAID'),(12,32,9,300.00,'PAID'),(13,19,3,300.00,'PAID'),(14,33,3,910.00,'PAID'),(15,34,3,910.00,'PAID'),(16,35,3,910.00,'PAID'),(17,39,6,910.00,'PAID'),(18,40,6,910.00,'PAID'),(19,42,6,910.00,'PAID'),(20,43,6,910.00,'PAID'),(21,41,6,910.00,'PAID'),(22,38,5,910.00,'PAID');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `contact_info` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `mail` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('owner','customer') NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `mail` (`mail`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'9876543210','Owner','owner@gmail.com','$2a$12$bnWfOKGnfesOMDF/2JWuQe3kSiWPmY1UdeFvAQ6LGnnGWq38Dki..','owner'),(2,'9999999999','shyam','s@gmail.com','$2a$10$AwbhvJsoFd.ereqM7gkMvOFvEalpZP.04jfezpDDYYaYa3QcMfu2G','customer'),(3,'9898989898','navya','n@gmail.com','$2a$10$co11YX351GVC45LxjAIjgOR/G15wl72xvT2VzWMgKJAWB97OU7K0i','customer'),(4,'9876789876','shravya','shravya@gmail.com','$2a$10$4WsxNj/91Vvs9442Y9v6DO3YeWqVp.Dr.twj/vlxE6kpBo6tCRiEu','customer'),(5,'9876545434','Hema','h@gmail.com','$2a$10$6HiXEyeEBRkAALL9CHfAzOJvlqrpPNZK8ocgm2FpBs15kUhtzPw8.','customer'),(6,'7687678656','zara','z@gmail.com','$2a$10$QABc7kO40OTQNiabT.I4cOdHHNhEGMbDtirPq4qQR610EhpP6kDdy','customer'),(7,'8978787678','Nayan','nm@gmail.com','$2a$10$Pimqmd5ZDDVGelXxF4/.u.GbH4VoeXxEEWbQMqkKAFmTc9IALsPWK','customer'),(8,'7878765456','Ten','ten@gmail.com','$2a$10$XIa8v6DQsbLdakw0bha75u.5OduKgV99IVLfr9Qk5Y7aChB75T/pK','customer'),(9,'9898989878','dora','dora@gmail.com','$2a$10$yVlBFLNxz3i4J2WvCqUny.quUFGopRXr1u/mYdU.aKXydYrUzaRAa','customer'),(10,'9876789098','raju','r@gmail.com','$2a$10$gct0lpnHNlW6QKB3s0xa0OcCu/IlporiUfYpagkbnLIqhQN4mIt/2','customer'),(11,'9876765231','archith','a@yahoo.com','$2a$10$TUnHvPF.5LJwM/AfXLlP0u08kZ4wk53foL7C1Kas8SoM1sL/mOvyq','customer');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-31 16:16:41
