-- MySQL dump 8.23
--
-- Host: localhost    Database: gaspdb
---------------------------------------------------------
-- Server version	3.23.58-nt

--
-- Table structure for table `actors`
--

DROP DATABASE gaspdb;

CREATE DATABASE gaspdb;

USE gaspdb;

CREATE TABLE actors (
  actorID int(11) default NULL,
  userID int(11) default NULL,
  appID int(11) default NULL,
  username char(40) default NULL,
  password char(41) default NULL,
  lastPseudo char(40) default NULL,
  rating int(4) default 1200
) TYPE=MyISAM;

--
-- Dumping data for table `actors`
--


INSERT INTO actors VALUES (1,1,1,'test2','3caf86c45217ba57','test2', 1200);
INSERT INTO actors VALUES (2,1,2,'test2','3caf86c45217ba57','test2', 1200);
INSERT INTO actors VALUES (3,1,3,'test2','3caf86c45217ba57','test2', 1200);

--
-- Table structure for table `applications`
--

CREATE TABLE applications (
  appID int(11) NOT NULL default '0'
) TYPE=MyISAM;

--
-- Dumping data for table `applications`
--


INSERT INTO applications VALUES (1);

--
-- Table structure for table `rights`
--

CREATE TABLE rights (
  userID int(11) default NULL,
  appID int(11) default NULL
) TYPE=MyISAM;

--
-- Dumping data for table `rights`
--


INSERT INTO rights VALUES (1,1);
INSERT INTO rights VALUES (1,2);
INSERT INTO rights VALUES (1,3);

--
-- Table structure for table `users`
--

CREATE TABLE users (
  userID int(11) NOT NULL default '0',
  PRIMARY KEY  (userID)
) TYPE=MyISAM;

--
-- Dumping data for table `users`
--


INSERT INTO users VALUES (1);

