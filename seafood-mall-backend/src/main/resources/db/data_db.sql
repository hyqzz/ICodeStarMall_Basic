-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: seafood_mall_db
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `admin_user`
--

--
-- Current Database: `seafood_mall_db`
--


/*!40000 DROP DATABASE IF EXISTS `seafood_mall_db`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `seafood_mall_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `seafood_mall_db`;



DROP TABLE IF EXISTS `admin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '用户角色：0-普通管理员，1-超级管理员',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user`
--

LOCK TABLES `admin_user` WRITE;
/*!40000 ALTER TABLE `admin_user` DISABLE KEYS */;
INSERT INTO `admin_user` VALUES (1,'superadmin','{bcrypt}$2a$10$isJRdwWFQ/so06dmHZh9DeBZRdqCgE03wmlKz4o/p638UXBwF8QRy','超级管理员','13333333333','hhhh@sina.com',0,1,'2025-06-17 19:54:57','2025-06-17 19:54:57',0),(2,'superadmin1','{bcrypt}$2a$10$n.PFBqvebPhdOJSvT/u.1Ow1zkLleJBVVvVaKmbRN2tLEeAoAOUfG','超级管理员','13333333333','hhhh@sina.com',0,1,'2025-06-17 19:58:24','2025-06-17 19:58:24',0),(3,'superadmin2','{bcrypt}$2a$10$cOiXFnJfgDd3fl02cHZMKuugHVDTxPE2HKTI4eK2x4ssTr0yj0nsS','superadmin2','13333333333','cccc@sina.com',0,1,'2025-06-17 20:04:48','2025-06-17 20:04:48',0),(4,'codestar','{bcrypt}$2a$10$NugXHLmhl8W/xvxOZuiT7eKcJTPvQEjfOmMjDd1a9PcbZNlYaWMyq','icodestar','13200000000','codestar@sina.com',0,1,'2025-06-19 10:57:24','2025-06-19 10:57:24',0);
/*!40000 ALTER TABLE `admin_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `image` varchar(255) NOT NULL COMMENT '图片地址',
  `link` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (1,'1','/uploads/3a0bf3f7-f696-4661-b5e0-984173cb14c3.png','',0,1,'2025-06-18 12:12:55','2025-06-19 00:58:39',0),(2,'2','/uploads/e5241f13-9dee-4ff2-a6d4-6894db8a6ddb.png','',0,1,'2025-06-18 12:13:31','2025-07-11 07:08:45',1),(3,'3','/uploads/a0961ddf-8ff4-4399-a41c-693d9eeb57f2.png','',0,1,'2025-06-18 12:20:01','2025-07-11 07:08:43',1);
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父分类ID',
  `level` tinyint NOT NULL DEFAULT '1' COMMENT '层级',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'生蚝',0,1,0,1,'2025-06-18 01:04:08','2025-06-18 01:04:08',0);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `address_id` bigint NOT NULL COMMENT '收货地址ID',
  `payment_type` tinyint NOT NULL COMMENT '支付方式：1-在线支付，2-储值卡支付，3-月结',
  `payment_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态：0-未支付，1-已支付',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消',
  `remark` varchar(255) DEFAULT NULL COMMENT '订单备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_address_id` (`address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,'SN1750613881631',5,4.90,8,3,0,3,NULL,'2025-06-23 01:38:02','2025-06-23 01:38:02',0),(2,'SN1750613889949',5,239.90,8,3,0,1,NULL,'2025-06-23 01:38:10','2025-06-23 01:38:10',0),(3,'SN1750613904440',5,6.90,8,3,0,1,NULL,'2025-06-23 01:38:24','2025-06-23 01:38:24',0),(4,'SN1750613924895',5,235.00,8,3,0,2,NULL,'2025-06-23 01:38:45','2025-06-23 01:38:45',0),(5,'SN1750614243825',5,2.00,8,3,0,2,NULL,'2025-06-23 01:44:04','2025-06-23 01:44:04',0),(6,'SN1750614250680',5,2.00,8,3,0,3,NULL,'2025-06-23 01:44:11','2025-06-23 01:44:11',0),(7,'SN1750614544146',5,241.90,8,3,0,1,NULL,'2025-06-23 01:49:04','2025-06-23 01:49:04',0),(8,'SN1750614571008',5,241.90,8,3,0,2,NULL,'2025-06-23 01:49:31','2025-06-23 01:49:31',0),(9,'SN1750614779973',5,247.70,8,3,0,1,NULL,'2025-06-23 01:53:00','2025-06-23 01:53:00',0),(10,'SN1750619011308',2,2.00,4,3,0,3,NULL,'2025-06-23 03:03:31','2025-06-23 03:03:31',0),(11,'SN1750619022329',2,239.90,4,3,0,1,NULL,'2025-06-23 03:03:42','2025-06-23 03:03:42',0),(12,'SN1750619032805',2,235.00,4,3,0,2,NULL,'2025-06-23 03:03:53','2025-06-23 03:03:53',0),(13,'SN1750619044051',2,237.90,4,3,0,1,NULL,'2025-06-23 03:04:04','2025-06-23 03:04:04',0),(14,'SN1750619067005',2,472.90,4,3,0,2,NULL,'2025-06-23 03:04:27','2025-06-23 03:04:27',0),(15,'SN1750619164834',2,4.90,4,3,0,1,NULL,'2025-06-23 03:06:05','2025-06-23 03:06:05',0);
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `unit` varchar(32) DEFAULT NULL COMMENT '单位',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,1,4,'鲜生蚝（中号带子）',4.90,1,4.90,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 01:38:02','2025-06-23 01:38:02',0),(2,2,4,'鲜生蚝（中号带子）',4.90,1,4.90,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 01:38:10','2025-06-23 01:38:10',0),(3,2,3,'生蚝(全壳)',2.00,1,2.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 01:38:10','2025-06-23 01:38:10',0),(4,2,2,'生蚝',233.00,1,233.00,'/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png','50斤 / 箱','2025-06-23 01:38:10','2025-06-23 01:38:10',0),(5,3,4,'鲜生蚝（中号带子）',4.90,1,4.90,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 01:38:24','2025-06-23 01:38:24',0),(6,3,3,'生蚝(全壳)',2.00,1,2.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 01:38:24','2025-06-23 01:38:24',0),(7,4,3,'生蚝(全壳)',2.00,1,2.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 01:38:45','2025-06-23 01:38:45',0),(8,4,2,'生蚝',233.00,1,233.00,'/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png','50斤 / 箱','2025-06-23 01:38:45','2025-06-23 01:38:45',0),(9,5,3,'生蚝(全壳)',2.00,1,2.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 01:44:04','2025-06-23 01:44:04',0),(10,6,3,'生蚝(全壳)',2.00,1,2.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 01:44:11','2025-06-23 01:44:11',0),(11,7,4,'鲜生蚝（中号带子）',4.90,1,4.90,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 01:49:04','2025-06-23 01:49:04',0),(12,7,3,'生蚝(全壳)',2.00,2,4.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 01:49:04','2025-06-23 01:49:04',0),(13,7,2,'生蚝',233.00,1,233.00,'/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png','50斤 / 箱','2025-06-23 01:49:04','2025-06-23 01:49:04',0),(14,8,4,'鲜生蚝（中号带子）',4.90,1,4.90,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 01:49:31','2025-06-23 01:49:31',0),(15,8,3,'生蚝(全壳)',2.00,2,4.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 01:49:31','2025-06-23 01:49:31',0),(16,8,2,'生蚝',233.00,1,233.00,'/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png','50斤 / 箱','2025-06-23 01:49:31','2025-06-23 01:49:31',0),(17,9,4,'鲜生蚝（中号带子）',4.90,3,14.70,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 01:53:00','2025-06-23 01:53:00',0),(18,9,2,'生蚝',233.00,1,233.00,'/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png','50斤 / 箱','2025-06-23 01:53:00','2025-06-23 01:53:00',0),(19,10,3,'生蚝(全壳)',2.00,1,2.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 03:03:31','2025-06-23 03:03:31',0),(20,11,4,'鲜生蚝（中号带子）',4.90,1,4.90,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 03:03:42','2025-06-23 03:03:42',0),(21,11,3,'生蚝(全壳)',2.00,1,2.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 03:03:42','2025-06-23 03:03:42',0),(22,11,2,'生蚝',233.00,1,233.00,'/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png','50斤 / 箱','2025-06-23 03:03:42','2025-06-23 03:03:42',0),(23,12,3,'生蚝(全壳)',2.00,1,2.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 03:03:53','2025-06-23 03:03:53',0),(24,12,2,'生蚝',233.00,1,233.00,'/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png','50斤 / 箱','2025-06-23 03:03:53','2025-06-23 03:03:53',0),(25,13,2,'生蚝',233.00,1,233.00,'/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png','50斤 / 箱','2025-06-23 03:04:04','2025-06-23 03:04:04',0),(26,13,4,'鲜生蚝（中号带子）',4.90,1,4.90,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 03:04:04','2025-06-23 03:04:04',0),(27,14,4,'鲜生蚝（中号带子）',4.90,1,4.90,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 03:04:27','2025-06-23 03:04:27',0),(28,14,3,'生蚝(全壳)',2.00,1,2.00,'/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png','个','2025-06-23 03:04:27','2025-06-23 03:04:27',0),(29,14,2,'生蚝',233.00,2,466.00,'/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png','50斤 / 箱','2025-06-23 03:04:27','2025-06-23 03:04:27',0),(30,15,4,'鲜生蚝（中号带子）',4.90,1,4.90,'/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png','斤','2025-06-23 03:06:05','2025-06-23 03:06:05',0);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_record`
--

DROP TABLE IF EXISTS `payment_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `payment_type` tinyint NOT NULL COMMENT '支付方式：1-在线支付，2-储值卡支付，3-月结',
  `payment_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态：0-未支付，1-已支付',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '交易流水号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_record`
--

LOCK TABLES `payment_record` WRITE;
/*!40000 ALTER TABLE `payment_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `description` text COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `unit` varchar(20) NOT NULL COMMENT '单位',
  `image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `images` json DEFAULT NULL COMMENT '商品多图（JSON数组）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,1,'生蚝','<p>大大的生蚝，香香的生蚝，欢迎购买</p><p><img src=\"/uploads/cfb4dead-4246-4a08-8d7c-4a90d72095bf.png\" alt=\"\" data-href=\"/uploads/cfb4dead-4246-4a08-8d7c-4a90d72095bf.png\" style=\"width: 173.00px;height: 173.00px;\"/></p>',255.00,100,'箱','/uploads/8834df37-4035-44b6-8f11-9a6b52aabd72.png','[\"http://localhost:8080/uploads/8834df37-4035-44b6-8f11-9a6b52aabd72.png\"]',0,'2025-06-18 01:46:14','2025-06-19 00:58:49',0),(2,1,'生蚝','<p>大大的生蚝，香香的生蚝</p><p><img src=\"/uploads/b883f9cb-b59c-4e5a-9dcf-c28226c344f7.png\" alt=\"\" data-href=\"/uploads/b883f9cb-b59c-4e5a-9dcf-c28226c344f7.png\" style=\"\"/></p>',233.00,999,'50斤 / 箱','/uploads/95822b6f-77f0-4a37-9fae-37efeffd2c1f.png',NULL,1,'2025-06-18 16:51:07','2025-06-19 10:46:59',0),(3,1,'生蚝(全壳)','<p><span style=\"color: rgb(66, 144, 247); font-size: 19px;\"><strong>按个卖的，大大的生蚝，新鲜的生蚝，好吃的生蚝</strong></span></p><p><img src=\"/uploads/5e634859-18fe-4a90-95a9-60c4d19e557b.png\" alt=\"\" data-href=\"/uploads/5e634859-18fe-4a90-95a9-60c4d19e557b.png\" style=\"\"/></p>',2.00,10000,'个','/uploads/879ec2f7-2f58-4ffb-88f9-4dee124f01ec.png',NULL,1,'2025-06-18 21:18:03','2025-06-19 10:46:59',0),(4,1,'鲜生蚝（中号带子）','<p><span style=\"color: rgb(56, 158, 13); font-size: 22px;\">新鲜的生蚝，大大的生蚝，中号带子</span></p><p><img src=\"/uploads/a8bcb03a-0690-41d4-a547-9903cd2f73e3.png\" alt=\"\" data-href=\"/uploads/a8bcb03a-0690-41d4-a547-9903cd2f73e3.png\" style=\"\"/></p><p><br></p>',4.90,1000,'斤','/uploads/fec6c96c-a410-479b-8ae8-8dfdfc4e2cf1.png',NULL,1,'2025-06-19 11:58:14','2025-06-19 11:58:14',0);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stored_value_card`
--

DROP TABLE IF EXISTS `stored_value_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stored_value_card` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `card_no` varchar(50) NOT NULL COMMENT '卡号',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_card_no` (`card_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='储值卡表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stored_value_card`
--

LOCK TABLES `stored_value_card` WRITE;
/*!40000 ALTER TABLE `stored_value_card` DISABLE KEYS */;
/*!40000 ALTER TABLE `stored_value_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `user_type` tinyint NOT NULL DEFAULT '0' COMMENT '用户类型：0-普通用户，1-月结用户，2-储值卡用户',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `level` int DEFAULT '0' COMMENT '用户等级：0-普通用户，1-银牌用户，2-金牌用户',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'superadmin','{bcrypt}$2a$10$T6nAHtFUUGACBi74wEGNb.Z7bWaPALongVx7v5SNccV01DBt1G.tu','超级管理员','13800000000','superadmin@example.com',0,1,'2025-06-16 19:10:25','2025-06-16 20:20:58',0,0),(2,'111111','{bcrypt}$2a$10$cITBgaP8/dd0vlGHBRY0zeVrJpX77JsgAKr88klQZmIxyklJMZ.gG','111111','13311111111','111111@11.11',0,1,'2025-06-16 20:46:32','2025-06-16 20:46:32',0,0),(3,'222222','{bcrypt}$2a$10$6gWvpwk6a4FPNaCYh/dUbOYaij1yLU14vHII3DyMAWc.L5WXN2wRW','222222','13322222222','222222@22.22',0,1,'2025-06-17 18:42:36','2025-06-17 18:42:36',0,0),(4,'333333','{bcrypt}$2a$10$8J5TcDCCowO23KJqN9pJZ.cBvS8UWsAsbR3TnhA.t/.fgbm7KAvkS','张三','13033333333','sina3@sina.com',0,1,'2025-06-18 23:12:13','2025-06-18 23:12:13',0,0),(5,'icodestar','{bcrypt}$2a$10$BCGZ1.Gxpx3mbBi8GAVmEenGX7VsvpfqVUo3msiGeOv/R2jm7w3XW','codestar','13000000000','sina0@sina.com',0,1,'2025-06-19 02:41:39','2025-06-19 02:41:39',0,0),(6,'我','{bcrypt}$2a$10$5gbRnnwuUtBJz0G/ozmCV.EUO0ffq1HvCZi13JSBSSC6o0djmhhiC','王玉','15667622598','496971180@qq.com',0,1,'2025-06-20 00:13:50','2025-06-20 00:13:50',0,0),(7,'mycode','{bcrypt}$2a$10$QTkRSTHMo47n5f0lfpRIxO6mfrHBR6Tci9TlSvjGAkzQ9RthINhOS','mycode','13000000000','mycode@223.com',0,1,'2025-06-23 01:06:14','2025-06-23 01:06:14',0,0),(8,'user','{bcrypt}$2a$10$W1hEL/9/EVwGV9LOyrB9bOiK2.JoB2RabFNp5EqZnBtxroCwbgoHW','user','13222222222','user@icodestar.net',0,1,'2025-07-11 10:51:15','2025-07-11 10:51:15',0,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_address`
--

DROP TABLE IF EXISTS `user_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `consignee_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `consignee_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区/县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址：0-否，1-是',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

LOCK TABLES `user_address` WRITE;
/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (1,111111,'11111111','13333333333','山东省','潍坊市','潍城区','111111111111111111',1,'2025-06-18 16:01:26','2025-06-18 16:01:26',0),(2,111111,'2222222','13222222222','天津市','天津市','河东区','222222222222',0,'2025-06-18 16:14:21','2025-06-18 16:14:21',0),(3,222222,'贺先生','13333333333','北京市','北京市','东城区','无言路285号',1,'2025-06-18 21:54:45','2025-06-18 21:54:45',0),(4,2,'张意义','13311111111','河北省','唐山市','路南区','公平路322号',1,'2025-06-18 22:14:22','2025-06-18 22:14:22',0),(5,2,'刘意义','13211111111','上海市','上海市','静安区','三成路222号',0,'2025-06-18 22:16:15','2025-06-18 22:16:15',0),(6,3,'张尔耳','13022222222','山西省','大同市','新荣区','大庆路365号',1,'2025-06-18 22:17:42','2025-06-18 22:17:42',0),(7,4,'张三','13033333333','浙江省','杭州市','西湖区','延安路333号',1,'2025-06-18 23:14:11','2025-06-18 23:14:11',0),(8,5,'icodestar','13200000000','上海市','上海市','静安区','大庆路138号',1,'2025-06-19 10:50:31','2025-06-19 10:50:31',0),(9,5,'star','13211111111','河北省','石家庄市','长安区','长安南路138号',0,'2025-06-19 10:54:58','2025-06-19 10:54:58',0),(10,6,'王玉','15667622598','陕西省','延安市','宝塔区','燕沟',0,'2025-06-20 00:16:33','2025-06-20 00:16:33',0),(11,7,'mycode','13000000000','天津市','天津市','河东区','天天路122号',0,'2025-06-23 01:07:37','2025-06-23 01:07:37',0),(12,7,'mycode1','13022222222','山西省','大同市','云冈区','大庆路130号',1,'2025-06-23 01:08:43','2025-06-23 01:08:50',0);
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-12  2:58:22
