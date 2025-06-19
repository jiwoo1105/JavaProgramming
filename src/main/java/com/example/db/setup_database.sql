-- 기존 데이터베이스 삭제 후 새로 생성
DROP DATABASE IF EXISTS recipe_db;
CREATE DATABASE recipe_db;
USE recipe_db;

-- 재료 테이블 생성
CREATE TABLE IF NOT EXISTS ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    available_quantity INT NOT NULL DEFAULT 0,
    UNIQUE (name)
);

-- 레시피 테이블 생성
CREATE TABLE IF NOT EXISTS recipes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    instructions TEXT NOT NULL,
    last_cooked_at TIMESTAMP NULL,
    UNIQUE (name)
);

-- 레시피-재료 관계 테이블 생성
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    recipe_id INT,
    ingredient_name VARCHAR(100) NOT NULL,
    required_quantity INT NOT NULL,
    PRIMARY KEY (recipe_id, ingredient_name),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- 즐겨찾기 테이블 생성
CREATE TABLE IF NOT EXISTS favorite_recipes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    recipe_id INT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    note TEXT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
); 