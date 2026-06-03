-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema watchapi
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `watchapi` ;

-- -----------------------------------------------------
-- Schema watchapi
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `watchapi` DEFAULT CHARACTER SET utf8 ;
SHOW WARNINGS;
USE `watchapi` ;

-- -----------------------------------------------------
-- Table `watchapi`.`roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `watchapi`.`roles` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `watchapi`.`roles` (
  `rol_id` INT NOT NULL AUTO_INCREMENT,
  `rol_name` VARCHAR(5) NOT NULL,
  PRIMARY KEY (`rol_id`))
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE UNIQUE INDEX `rol_name_UNIQUE` ON `watchapi`.`roles` (`rol_name` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `watchapi`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `watchapi`.`users` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `watchapi`.`users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL,
  `email` VARCHAR(90) NOT NULL,
  `password` CHAR(60) NOT NULL,
  `description` LONGTEXT NULL,
  `create_date` DATE NOT NULL,
  `roles_rol_id` INT NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_users_roles`
    FOREIGN KEY (`roles_rol_id`)
    REFERENCES `watchapi`.`roles` (`rol_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE UNIQUE INDEX `username_UNIQUE` ON `watchapi`.`users` (`username` ASC) VISIBLE;

SHOW WARNINGS;
CREATE UNIQUE INDEX `email_UNIQUE` ON `watchapi`.`users` (`email` ASC) VISIBLE;

SHOW WARNINGS;
CREATE INDEX `fk_users_roles_idx` ON `watchapi`.`users` (`roles_rol_id` ASC) VISIBLE;

-- -----------------------------------------------------
-- Table `watchapi`.`titles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `watchapi`.`titles` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `watchapi`.`titles` (
  `title_id` INT NOT NULL AUTO_INCREMENT,
  `watchmode_id` INT NOT NULL,
  `title_name` VARCHAR(255) NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `year` INT NOT NULL,
  `genre` VARCHAR(100) NULL,
  PRIMARY KEY (`title_id`))
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE UNIQUE INDEX `watchmode_id_UNIQUE` ON `watchapi`.`titles` (`watchmode_id` ASC) VISIBLE;

-- -----------------------------------------------------
-- Table `watchapi`.`reviews`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `watchapi`.`reviews` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `watchapi`.`reviews` (
  `review_id` INT NOT NULL AUTO_INCREMENT,
  `content` LONGTEXT NOT NULL,
  `rating` INT NOT NULL,
  `create_date` DATETIME NOT NULL,
  `edit_date` DATETIME NULL,
  `users_user_id` INT NOT NULL,
  `titles_title_id` INT NOT NULL,
  PRIMARY KEY (`review_id`),
  CONSTRAINT `fk_reviews_users`
    FOREIGN KEY (`users_user_id`)
    REFERENCES `watchapi`.`users` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_reviews_titles`
    FOREIGN KEY (`titles_title_id`)
    REFERENCES `watchapi`.`titles` (`title_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE INDEX `fk_reviews_users_idx` ON `watchapi`.`reviews` (`users_user_id` ASC) VISIBLE;

SHOW WARNINGS;
CREATE INDEX `fk_reviews_titles_idx` ON `watchapi`.`reviews` (`titles_title_id` ASC) VISIBLE;

-- -----------------------------------------------------
-- Table `watchapi`.`user_favorites`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `watchapi`.`user_favorites` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `watchapi`.`user_favorites` (
  `users_user_id` INT NOT NULL,
  `titles_title_id` INT NOT NULL,
  PRIMARY KEY (`users_user_id`, `titles_title_id`),
  CONSTRAINT `fk_user_favorites_users`
    FOREIGN KEY (`users_user_id`)
    REFERENCES `watchapi`.`users` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_favorites_titles`
    FOREIGN KEY (`titles_title_id`)
    REFERENCES `watchapi`.`titles` (`title_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE INDEX `fk_user_favorites_titles_idx` ON `watchapi`.`user_favorites` (`titles_title_id` ASC) VISIBLE;

SHOW WARNINGS;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
