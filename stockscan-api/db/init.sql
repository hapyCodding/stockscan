-- NAS MariaDB에 stockscan 데이터베이스와 전용 계정을 만든다.
-- phpMyAdmin(root) 또는 SSH의 mysql 클라이언트에서 root로 실행한다.
-- 아래 'CHANGE_ME'를 실제 비밀번호로 바꿀 것.

CREATE DATABASE IF NOT EXISTS stockscan
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'stockscan'@'%' IDENTIFIED BY 'CHANGE_ME';
GRANT ALL PRIVILEGES ON stockscan.* TO 'stockscan'@'%';
FLUSH PRIVILEGES;
