-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema dlocal_demo_db
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `dlocal_demo_db` ;

-- -----------------------------------------------------
-- Schema dlocal_demo_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dlocal_demo_db` DEFAULT CHARACTER SET utf8 ;
USE `dlocal_demo_db` ;

-- -----------------------------------------------------
-- Table `dlocal_demo_db`.`merchants`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dlocal_demo_db`.`merchants` ;

CREATE TABLE IF NOT EXISTS `dlocal_demo_db`.`merchants` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL DEFAULT 'NO NAME',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dlocal_demo_db`.`sales`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dlocal_demo_db`.`sales` ;

CREATE TABLE IF NOT EXISTS `dlocal_demo_db`.`sales` (
  `id` BINARY(16) NOT NULL COMMENT 'A sale unique identifier.',
  `merchants_id` INT(10) UNSIGNED NOT NULL,
  `currency` ENUM('AED','AFN','ALL','AMD','ANG','AOA','ARS','AUD','AWG','AZN','BAM','BBD','BDT','BGN','BHD','BIF','BMD','BND','BOB','BRL','BSD','BTC','BTN','BWP','BYN','BYR','BZD','CAD','CDF','CHF','CLF','CLP','CNY','COP','CRC','CUC','CUP','CVE','CZK','DJF','DKK','DOP','DZD','EGP','ERN','ETB','EUR','FJD','FKP','GBP','GEL','GGP','GHS','GIP','GMD','GNF','GTQ','GYD','HKD','HNL','HRK','HTG','HUF','IDR','ILS','IMP','INR','IQD','IRR','ISK','JEP','JMD','JOD','JPY','KES','KGS','KHR','KMF','KPW','KRW','KWD','KYD','KZT','LAK','LBP','LKR','LRD','LSL','LTL','LVL','LYD','MAD','MDL','MGA','MKD','MMK','MNT','MOP','MRO','MUR','MVR','MWK','MXN','MYR','MZN','NAD','NGN','NIO','NOK','NPR','NZD','OMR','PAB','PEN','PGK','PHP','PKR','PLN','PYG','QAR','RON','RSD','RUB','RWF','SAR','SBD','SCR','SDG','SEK','SGD','SHP','SLL','SOS','SRD','STD','SVC','SYP','SZL','THB','TJS','TMT','TND','TOP','TRY','TTD','TWD','TZS','UAH','UGX','USD','UYU','UZS','VEF','VND','VUV','WST','XAF','XAG','XAU','XCD','XDR','XOF','XPF','YER','ZAR','ZMK','ZMW','ZWL') NOT NULL DEFAULT 'USD' COMMENT 'ORIGINAL CURRENCY CODE\n\nDefined in 3 capital letter based on the fixer service.\n',
  `amount_org` DOUBLE(10,2) NOT NULL DEFAULT 0,
  `amount_usd` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT 'CONVERTED AMOUNT\n\nvalue is coverted from orginal CURRENCY to USD based AMOUNT.\n\nconversion is made at bussiness logic because spec ask to write the value in USD.\n\nfixer.io for all conversion, currency tasks.',
  `status` ENUM('PENDING','PAID','REJECTED') NOT NULL DEFAULT 'PENDING',
  `created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `transaction_id` INT(10) NULL COMMENT 'Internal DLocal transaction id.\nThis value is provided and unique for each sale.',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_sales_merchants1`
    FOREIGN KEY (`merchants_id`)
    REFERENCES `dlocal_demo_db`.`merchants` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX `fk_sales_merchants1_idx` ON `dlocal_demo_db`.`sales` (`merchants_id` ASC);

CREATE UNIQUE INDEX `id_UNIQUE` ON `dlocal_demo_db`.`sales` (`id` ASC);

-- begin attached script 'merchants data'
insert into merchants (id, name) values (1, 'Wordware');
insert into merchants (id, name) values (2, 'Topicstorm');
insert into merchants (id, name) values (3, 'Jatri');
insert into merchants (id, name) values (4, 'Trilia');
insert into merchants (id, name) values (5, 'Avamm');
insert into merchants (id, name) values (6, 'Roodel');
insert into merchants (id, name) values (7, 'Eimbee');
insert into merchants (id, name) values (8, 'Kazio');
insert into merchants (id, name) values (9, 'Browsebug');
insert into merchants (id, name) values (10, 'Meezzy');
insert into merchants (id, name) values (11, 'Trilith');
insert into merchants (id, name) values (12, 'Skyndu');
insert into merchants (id, name) values (13, 'Tagtune');
insert into merchants (id, name) values (14, 'Tavu');
insert into merchants (id, name) values (15, 'Wikibox');
insert into merchants (id, name) values (16, 'Jaxworks');
insert into merchants (id, name) values (17, 'Oozz');
insert into merchants (id, name) values (18, 'Realblab');
insert into merchants (id, name) values (19, 'Gevee');
insert into merchants (id, name) values (20, 'Devpoint');
-- end attached script 'merchants data'

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
-- begin attached script 'sales_data_short.sql'
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),11,'ANG',407.26,407.26,'PENDING','2018-04-11 21:58:02',556);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),7,'FJD',54.37,54.37,'PENDING','2017-06-10 09:55:43',557);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),11,'TMT',156.29,156.29,'PENDING','2017-09-15 08:24:15',558);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),17,'UAH',230.64,230.64,'PENDING','2017-09-09 02:59:07',559);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),16,'XDR',290.41,290.41,'PENDING','2017-10-10 14:37:44',560);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),11,'CRC',48.99,48.99,'PENDING','2017-08-30 07:39:00',561);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),10,'XOF',55.91,55.91,'PENDING','2018-05-13 17:49:04',562);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),15,'SZL',483.75,483.75,'PENDING','2018-03-06 03:40:31',563);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),1,'SVC',311.73,311.73,'PENDING','2017-10-28 00:25:41',564);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),11,'SZL',500.47,500.47,'PENDING','2017-06-12 23:02:39',565);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),10,'CNY',21.13,21.13,'PENDING','2018-04-20 09:07:53',566);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),4,'STD',182.98,182.98,'PENDING','2017-10-10 20:22:17',567);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),7,'KHR',246.86,246.86,'PENDING','2017-12-05 12:54:39',568);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),16,'INR',470.07,470.07,'PENDING','2018-04-14 09:03:58',569);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),5,'BTN',259.47,259.47,'PENDING','2017-11-29 18:19:06',570);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),17,'BHD',488.19,488.19,'PENDING','2017-06-11 20:06:25',571);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),5,'COP',265.60,265.60,'PENDING','2018-03-02 15:33:30',572);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),10,'RSD',462.26,462.26,'PENDING','2018-01-01 18:18:00',573);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),1,'TTD',94.73,94.73,'PENDING','2017-08-07 07:48:43',574);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),15,'ERN',438.11,438.11,'PENDING','2017-12-09 20:04:32',575);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),20,'AFN',385.55,385.55,'PENDING','2017-07-16 01:16:45',576);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),16,'CRC',77.58,77.58,'PENDING','2017-07-20 01:31:10',577);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),18,'MWK',71.21,71.21,'PENDING','2017-07-03 03:40:21',578);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),12,'IQD',424.37,424.37,'PENDING','2017-09-08 05:50:23',579);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),16,'LRD',505.76,505.76,'PENDING','2017-08-30 11:54:47',580);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),1,'PYG',135.69,135.69,'PENDING','2017-11-01 21:27:48',581);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),12,'AZN',282.46,282.46,'PENDING','2018-03-19 21:03:55',582);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),16,'KES',61.14,61.14,'PENDING','2017-12-14 14:53:23',583);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),17,'XAF',55.31,55.31,'PENDING','2017-11-10 14:27:46',584);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),17,'MUR',381.90,381.90,'PENDING','2017-06-11 18:35:30',585);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),5,'CUC',436.92,436.92,'PENDING','2017-09-11 16:55:34',586);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),16,'KHR',409.51,409.51,'PENDING','2018-02-23 07:20:28',587);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),14,'TND',233.62,233.62,'PENDING','2017-11-06 23:29:09',588);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),14,'CRC',253.78,253.78,'PENDING','2018-05-15 02:14:06',589);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),3,'XAU',121.34,121.34,'PENDING','2018-03-16 09:39:03',590);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),13,'AOA',496.31,496.31,'PENDING','2017-11-05 11:41:49',591);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),19,'TJS',229.46,229.46,'PENDING','2018-01-19 12:21:16',592);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),11,'MDL',216.38,216.38,'PENDING','2018-02-25 13:25:53',593);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),2,'DJF',237.68,237.68,'PENDING','2018-04-07 11:33:44',594);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),3,'XCD',495.87,495.87,'PENDING','2018-03-14 15:22:39',595);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),15,'VND',62.17,62.17,'PENDING','2017-06-16 12:24:47',596);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),18,'CDF',279.71,279.71,'PENDING','2017-11-26 07:37:48',597);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),7,'SGD',110.45,110.45,'PENDING','2017-06-24 04:24:43',598);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),6,'BND',213.86,213.86,'PENDING','2017-08-16 06:35:38',599);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),5,'MNT',345.95,345.95,'PENDING','2017-11-18 03:10:38',600);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),18,'HRK',83.79,83.79,'PENDING','2017-09-06 14:31:19',601);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),17,'TWD',286.03,286.03,'PENDING','2017-12-07 01:54:21',602);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),19,'FJD',346.41,346.41,'PENDING','2017-11-22 19:58:12',603);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),19,'KZT',262.36,262.36,'PENDING','2018-05-12 05:19:42',604);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),19,'GEL',71.15,71.15,'PENDING','2017-08-22 23:42:02',605);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),11,'BRL',433.91,433.91,'PENDING','2018-05-08 21:06:25',606);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),19,'SBD',522.74,522.74,'PENDING','2018-06-03 22:47:38',607);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),12,'ETB',415.32,415.32,'PENDING','2018-02-08 07:21:28',608);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),7,'VEF',437.06,437.06,'PENDING','2017-09-19 23:11:09',609);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),9,'ZAR',202.40,202.40,'PENDING','2017-10-31 12:27:29',610);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),15,'KES',513.11,513.11,'PENDING','2018-05-15 00:00:24',611);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),8,'CNY',378.82,378.82,'PENDING','2017-09-23 19:34:09',612);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),18,'NGN',224.00,224.00,'PENDING','2017-06-23 18:56:50',613);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),8,'XAG',223.32,223.32,'PENDING','2017-08-02 15:44:29',614);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),8,'GGP',158.53,158.53,'PENDING','2017-07-26 16:05:43',615);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),12,'SRD',479.94,479.94,'PENDING','2018-05-19 23:45:22',616);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),5,'JMD',406.79,406.79,'PENDING','2018-04-22 21:59:22',617);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),18,'FJD',430.84,430.84,'PENDING','2017-06-14 19:06:05',618);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),2,'SDG',498.71,498.71,'PENDING','2017-12-31 16:31:30',619);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),6,'BRL',245.52,245.52,'PENDING','2017-11-07 09:10:53',620);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),9,'LAK',310.39,310.39,'PENDING','2017-07-18 19:46:50',621);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),19,'SEK',29.20,29.20,'PENDING','2018-01-28 22:43:05',622);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),18,'LKR',469.04,469.04,'PENDING','2018-05-12 23:53:33',623);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),17,'TOP',101.67,101.67,'PENDING','2018-01-01 18:05:06',624);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),2,'TTD',183.19,183.19,'PENDING','2018-02-21 05:26:02',625);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),7,'GEL',95.83,95.83,'PENDING','2017-11-30 23:10:09',626);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),16,'LYD',73.74,73.74,'PENDING','2018-04-09 06:29:18',627);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),20,'XPF',416.88,416.88,'PENDING','2017-10-12 06:20:06',628);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),18,'CLF',83.85,83.85,'PENDING','2018-04-30 02:01:32',629);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),18,'AWG',319.97,319.97,'PENDING','2017-11-16 21:34:55',630);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),13,'AED',185.44,185.44,'PENDING','2017-06-28 00:07:13',631);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),13,'NAD',220.36,220.36,'PENDING','2017-10-10 19:40:06',632);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),6,'KHR',368.17,368.17,'PENDING','2018-05-20 08:58:03',633);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),16,'TTD',352.67,352.67,'PENDING','2018-05-02 13:02:49',634);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),13,'AZN',41.47,41.47,'PENDING','2018-05-30 09:05:08',635);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),9,'CVE',396.29,396.29,'PENDING','2017-08-30 18:06:02',636);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),1,'VND',477.52,477.52,'PENDING','2017-11-04 14:42:25',637);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),1,'AED',197.61,197.61,'PENDING','2017-11-30 03:11:07',638);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),2,'MMK',204.46,204.46,'PENDING','2018-02-25 01:25:19',639);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),10,'PAB',342.41,342.41,'PENDING','2017-12-15 00:24:56',640);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),19,'XAG',492.33,492.33,'PENDING','2017-06-24 15:04:05',641);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),9,'LBP',325.56,325.56,'PENDING','2018-05-24 03:12:05',642);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),18,'XAG',107.92,107.92,'PENDING','2017-10-06 21:38:49',643);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),8,'KRW',23.83,23.83,'PENDING','2017-09-07 23:58:40',644);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),12,'GMD',155.69,155.69,'PENDING','2017-09-02 00:37:56',645);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),20,'BWP',118.45,118.45,'PENDING','2017-06-14 07:44:50',646);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),4,'KGS',501.66,501.66,'PENDING','2017-11-20 07:50:34',647);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),20,'AUD',387.71,387.71,'PENDING','2017-06-21 23:37:05',648);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),20,'PLN',174.77,174.77,'PENDING','2018-06-02 18:27:42',649);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),15,'KZT',364.91,364.91,'PENDING','2018-03-17 08:37:44',650);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),14,'LKR',167.59,167.59,'PENDING','2017-12-09 15:54:17',651);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),9,'BHD',365.16,365.16,'PENDING','2017-08-08 22:10:52',652);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),10,'KHR',441.08,441.08,'PENDING','2017-11-15 07:55:55',653);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),15,'UAH',269.71,269.71,'PENDING','2017-12-15 07:00:22',654);
INSERT INTO `sales` (`id`,`merchants_id`,`currency`,`amount_org`,`amount_usd`,`status`,`created`,`transaction_id`) VALUES (UUID_TO_BIN(UUID()),20,'IDR',520.16,520.16,'PENDING','2017-06-12 22:25:07',655);

-- end attached script 'sales_data_short.sql'
