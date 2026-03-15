-- 记账模块可选建表/改表（若使用 JPA ddl-auto=update 可自动生成，此处仅作参考）

-- 1. bill_category 若需按创建时间升序，需有 create_time 列（BIGINT 毫秒时间戳）
-- ALTER TABLE bill_category ADD COLUMN create_time BIGINT NULL;

-- 2. 预算表 bill_budget（若未自动创建）
-- CREATE TABLE bill_budget (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     user_id INT NOT NULL,
--     year_month VARCHAR(7) NOT NULL,
--     budget_amount DECIMAL(18,2) NOT NULL,
--     UNIQUE KEY uk_user_year_month (user_id, year_month)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
