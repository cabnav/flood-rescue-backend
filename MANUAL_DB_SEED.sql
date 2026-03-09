-- 1. Insert 2 Warehouse mẫu (Ví dụ lấy ID là 1 và 2)
INSERT INTO warehouses (resource_id, supply_id, status, latitude, longitude, address) 
VALUES 
('WH-001', 'SUP-Q1', 'ACTIVE', 10.776889, 106.700806, 'Kho Quận 1'),
('WH-002', 'SUP-Q3', 'ACTIVE', 10.784680, 106.686930, 'Kho Quận 3')
ON CONFLICT DO NOTHING;

-- 2. Insert 4 Team (Mỗi Warehouse 2 Team)
-- Lưu ý: Id của team tự tăng (SERIAL)
INSERT INTO rescue_teams (name, status, quantity, warehouse_id)
VALUES 
('Đội Cứu Hộ Q1 - T1', 'ACTIVE', 7, (SELECT id FROM warehouses WHERE resource_id = 'WH-001')),
('Đội Cứu Hộ Q1 - T2', 'ACTIVE', 7, (SELECT id FROM warehouses WHERE resource_id = 'WH-001')),
('Đội Cứu Hộ Q3 - T1', 'ACTIVE', 7, (SELECT id FROM warehouses WHERE resource_id = 'WH-002')),
('Đội Cứu Hộ Q3 - T2', 'ACTIVE', 7, (SELECT id FROM warehouses WHERE resource_id = 'WH-002'));

-- 3. Cập nhật bảng team_positions: Lấy toạ độ của Warehouse cắm qua Team
INSERT INTO team_positions (team_id, latitude, longitude, recorded_at)
SELECT 
    t.id AS team_id,
    w.latitude,
    w.longitude,
    CURRENT_TIME
FROM rescue_teams t
JOIN warehouses w ON t.warehouse_id = w.id
WHERE t.name LIKE 'Đội Cứu Hộ Q%';

-- 4. Tạo Users & Team Members (Tạo nhanh cho Đội Q1 - T1 làm mẫu xài 7 mem)
INSERT INTO users (full_name, email, phone_number, password_hash, role_id, is_active)
SELECT 'Member ' || generate_series(1,7), 'q1_t1_m' || generate_series(1,7) || '@rescue.com', '090100000' || generate_series(1,7), 'hashed_password_123', 3, true;

-- Lập map 7 user đó vào team
INSERT INTO team_members (user_id, team_id, role_in_team)
SELECT u.id, (SELECT id FROM rescue_teams WHERE name = 'Đội Cứu Hộ Q1 - T1'), 
CASE WHEN u.email = 'q1_t1_m1@rescue.com' THEN 'LEADER' ELSE 'MEMBER' END
FROM users u WHERE u.email LIKE 'q1_t1_%';
