-- =============================================
-- CCLEARLY DATABASE SEED DATA
-- Auto-run by Spring Boot on startup
-- Password: Abc@12345 (BCrypt encoded)
-- =============================================

-- =============================================
-- 1. ROLES
-- =============================================
IF NOT EXISTS (SELECT * FROM [Roles] WHERE role_name = 'ADMIN')
    INSERT INTO [Roles] (role_id, role_name, description) VALUES (NEWID(), 'ADMIN', N'Quản trị viên hệ thống');

IF NOT EXISTS (SELECT * FROM [Roles] WHERE role_name = 'MANAGER')
    INSERT INTO [Roles] (role_id, role_name, description) VALUES (NEWID(), 'MANAGER', N'Quản lý cửa hàng');

IF NOT EXISTS (SELECT * FROM [Roles] WHERE role_name = 'SALES_STAFF')
    INSERT INTO [Roles] (role_id, role_name, description) VALUES (NEWID(), 'SALES_STAFF', N'Nhân viên bán hàng');

IF NOT EXISTS (SELECT * FROM [Roles] WHERE role_name = 'OPERATION_STAFF')
    INSERT INTO [Roles] (role_id, role_name, description) VALUES (NEWID(), 'OPERATION_STAFF', N'Nhân viên vận hành');

IF NOT EXISTS (SELECT * FROM [Roles] WHERE role_name = 'CUSTOMER')
    INSERT INTO [Roles] (role_id, role_name, description) VALUES (NEWID(), 'CUSTOMER', N'Khách hàng');

-- =============================================
-- 2. PERMISSIONS
-- =============================================
IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'USER_READ')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'USER_READ', N'Xem thông tin người dùng');

IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'USER_WRITE')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'USER_WRITE', N'Chỉnh sửa thông tin người dùng');

IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'PRODUCT_READ')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'PRODUCT_READ', N'Xem sản phẩm');

IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'PRODUCT_WRITE')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'PRODUCT_WRITE', N'Thêm/sửa/xóa sản phẩm');

IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'ORDER_READ')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'ORDER_READ', N'Xem đơn hàng');

IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'ORDER_WRITE')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'ORDER_WRITE', N'Xử lý đơn hàng');

IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'INVENTORY_READ')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'INVENTORY_READ', N'Xem tồn kho');

IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'INVENTORY_WRITE')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'INVENTORY_WRITE', N'Quản lý tồn kho');

IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'REPORT_READ')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'REPORT_READ', N'Xem báo cáo');

IF NOT EXISTS (SELECT * FROM [Permissions] WHERE slug = 'CONFIG_WRITE')
    INSERT INTO [Permissions] (permission_id, slug, description) VALUES (NEWID(), 'CONFIG_WRITE', N'Cấu hình hệ thống');

-- =============================================
-- 3. ROLE_PERMISSIONS
-- =============================================
-- ADMIN gets all permissions
INSERT INTO [Role_Permissions] (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM [Roles] r, [Permissions] p 
WHERE r.role_name = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM [Role_Permissions] rp WHERE rp.role_id = r.role_id AND rp.permission_id = p.permission_id);

-- MANAGER gets all permissions except CONFIG_WRITE
INSERT INTO [Role_Permissions] (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM [Roles] r, [Permissions] p 
WHERE r.role_name = 'MANAGER' AND p.slug != 'CONFIG_WRITE'
AND NOT EXISTS (SELECT 1 FROM [Role_Permissions] rp WHERE rp.role_id = r.role_id AND rp.permission_id = p.permission_id);

-- SALES_STAFF: product, order
INSERT INTO [Role_Permissions] (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM [Roles] r, [Permissions] p 
WHERE r.role_name = 'SALES_STAFF' AND p.slug IN ('PRODUCT_READ', 'PRODUCT_WRITE', 'ORDER_READ', 'ORDER_WRITE')
AND NOT EXISTS (SELECT 1 FROM [Role_Permissions] rp WHERE rp.role_id = r.role_id AND rp.permission_id = p.permission_id);

-- OPERATION_STAFF: inventory, order
INSERT INTO [Role_Permissions] (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM [Roles] r, [Permissions] p 
WHERE r.role_name = 'OPERATION_STAFF' AND p.slug IN ('INVENTORY_READ', 'INVENTORY_WRITE', 'ORDER_READ', 'ORDER_WRITE')
AND NOT EXISTS (SELECT 1 FROM [Role_Permissions] rp WHERE rp.role_id = r.role_id AND rp.permission_id = p.permission_id);

-- =============================================
-- 4. USERS (Password: Abc@12345)
-- BCrypt hash generated with strength 10
-- =============================================
IF NOT EXISTS (SELECT * FROM [Users] WHERE email = 'admin@cclearly.com')
    INSERT INTO [Users] (user_id, email, password_hash, full_name, phone_number, role_id, status, is_email_verified, created_at)
    SELECT NEWID(), 'admin@cclearly.com', '$2a$10$QzSf/hgRoo3mkItxEyUDs.xCgnn7uacO.FDHTEZnNDACHrXhq08SK', N'Admin CClearly', '0901234567', role_id, 'ACTIVE', 1, GETUTCDATE()
    FROM [Roles] WHERE role_name = 'ADMIN';

IF NOT EXISTS (SELECT * FROM [Users] WHERE email = 'manager@cclearly.com')
    INSERT INTO [Users] (user_id, email, password_hash, full_name, phone_number, role_id, status, is_email_verified, created_at)
    SELECT NEWID(), 'manager@cclearly.com', '$2a$10$QzSf/hgRoo3mkItxEyUDs.xCgnn7uacO.FDHTEZnNDACHrXhq08SK', N'Nguyễn Văn Quản Lý', '0901234568', role_id, 'ACTIVE', 1, GETUTCDATE()
    FROM [Roles] WHERE role_name = 'MANAGER';

IF NOT EXISTS (SELECT * FROM [Users] WHERE email = 'sales@cclearly.com')
    INSERT INTO [Users] (user_id, email, password_hash, full_name, phone_number, role_id, status, is_email_verified, created_at)
    SELECT NEWID(), 'sales@cclearly.com', '$2a$10$QzSf/hgRoo3mkItxEyUDs.xCgnn7uacO.FDHTEZnNDACHrXhq08SK', N'Trần Thị Bán Hàng', '0901234569', role_id, 'ACTIVE', 1, GETUTCDATE()
    FROM [Roles] WHERE role_name = 'SALES_STAFF';

IF NOT EXISTS (SELECT * FROM [Users] WHERE email = 'operation@cclearly.com')
    INSERT INTO [Users] (user_id, email, password_hash, full_name, phone_number, role_id, status, is_email_verified, created_at)
    SELECT NEWID(), 'operation@cclearly.com', '$2a$10$QzSf/hgRoo3mkItxEyUDs.xCgnn7uacO.FDHTEZnNDACHrXhq08SK', N'Lê Văn Vận Hành', '0901234570', role_id, 'ACTIVE', 1, GETUTCDATE()
    FROM [Roles] WHERE role_name = 'OPERATION_STAFF';

IF NOT EXISTS (SELECT * FROM [Users] WHERE email = 'customer1@gmail.com')
    INSERT INTO [Users] (user_id, email, password_hash, full_name, phone_number, role_id, status, is_email_verified, created_at)
    SELECT NEWID(), 'customer1@gmail.com', '$2a$10$QzSf/hgRoo3mkItxEyUDs.xCgnn7uacO.FDHTEZnNDACHrXhq08SK', N'Phạm Văn Khách 1', '0912345678', role_id, 'ACTIVE', 1, GETUTCDATE()
    FROM [Roles] WHERE role_name = 'CUSTOMER';

IF NOT EXISTS (SELECT * FROM [Users] WHERE email = 'customer2@gmail.com')
    INSERT INTO [Users] (user_id, email, password_hash, full_name, phone_number, role_id, status, is_email_verified, created_at)
    SELECT NEWID(), 'customer2@gmail.com', '$2a$10$QzSf/hgRoo3mkItxEyUDs.xCgnn7uacO.FDHTEZnNDACHrXhq08SK', N'Hoàng Thị Khách 2', '0912345679', role_id, 'ACTIVE', 1, GETUTCDATE()
    FROM [Roles] WHERE role_name = 'CUSTOMER';

-- =============================================
-- 5. STAFF_PROFILES
-- =============================================
INSERT INTO [Staff_Profiles] (user_id)
SELECT user_id FROM [Users] WHERE email = 'manager@cclearly.com'
AND NOT EXISTS (SELECT 1 FROM [Staff_Profiles] sp WHERE sp.user_id = Users.user_id);

INSERT INTO [Staff_Profiles] (user_id)
SELECT user_id FROM [Users] WHERE email = 'sales@cclearly.com'
AND NOT EXISTS (SELECT 1 FROM [Staff_Profiles] sp WHERE sp.user_id = Users.user_id);

INSERT INTO [Staff_Profiles] (user_id)
SELECT user_id FROM [Users] WHERE email = 'operation@cclearly.com'
AND NOT EXISTS (SELECT 1 FROM [Staff_Profiles] sp WHERE sp.user_id = Users.user_id);

-- =============================================
-- 6. WAREHOUSES
-- =============================================
IF NOT EXISTS (SELECT * FROM [Warehouses] WHERE name = N'Kho chính Hà Nội')
    INSERT INTO [Warehouses] (warehouse_id, name) VALUES (NEWID(), N'Kho chính Hà Nội');

IF NOT EXISTS (SELECT * FROM [Warehouses] WHERE name = N'Kho TP.HCM')
    INSERT INTO [Warehouses] (warehouse_id, name) VALUES (NEWID(), N'Kho TP.HCM');

-- =============================================
-- 7. MASTER_LENS_TECHNOLOGIES
-- =============================================
IF NOT EXISTS (SELECT * FROM [Master_Lens_Technologies] WHERE name = N'Lọc ánh sáng xanh')
    INSERT INTO [Master_Lens_Technologies] (tech_id, name) VALUES (NEWID(), N'Lọc ánh sáng xanh');

IF NOT EXISTS (SELECT * FROM [Master_Lens_Technologies] WHERE name = N'Đổi màu')
    INSERT INTO [Master_Lens_Technologies] (tech_id, name) VALUES (NEWID(), N'Đổi màu');

IF NOT EXISTS (SELECT * FROM [Master_Lens_Technologies] WHERE name = N'Phân cực')
    INSERT INTO [Master_Lens_Technologies] (tech_id, name) VALUES (NEWID(), N'Phân cực');

IF NOT EXISTS (SELECT * FROM [Master_Lens_Technologies] WHERE name = N'Chống UV')
    INSERT INTO [Master_Lens_Technologies] (tech_id, name) VALUES (NEWID(), N'Chống UV');

IF NOT EXISTS (SELECT * FROM [Master_Lens_Technologies] WHERE name = N'Chống trầy')
    INSERT INTO [Master_Lens_Technologies] (tech_id, name) VALUES (NEWID(), N'Chống trầy');

-- =============================================
-- 8. PRODUCTS - FRAMES
-- =============================================
IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Ray-Ban Aviator Classic')
BEGIN
    DECLARE @p1 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@p1, N'Ray-Ban Aviator Classic', 'FRAME', N'Kính mát', 3500000, 1);
    INSERT INTO [Product_Frames] (product_id, material, shape, lens_width_mm, bridge_width_mm, temple_length_mm) VALUES (@p1, N'Kim loại', N'Phi công', 58, 14, 135);
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Oakley Holbrook')
BEGIN
    DECLARE @p2 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@p2, N'Oakley Holbrook', 'FRAME', N'Kính mát', 2800000, 1);
    INSERT INTO [Product_Frames] (product_id, material, shape, lens_width_mm, bridge_width_mm, temple_length_mm) VALUES (@p2, N'Nhựa', N'Vuông', 55, 18, 137);
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Gucci GG0061S')
BEGIN
    DECLARE @p3 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@p3, N'Gucci GG0061S', 'FRAME', N'Gọng cận', 7500000, 1);
    INSERT INTO [Product_Frames] (product_id, material, shape, lens_width_mm, bridge_width_mm, temple_length_mm) VALUES (@p3, N'Acetate', N'Mắt mèo', 56, 15, 140);
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Tommy Hilfiger TH1794')
BEGIN
    DECLARE @p4 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@p4, N'Tommy Hilfiger TH1794', 'FRAME', N'Trẻ em', 1800000, 1);
    INSERT INTO [Product_Frames] (product_id, material, shape, lens_width_mm, bridge_width_mm, temple_length_mm) VALUES (@p4, N'Kim loại', N'Chữ nhật', 52, 17, 140);
END;

-- =============================================
-- 9. PRODUCTS - LENSES (refractive_index now in variants)
-- =============================================
IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Essilor Crizal Sapphire')
BEGIN
    DECLARE @l1 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@l1, N'Essilor Crizal Sapphire', 'LENS', N'Tròng cận', 2500000, 1);
    INSERT INTO [Product_Lenses] (product_id, material, lens_type) VALUES (@l1, N'Nhựa CR-39', N'Đơn tròng');
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Zeiss Digital Lens')
BEGIN
    DECLARE @l2 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@l2, N'Zeiss Digital Lens', 'LENS', N'Tròng cận', 4500000, 1);
    INSERT INTO [Product_Lenses] (product_id, material, lens_type) VALUES (@l2, N'Polycarbonate', N'Đa tròng lũy tiến');
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Hoya Blue Control')
BEGIN
    DECLARE @l3 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@l3, N'Hoya Blue Control', 'LENS', N'Tròng cận', 1800000, 1);
    INSERT INTO [Product_Lenses] (product_id, material, lens_type) VALUES (@l3, N'Nhựa CR-39', N'Đơn tròng');
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Nikon Lite AS')
BEGIN
    DECLARE @l4 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@l4, N'Nikon Lite AS', 'LENS', N'Tròng cận', 2200000, 1);
    INSERT INTO [Product_Lenses] (product_id, material, lens_type) VALUES (@l4, N'Trivex', N'Đơn tròng');
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Rodenstock Multigressiv')
BEGIN
    DECLARE @l5 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@l5, N'Rodenstock Multigressiv', 'LENS', N'Tròng cận', 5500000, 1);
    INSERT INTO [Product_Lenses] (product_id, material, lens_type) VALUES (@l5, N'Thủy tinh', N'Đa tròng lũy tiến');
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Chemi Crystal')
BEGIN
    DECLARE @l6 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@l6, N'Chemi Crystal', 'LENS', N'Tròng mát', 1500000, 1);
    INSERT INTO [Product_Lenses] (product_id, material, lens_type) VALUES (@l6, N'MR-8', N'Đơn tròng');
END;

-- =============================================
-- 9a. PRODUCTS - PREORDER FRAMES
-- =============================================
IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Prada PR 17WV')
BEGIN
    DECLARE @pp1 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@pp1, N'Prada PR 17WV', 'FRAME', N'Gọng cận', 8500000, 1);
    INSERT INTO [Product_Frames] (product_id, material, shape, lens_width_mm, bridge_width_mm, temple_length_mm) VALUES (@pp1, N'Acetate', N'Vuông', 54, 18, 140);
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Dior DiorBlackSuitO S1I')
BEGIN
    DECLARE @pp2 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@pp2, N'Dior DiorBlackSuitO S1I', 'FRAME', N'Kính mát', 12000000, 1);
    INSERT INTO [Product_Frames] (product_id, material, shape, lens_width_mm, bridge_width_mm, temple_length_mm) VALUES (@pp2, N'Acetate', N'Tròn', 52, 20, 145);
END;

-- =============================================
-- 9b. PRODUCTS - ACCESSORIES
-- =============================================
IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Hộp đựng kính cao cấp')
BEGIN
    DECLARE @a1 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@a1, N'Hộp đựng kính cao cấp', 'ACCESSORY', NULL, 250000, 1);
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Khăn lau kính vi sợi')
BEGIN
    DECLARE @a2 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@a2, N'Khăn lau kính vi sợi', 'ACCESSORY', NULL, 50000, 1);
END;

IF NOT EXISTS (SELECT * FROM [Products] WHERE name = N'Dung dịch rửa kính')
BEGIN
    DECLARE @a3 UNIQUEIDENTIFIER = NEWID();
    INSERT INTO [Products] (product_id, name, category_type, sub_category, base_price, is_active) VALUES (@a3, N'Dung dịch rửa kính', 'ACCESSORY', NULL, 120000, 1);
END;

-- =============================================
-- 9c. LENS TECHNOLOGY MAPPINGS
-- =============================================
-- Essilor Crizal Sapphire: Chống UV, Chống trầy
INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Essilor Crizal Sapphire' AND t.name = N'Chống UV'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Essilor Crizal Sapphire' AND t.name = N'Chống trầy'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

-- Hoya Blue Control: Lọc ánh sáng xanh, Chống trầy
INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Hoya Blue Control' AND t.name = N'Lọc ánh sáng xanh'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Hoya Blue Control' AND t.name = N'Chống trầy'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

-- Zeiss Digital Lens: Lọc ánh sáng xanh, Chống UV
INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Zeiss Digital Lens' AND t.name = N'Lọc ánh sáng xanh'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Zeiss Digital Lens' AND t.name = N'Chống UV'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

-- Nikon Lite AS: Chống trầy, Chống UV
INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Nikon Lite AS' AND t.name = N'Chống trầy'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Nikon Lite AS' AND t.name = N'Chống UV'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

-- Rodenstock Multigressiv: Chống UV
INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Rodenstock Multigressiv' AND t.name = N'Chống UV'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

-- Chemi Crystal: Lọc ánh sáng xanh, Chống trầy
INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Chemi Crystal' AND t.name = N'Lọc ánh sáng xanh'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

INSERT INTO [Product_Lens_Tech_Map] (product_id, tech_id)
SELECT p.product_id, t.tech_id FROM [Products] p, [Master_Lens_Technologies] t
WHERE p.name = N'Chemi Crystal' AND t.name = N'Chống trầy'
AND NOT EXISTS (SELECT 1 FROM [Product_Lens_Tech_Map] m WHERE m.product_id = p.product_id AND m.tech_id = t.tech_id);

-- =============================================
-- 10. PRODUCT_VARIANTS
-- =============================================
-- Frame variants (color-based)
IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'RB-AV-GOLD')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'RB-AV-GOLD', N'Vàng', 3500000, 0 FROM [Products] WHERE name = N'Ray-Ban Aviator Classic';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'RB-AV-SILVER')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'RB-AV-SILVER', N'Bạc', 3500000, 0 FROM [Products] WHERE name = N'Ray-Ban Aviator Classic';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'RB-AV-BLACK')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'RB-AV-BLACK', N'Đen', 3700000, 0 FROM [Products] WHERE name = N'Ray-Ban Aviator Classic';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'OAK-HB-BLACK')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'OAK-HB-BLACK', N'Đen mờ', 2800000, 0 FROM [Products] WHERE name = N'Oakley Holbrook';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'OAK-HB-TORT')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'OAK-HB-TORT', N'Đồi mồi', 2900000, 0 FROM [Products] WHERE name = N'Oakley Holbrook';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'GG-0061-BLACK')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'GG-0061-BLACK', N'Đen/Vàng', 7500000, 0 FROM [Products] WHERE name = N'Gucci GG0061S';

-- Lens variants (refractive index-based, price varies by index)
IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'ESS-CS-1.56')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'ESS-CS-1.56', NULL, 1.56, 2500000, 0 FROM [Products] WHERE name = N'Essilor Crizal Sapphire';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'ESS-CS-1.6')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'ESS-CS-1.6', NULL, 1.6, 3200000, 0 FROM [Products] WHERE name = N'Essilor Crizal Sapphire';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'ESS-CS-1.67')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'ESS-CS-1.67', NULL, 1.67, 4500000, 0 FROM [Products] WHERE name = N'Essilor Crizal Sapphire';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'ESS-CS-1.74')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'ESS-CS-1.74', NULL, 1.74, 6800000, 0 FROM [Products] WHERE name = N'Essilor Crizal Sapphire';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'ZS-DL-1.6')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'ZS-DL-1.6', NULL, 1.6, 4500000, 0 FROM [Products] WHERE name = N'Zeiss Digital Lens';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'ZS-DL-1.67')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'ZS-DL-1.67', NULL, 1.67, 6000000, 0 FROM [Products] WHERE name = N'Zeiss Digital Lens';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'ZS-DL-1.74')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'ZS-DL-1.74', NULL, 1.74, 8500000, 0 FROM [Products] WHERE name = N'Zeiss Digital Lens';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'HY-BC-1.56')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'HY-BC-1.56', NULL, 1.56, 1800000, 0 FROM [Products] WHERE name = N'Hoya Blue Control';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'HY-BC-1.6')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'HY-BC-1.6', NULL, 1.6, 2500000, 0 FROM [Products] WHERE name = N'Hoya Blue Control';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'HY-BC-1.67')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'HY-BC-1.67', NULL, 1.67, 3500000, 0 FROM [Products] WHERE name = N'Hoya Blue Control';

-- Nikon Lite AS variants (Trivex)
IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'NK-LA-1.53')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'NK-LA-1.53', NULL, 1.53, 2200000, 0 FROM [Products] WHERE name = N'Nikon Lite AS';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'NK-LA-1.6')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'NK-LA-1.6', NULL, 1.6, 2900000, 0 FROM [Products] WHERE name = N'Nikon Lite AS';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'NK-LA-1.67')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'NK-LA-1.67', NULL, 1.67, 4200000, 0 FROM [Products] WHERE name = N'Nikon Lite AS';

-- Rodenstock Multigressiv variants (Thủy tinh)
IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'RD-MG-1.6')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'RD-MG-1.6', NULL, 1.6, 5500000, 0 FROM [Products] WHERE name = N'Rodenstock Multigressiv';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'RD-MG-1.67')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'RD-MG-1.67', NULL, 1.67, 7200000, 0 FROM [Products] WHERE name = N'Rodenstock Multigressiv';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'RD-MG-1.74')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'RD-MG-1.74', NULL, 1.74, 9500000, 0 FROM [Products] WHERE name = N'Rodenstock Multigressiv';

-- Chemi Crystal variants (MR-8)
IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'CH-CR-1.56')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'CH-CR-1.56', NULL, 1.56, 1500000, 0 FROM [Products] WHERE name = N'Chemi Crystal';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'CH-CR-1.6')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'CH-CR-1.6', NULL, 1.6, 2000000, 0 FROM [Products] WHERE name = N'Chemi Crystal';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'CH-CR-1.67')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, refractive_index, sale_price, is_preorder)
    SELECT NEWID(), product_id, 'CH-CR-1.67', NULL, 1.67, 3000000, 0 FROM [Products] WHERE name = N'Chemi Crystal';

-- Preorder frame variants
IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'PR-17WV-BLK')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder, expected_availability)
    SELECT NEWID(), product_id, 'PR-17WV-BLK', N'Đen', 8500000, 1, '2026-05-01' FROM [Products] WHERE name = N'Prada PR 17WV';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'PR-17WV-TORT')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder, expected_availability)
    SELECT NEWID(), product_id, 'PR-17WV-TORT', N'Đồi mồi', 8500000, 1, '2026-05-01' FROM [Products] WHERE name = N'Prada PR 17WV';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'DIOR-BS-BLK')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder, expected_availability)
    SELECT NEWID(), product_id, 'DIOR-BS-BLK', N'Đen', 12000000, 1, '2026-06-15' FROM [Products] WHERE name = N'Dior DiorBlackSuitO S1I';

IF NOT EXISTS (SELECT * FROM [Product_Variants] WHERE sku = 'DIOR-BS-HAVANA')
    INSERT INTO [Product_Variants] (variant_id, product_id, sku, color_name, sale_price, is_preorder, expected_availability)
    SELECT NEWID(), product_id, 'DIOR-BS-HAVANA', N'Nâu Havana', 12500000, 1, '2026-06-15' FROM [Products] WHERE name = N'Dior DiorBlackSuitO S1I';

-- =============================================
-- 11. PRODUCT_IMAGES
-- =============================================
INSERT INTO [Product_Images] (image_id, product_id, variant_id, image_url)
SELECT NEWID(), p.product_id, v.variant_id, 'https://res.cloudinary.com/cclearly/image/upload/v1/products/rayban-aviator-gold.jpg'
FROM [Products] p JOIN [Product_Variants] v ON p.product_id = v.product_id
WHERE v.sku = 'RB-AV-GOLD'
AND NOT EXISTS (SELECT 1 FROM [Product_Images] pi WHERE pi.variant_id = v.variant_id);

INSERT INTO [Product_Images] (image_id, product_id, variant_id, image_url)
SELECT NEWID(), p.product_id, v.variant_id, 'https://res.cloudinary.com/cclearly/image/upload/v1/products/rayban-aviator-silver.jpg'
FROM [Products] p JOIN [Product_Variants] v ON p.product_id = v.product_id
WHERE v.sku = 'RB-AV-SILVER'
AND NOT EXISTS (SELECT 1 FROM [Product_Images] pi WHERE pi.variant_id = v.variant_id);

INSERT INTO [Product_Images] (image_id, product_id, variant_id, image_url)
SELECT NEWID(), p.product_id, v.variant_id, 'https://res.cloudinary.com/cclearly/image/upload/v1/products/oakley-holbrook-black.jpg'
FROM [Products] p JOIN [Product_Variants] v ON p.product_id = v.product_id
WHERE v.sku = 'OAK-HB-BLACK'
AND NOT EXISTS (SELECT 1 FROM [Product_Images] pi WHERE pi.variant_id = v.variant_id);

INSERT INTO [Product_Images] (image_id, product_id, variant_id, image_url)
SELECT NEWID(), p.product_id, v.variant_id, 'https://res.cloudinary.com/cclearly/image/upload/v1/products/gucci-cateye.jpg'
FROM [Products] p JOIN [Product_Variants] v ON p.product_id = v.product_id
WHERE v.sku = 'GG-0061-BLACK'
AND NOT EXISTS (SELECT 1 FROM [Product_Images] pi WHERE pi.variant_id = v.variant_id);

-- =============================================
-- 12. INVENTORY_STOCK
-- =============================================
INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 50
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'RB-AV-GOLD' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 30
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'RB-AV-SILVER' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 25
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'RB-AV-BLACK' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 40
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'OAK-HB-BLACK' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 35
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'OAK-HB-TORT' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 10
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'GG-0061-BLACK' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

-- HCM warehouse
INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 20
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'RB-AV-GOLD' AND w.name = N'Kho TP.HCM'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 15
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'OAK-HB-BLACK' AND w.name = N'Kho TP.HCM'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

-- Lens variant inventory (Hà Nội)
INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 100
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'ESS-CS-1.56' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 80
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'ESS-CS-1.6' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 60
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'ESS-CS-1.67' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 30
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'ESS-CS-1.74' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 70
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'HY-BC-1.56' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 50
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'HY-BC-1.6' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 40
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'HY-BC-1.67' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 50
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'ZS-DL-1.6' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 35
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'ZS-DL-1.67' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 20
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'ZS-DL-1.74' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

-- Nikon Lite AS inventory (Trivex)
INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 90
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'NK-LA-1.53' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 65
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'NK-LA-1.6' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 40
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'NK-LA-1.67' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

-- Rodenstock Multigressiv inventory (Thủy tinh)
INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 25
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'RD-MG-1.6' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 15
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'RD-MG-1.67' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 10
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'RD-MG-1.74' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

-- Chemi Crystal inventory (MR-8)
INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 120
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'CH-CR-1.56' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 85
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'CH-CR-1.6' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 55
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'CH-CR-1.67' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

-- Preorder variants - 0 stock (will be available later)
INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 0
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'PR-17WV-BLK' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 0
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'PR-17WV-TORT' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 0
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'DIOR-BS-BLK' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

INSERT INTO [Inventory_Stock] (variant_id, warehouse_id, quantity_on_hand)
SELECT v.variant_id, w.warehouse_id, 0
FROM [Product_Variants] v, [Warehouses] w
WHERE v.sku = 'DIOR-BS-HAVANA' AND w.name = N'Kho chính Hà Nội'
AND NOT EXISTS (SELECT 1 FROM [Inventory_Stock] s WHERE s.variant_id = v.variant_id AND s.warehouse_id = w.warehouse_id);

-- =============================================
-- 13. PROMOTIONS
-- =============================================
IF NOT EXISTS (SELECT * FROM [Promotions] WHERE code = 'WELCOME10')
    INSERT INTO [Promotions] (promotion_id, code, discount_type, value) VALUES (NEWID(), 'WELCOME10', 'PERCENTAGE', 10);

IF NOT EXISTS (SELECT * FROM [Promotions] WHERE code = 'NEWYEAR50K')
    INSERT INTO [Promotions] (promotion_id, code, discount_type, value) VALUES (NEWID(), 'NEWYEAR50K', 'FIXED', 50000);

IF NOT EXISTS (SELECT * FROM [Promotions] WHERE code = 'VIP20')
    INSERT INTO [Promotions] (promotion_id, code, discount_type, value) VALUES (NEWID(), 'VIP20', 'PERCENTAGE', 20);

-- =============================================
-- 14. SYSTEM_CONFIGS
-- =============================================
IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'shop_name')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('shop_name', N'CClearly - Kính Mắt Chính Hãng', 'general');

IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'shop_email')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('shop_email', 'contact@cclearly.com', 'general');

IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'shop_phone')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('shop_phone', '1900 1234', 'general');

IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'shop_address')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('shop_address', N'123 Nguyễn Huệ, Quận 1, TP.HCM', 'general');

IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'currency')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('currency', 'VND', 'payment');

IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'tax_rate')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('tax_rate', '10', 'payment');

IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'free_shipping_threshold')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('free_shipping_threshold', '500000', 'shipping');

IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'default_shipping_fee')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('default_shipping_fee', '30000', 'shipping');

IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'max_cart_items')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('max_cart_items', '20', 'cart');

IF NOT EXISTS (SELECT * FROM [System_Configs] WHERE config_key = 'order_expiry_minutes')
    INSERT INTO [System_Configs] (config_key, config_value, config_group) VALUES ('order_expiry_minutes', '30', 'order');

-- =============================================
-- 15. CONTENT_BANNERS
-- =============================================
-- NOTE: Sử dụng ảnh placeholder từ Unsplash. Để có ảnh thật, upload lên Cloudinary:
-- 1. Đăng nhập tài khoản Cloudinary
-- 2. Sử dụng API upload có sẵn: POST /api/upload/image (folder='banners')
-- 3. Hoặc upload trực tiếp qua Cloudinary Console > Media Library > Upload
-- 4. Copy URL trả về và cập nhật vào banner qua trang admin

IF NOT EXISTS (SELECT * FROM [Content_Banners] WHERE title = N'Miễn phí đo mắt & tư vấn')
    INSERT INTO [Content_Banners] (banner_id, title, image_url, position, display_order, is_active)
    VALUES (NEWID(), N'Miễn phí đo mắt & tư vấn', 'https://images.unsplash.com/photo-1516062423079-7ca13cdc7f5a?q=80&w=2083&auto=format&fit=crop', 'HEADER', 1, 1);

IF NOT EXISTS (SELECT * FROM [Content_Banners] WHERE title = N'Đại tiệc gọng kính - Giảm tới 50%')
    INSERT INTO [Content_Banners] (banner_id, title, image_url, position, display_order, is_active)
    VALUES (NEWID(), N'Đại tiệc gọng kính - Giảm tới 50%', 'https://images.unsplash.com/photo-1574258495973-f010dfbb5371?q=80&w=2070&auto=format&fit=crop', 'HOME_MAIN', 1, 1);

IF NOT EXISTS (SELECT * FROM [Content_Banners] WHERE title = N'Kính cận thị cho trẻ em')
    INSERT INTO [Content_Banners] (banner_id, title, image_url, position, display_order, is_active)
    VALUES (NEWID(), N'Kính cận thị cho trẻ em', 'https://images.unsplash.com/photo-1543332164-6e82f355badc?q=80&w=2070&auto=format&fit=crop', 'HOME_MAIN', 2, 1);

IF NOT EXISTS (SELECT * FROM [Content_Banners] WHERE title = N'Bộ sưu tập Kính Râm 2026')
    INSERT INTO [Content_Banners] (banner_id, title, image_url, position, display_order, is_active)
    VALUES (NEWID(), N'Bộ sưu tập Kính Râm 2026', 'https://images.unsplash.com/photo-1511499767390-a73359580bf1?q=80&w=1780&auto=format&fit=crop', 'HOME_PROMO', 1, 1);

IF NOT EXISTS (SELECT * FROM [Content_Banners] WHERE title = N'Flash Sale Cuối Tuần')
    INSERT INTO [Content_Banners] (banner_id, title, image_url, position, display_order, is_active)
    VALUES (NEWID(), N'Flash Sale Cuối Tuần', 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?q=80&w=2070&auto=format&fit=crop', 'HOME_MAIN', 3, 0);

-- =============================================
-- 16. ADDRESSES
-- =============================================
-- Customer 1 addresses
IF NOT EXISTS (SELECT * FROM [Addresses] a JOIN [Users] u ON a.user_id = u.user_id WHERE u.email = 'customer1@gmail.com' AND a.name = N'Phạm Văn Khách 1')
    INSERT INTO [Addresses] (address_id, user_id, name, phone, street, city, is_default)
    SELECT NEWID(), user_id, N'Phạm Văn Khách 1', '0912345678', N'123 Nguyễn Trãi, Phường Bến Thành, Quận 1', N'TP. Hồ Chí Minh', 1
    FROM [Users] WHERE email = 'customer1@gmail.com';

IF NOT EXISTS (SELECT * FROM [Addresses] a JOIN [Users] u ON a.user_id = u.user_id WHERE u.email = 'customer1@gmail.com' AND a.name = N'Phạm Văn Khách 1 - Công ty')
    INSERT INTO [Addresses] (address_id, user_id, name, phone, street, city, is_default)
    SELECT NEWID(), user_id, N'Phạm Văn Khách 1 - Công ty', '0912345600', N'456 Lê Lợi, Phường Bến Nghé, Quận 1', N'TP. Hồ Chí Minh', 0
    FROM [Users] WHERE email = 'customer1@gmail.com';

-- Customer 2 addresses
IF NOT EXISTS (SELECT * FROM [Addresses] a JOIN [Users] u ON a.user_id = u.user_id WHERE u.email = 'customer2@gmail.com' AND a.name = N'Hoàng Thị Khách 2')
    INSERT INTO [Addresses] (address_id, user_id, name, phone, street, city, is_default)
    SELECT NEWID(), user_id, N'Hoàng Thị Khách 2', '0912345679', N'789 Trần Hưng Đạo, Phường 1, Quận 5', N'TP. Hồ Chí Minh', 1
    FROM [Users] WHERE email = 'customer2@gmail.com';

IF NOT EXISTS (SELECT * FROM [Addresses] a JOIN [Users] u ON a.user_id = u.user_id WHERE u.email = 'customer2@gmail.com' AND a.name = N'Hoàng Thị Khách 2 - Nhà riêng HN')
    INSERT INTO [Addresses] (address_id, user_id, name, phone, street, city, is_default)
    SELECT NEWID(), user_id, N'Hoàng Thị Khách 2 - Nhà riêng HN', '0912345699', N'15 Phố Huế, Phường Hàng Bài, Quận Hoàn Kiếm', N'Hà Nội', 0
    FROM [Users] WHERE email = 'customer2@gmail.com';

-- =============================================
-- 17. AUDIT_LOGS (Mock data)
-- =============================================
-- LOGIN logs
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'admin@cclearly.com' AND al.action = 'LOGIN' AND al.details = N'Đăng nhập hệ thống: admin@cclearly.com')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'LOGIN', N'Đăng nhập hệ thống: admin@cclearly.com', '113.161.72.45', DATEADD(HOUR, -72, GETUTCDATE())
    FROM [Users] WHERE email = 'admin@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'manager@cclearly.com' AND al.action = 'LOGIN' AND al.details = N'Đăng nhập hệ thống: manager@cclearly.com')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'LOGIN', N'Đăng nhập hệ thống: manager@cclearly.com', '42.118.134.92', DATEADD(HOUR, -70, GETUTCDATE())
    FROM [Users] WHERE email = 'manager@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'sales@cclearly.com' AND al.action = 'LOGIN' AND al.details = N'Đăng nhập hệ thống: sales@cclearly.com')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'LOGIN', N'Đăng nhập hệ thống: sales@cclearly.com', '14.232.166.78', DATEADD(HOUR, -68, GETUTCDATE())
    FROM [Users] WHERE email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'operation@cclearly.com' AND al.action = 'LOGIN' AND al.details = N'Đăng nhập hệ thống: operation@cclearly.com')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'LOGIN', N'Đăng nhập hệ thống: operation@cclearly.com', '171.252.113.201', DATEADD(HOUR, -65, GETUTCDATE())
    FROM [Users] WHERE email = 'operation@cclearly.com';

-- ADD_PRODUCT logs (sales staff added products)
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'sales@cclearly.com' AND al.action = 'ADD_PRODUCT' AND al.details LIKE N'%Ray-Ban Aviator Classic%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'ADD_PRODUCT', N'Thêm sản phẩm: Ray-Ban Aviator Classic', '14.232.166.78', DATEADD(HOUR, -66, GETUTCDATE())
    FROM [Users] WHERE email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'sales@cclearly.com' AND al.action = 'ADD_PRODUCT' AND al.details LIKE N'%Oakley Holbrook%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'ADD_PRODUCT', N'Thêm sản phẩm: Oakley Holbrook', '14.232.166.78', DATEADD(HOUR, -66, GETUTCDATE())
    FROM [Users] WHERE email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'sales@cclearly.com' AND al.action = 'ADD_PRODUCT' AND al.details LIKE N'%Gucci GG0061S%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'ADD_PRODUCT', N'Thêm sản phẩm: Gucci GG0061S', '14.232.166.78', DATEADD(HOUR, -65, GETUTCDATE())
    FROM [Users] WHERE email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'manager@cclearly.com' AND al.action = 'ADD_PRODUCT' AND al.details LIKE N'%Essilor Crizal Sapphire%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'ADD_PRODUCT', N'Thêm sản phẩm: Essilor Crizal Sapphire', '42.118.134.92', DATEADD(HOUR, -64, GETUTCDATE())
    FROM [Users] WHERE email = 'manager@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'manager@cclearly.com' AND al.action = 'ADD_PRODUCT' AND al.details LIKE N'%Hoya Blue Control%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'ADD_PRODUCT', N'Thêm sản phẩm: Hoya Blue Control', '42.118.134.92', DATEADD(HOUR, -63, GETUTCDATE())
    FROM [Users] WHERE email = 'manager@cclearly.com';

-- UPDATE_PRODUCT log
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'sales@cclearly.com' AND al.action = 'UPDATE_PRODUCT' AND al.details LIKE N'%Tommy Hilfiger TH1794%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, old_value, new_value, ip_address, created_at)
    SELECT NEWID(), user_id, 'UPDATE_PRODUCT', N'Cập nhật sản phẩm: Tommy Hilfiger TH1794', N'base_price: 1600000', N'base_price: 1800000', '14.232.166.78', DATEADD(HOUR, -50, GETUTCDATE())
    FROM [Users] WHERE email = 'sales@cclearly.com';

-- IMPORT_STOCK logs (operation staff imported stock)
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'operation@cclearly.com' AND al.action = 'IMPORT_STOCK' AND al.details LIKE N'%RB-AV-GOLD%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'IMPORT_STOCK', N'Nhập kho 50 sản phẩm Ray-Ban Aviator Classic (SKU: RB-AV-GOLD) vào Kho chính Hà Nội', '171.252.113.201', DATEADD(HOUR, -60, GETUTCDATE())
    FROM [Users] WHERE email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'operation@cclearly.com' AND al.action = 'IMPORT_STOCK' AND al.details LIKE N'%OAK-HB-BLACK%Kho chính%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'IMPORT_STOCK', N'Nhập kho 40 sản phẩm Oakley Holbrook (SKU: OAK-HB-BLACK) vào Kho chính Hà Nội', '171.252.113.201', DATEADD(HOUR, -59, GETUTCDATE())
    FROM [Users] WHERE email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'operation@cclearly.com' AND al.action = 'IMPORT_STOCK' AND al.details LIKE N'%ESS-CS-1.56%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'IMPORT_STOCK', N'Nhập kho 100 sản phẩm Essilor Crizal Sapphire (SKU: ESS-CS-1.56) vào Kho chính Hà Nội', '171.252.113.201', DATEADD(HOUR, -58, GETUTCDATE())
    FROM [Users] WHERE email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'operation@cclearly.com' AND al.action = 'IMPORT_STOCK' AND al.details LIKE N'%RB-AV-GOLD%Kho TP.HCM%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'IMPORT_STOCK', N'Nhập kho 20 sản phẩm Ray-Ban Aviator Classic (SKU: RB-AV-GOLD) vào Kho TP.HCM', '171.252.113.201', DATEADD(HOUR, -55, GETUTCDATE())
    FROM [Users] WHERE email = 'operation@cclearly.com';

-- ADD_VOUCHER logs
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'manager@cclearly.com' AND al.action = 'ADD_VOUCHER' AND al.details LIKE N'%WELCOME10%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'ADD_VOUCHER', N'Thêm voucher: WELCOME10 - Giảm 10%', '42.118.134.92', DATEADD(HOUR, -48, GETUTCDATE())
    FROM [Users] WHERE email = 'manager@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'manager@cclearly.com' AND al.action = 'ADD_VOUCHER' AND al.details LIKE N'%NEWYEAR50K%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'ADD_VOUCHER', N'Thêm voucher: NEWYEAR50K - Giảm 50,000₫', '42.118.134.92', DATEADD(HOUR, -47, GETUTCDATE())
    FROM [Users] WHERE email = 'manager@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'manager@cclearly.com' AND al.action = 'ADD_VOUCHER' AND al.details LIKE N'%VIP20%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'ADD_VOUCHER', N'Thêm voucher: VIP20 - Giảm 20%', '42.118.134.92', DATEADD(HOUR, -46, GETUTCDATE())
    FROM [Users] WHERE email = 'manager@cclearly.com';

-- UPDATE_VOUCHER log
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'manager@cclearly.com' AND al.action = 'UPDATE_VOUCHER' AND al.details LIKE N'%WELCOME10%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, old_value, new_value, ip_address, created_at)
    SELECT NEWID(), user_id, 'UPDATE_VOUCHER', N'Cập nhật voucher: WELCOME10', N'value: 5', N'value: 10', '42.118.134.92', DATEADD(HOUR, -40, GETUTCDATE())
    FROM [Users] WHERE email = 'manager@cclearly.com';

-- CHANGE_BANNER logs
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'admin@cclearly.com' AND al.action = 'CHANGE_BANNER' AND al.details LIKE N'%HOME_HERO%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'CHANGE_BANNER', N'Cập nhật banner vị trí: HOME_HERO', '113.161.72.45', DATEADD(HOUR, -36, GETUTCDATE())
    FROM [Users] WHERE email = 'admin@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'admin@cclearly.com' AND al.action = 'CHANGE_BANNER' AND al.details LIKE N'%HOME_PROMO%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'CHANGE_BANNER', N'Cập nhật banner vị trí: HOME_PROMO', '113.161.72.45', DATEADD(HOUR, -35, GETUTCDATE())
    FROM [Users] WHERE email = 'admin@cclearly.com';

-- CREATE_USER log (admin created customer accounts)
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'admin@cclearly.com' AND al.action = 'CREATE_USER' AND al.details LIKE N'%sales@cclearly.com%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'CREATE_USER', N'Cấp tài khoản nhân viên: sales@cclearly.com (SALES_STAFF)', '113.161.72.45', DATEADD(HOUR, -69, GETUTCDATE())
    FROM [Users] WHERE email = 'admin@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'admin@cclearly.com' AND al.action = 'CREATE_USER' AND al.details LIKE N'%operation@cclearly.com%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'CREATE_USER', N'Cấp tài khoản nhân viên: operation@cclearly.com (OPERATION_STAFF)', '113.161.72.45', DATEADD(HOUR, -69, GETUTCDATE())
    FROM [Users] WHERE email = 'admin@cclearly.com';

-- UPDATE_USER log
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'admin@cclearly.com' AND al.action = 'UPDATE_USER' AND al.details LIKE N'%Trần Thị Bán Hàng%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, old_value, new_value, ip_address, created_at)
    SELECT NEWID(), user_id, 'UPDATE_USER', N'Cập nhật thông tin tài khoản: Trần Thị Bán Hàng', N'phone: 0901234500', N'phone: 0901234569', '113.161.72.45', DATEADD(HOUR, -30, GETUTCDATE())
    FROM [Users] WHERE email = 'admin@cclearly.com';

-- UPDATE_SETTINGS log
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'admin@cclearly.com' AND al.action = 'UPDATE_SETTINGS' AND al.details LIKE N'%free_shipping_threshold%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, old_value, new_value, ip_address, created_at)
    SELECT NEWID(), user_id, 'UPDATE_SETTINGS', N'Cập nhật cấu hình hệ thống: free_shipping_threshold', N'300000', N'500000', '113.161.72.45', DATEADD(HOUR, -24, GETUTCDATE())
    FROM [Users] WHERE email = 'admin@cclearly.com';

-- BAN_ACCOUNT log
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'admin@cclearly.com' AND al.action = 'BAN_ACCOUNT')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'BAN_ACCOUNT', N'Khóa tài khoản người dùng: fake_user@gmail.com (vi phạm chính sách)', '113.161.72.45', DATEADD(HOUR, -20, GETUTCDATE())
    FROM [Users] WHERE email = 'admin@cclearly.com';

-- More recent LOGIN logs (various staff re-login)
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'manager@cclearly.com' AND al.action = 'LOGIN' AND al.details = N'Đăng nhập hệ thống lần 2: manager@cclearly.com')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'LOGIN', N'Đăng nhập hệ thống lần 2: manager@cclearly.com', '42.118.134.92', DATEADD(HOUR, -12, GETUTCDATE())
    FROM [Users] WHERE email = 'manager@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'admin@cclearly.com' AND al.action = 'LOGIN' AND al.details = N'Đăng nhập hệ thống lần 2: admin@cclearly.com')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'LOGIN', N'Đăng nhập hệ thống lần 2: admin@cclearly.com', '113.161.72.45', DATEADD(HOUR, -6, GETUTCDATE())
    FROM [Users] WHERE email = 'admin@cclearly.com';

-- DELETE_PRODUCT log
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'manager@cclearly.com' AND al.action = 'DELETE_PRODUCT')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'DELETE_PRODUCT', N'Xóa sản phẩm: Test Product XYZ (sản phẩm lỗi)', '42.118.134.92', DATEADD(HOUR, -8, GETUTCDATE())
    FROM [Users] WHERE email = 'manager@cclearly.com';

-- IMPORT_STOCK recent batch
IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'operation@cclearly.com' AND al.action = 'IMPORT_STOCK' AND al.details LIKE N'%HY-BC-1.56%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'IMPORT_STOCK', N'Nhập kho 70 sản phẩm Hoya Blue Control (SKU: HY-BC-1.56) vào Kho chính Hà Nội', '171.252.113.201', DATEADD(HOUR, -4, GETUTCDATE())
    FROM [Users] WHERE email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT 1 FROM [Audit_Logs] al JOIN [Users] u ON al.user_id = u.user_id WHERE u.email = 'operation@cclearly.com' AND al.action = 'IMPORT_STOCK' AND al.details LIKE N'%CH-CR-1.67%')
    INSERT INTO [Audit_Logs] (log_id, user_id, action, details, ip_address, created_at)
    SELECT NEWID(), user_id, 'IMPORT_STOCK', N'Nhập kho 55 sản phẩm Chemi Crystal (SKU: CH-CR-1.67) vào Kho chính Hà Nội', '171.252.113.201', DATEADD(HOUR, -3, GETUTCDATE())
    FROM [Users] WHERE email = 'operation@cclearly.com';

-- =============================================
-- 18. ADDITIONAL CUSTOMER USERS
-- =============================================
IF NOT EXISTS (SELECT * FROM [Users] WHERE email = 'customer3@gmail.com')
    INSERT INTO [Users] (user_id, email, password_hash, full_name, phone_number, role_id, status, is_email_verified, created_at)
    SELECT NEWID(), 'customer3@gmail.com', '$2a$10$QzSf/hgRoo3mkItxEyUDs.xCgnn7uacO.FDHTEZnNDACHrXhq08SK', N'Nguyễn Văn An', '0987654321', role_id, 'ACTIVE', 1, DATEADD(DAY, -45, GETUTCDATE())
    FROM [Roles] WHERE role_name = 'CUSTOMER';

IF NOT EXISTS (SELECT * FROM [Users] WHERE email = 'customer4@gmail.com')
    INSERT INTO [Users] (user_id, email, password_hash, full_name, phone_number, role_id, status, is_email_verified, created_at)
    SELECT NEWID(), 'customer4@gmail.com', '$2a$10$QzSf/hgRoo3mkItxEyUDs.xCgnn7uacO.FDHTEZnNDACHrXhq08SK', N'Trần Thị Bích', '0976543210', role_id, 'ACTIVE', 1, DATEADD(DAY, -30, GETUTCDATE())
    FROM [Roles] WHERE role_name = 'CUSTOMER';

IF NOT EXISTS (SELECT * FROM [Users] WHERE email = 'customer5@gmail.com')
    INSERT INTO [Users] (user_id, email, password_hash, full_name, phone_number, role_id, status, is_email_verified, created_at)
    SELECT NEWID(), 'customer5@gmail.com', '$2a$10$QzSf/hgRoo3mkItxEyUDs.xCgnn7uacO.FDHTEZnNDACHrXhq08SK', N'Lê Văn Cường', '0965432109', role_id, 'INACTIVE', 0, DATEADD(DAY, -60, GETUTCDATE())
    FROM [Roles] WHERE role_name = 'CUSTOMER';

-- Customer 3 address
IF NOT EXISTS (SELECT * FROM [Addresses] a JOIN [Users] u ON a.user_id = u.user_id WHERE u.email = 'customer3@gmail.com' AND a.name = N'Nguyễn Văn An')
    INSERT INTO [Addresses] (address_id, user_id, name, phone, street, city, is_default)
    SELECT NEWID(), user_id, N'Nguyễn Văn An', '0987654321', N'88 Cầu Giấy, Phường Dịch Vọng, Quận Cầu Giấy', N'Hà Nội', 1
    FROM [Users] WHERE email = 'customer3@gmail.com';

-- Customer 4 address
IF NOT EXISTS (SELECT * FROM [Addresses] a JOIN [Users] u ON a.user_id = u.user_id WHERE u.email = 'customer4@gmail.com' AND a.name = N'Trần Thị Bích')
    INSERT INTO [Addresses] (address_id, user_id, name, phone, street, city, is_default)
    SELECT NEWID(), user_id, N'Trần Thị Bích', '0976543210', N'25 Hai Bà Trưng, Phường Bến Nghé, Quận 1', N'TP. Hồ Chí Minh', 1
    FROM [Users] WHERE email = 'customer4@gmail.com';

-- Customer 5 address
IF NOT EXISTS (SELECT * FROM [Addresses] a JOIN [Users] u ON a.user_id = u.user_id WHERE u.email = 'customer5@gmail.com' AND a.name = N'Lê Văn Cường')
    INSERT INTO [Addresses] (address_id, user_id, name, phone, street, city, is_default)
    SELECT NEWID(), user_id, N'Lê Văn Cường', '0965432109', N'102 Nguyễn Huệ, Phường Bến Nghé, Quận 1', N'TP. Hồ Chí Minh', 1
    FROM [Users] WHERE email = 'customer5@gmail.com';

-- =============================================
-- 19. ORDERS
-- =============================================
-- Order 1: Customer1 - DELIVERED (Ray-Ban Aviator Gold + Essilor lens)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250101')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250101', 'DELIVERED', 6000000, 0, 'VN123456789', DATEADD(DAY, -30, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer1@gmail.com';

-- Order 2: Customer1 - DELIVERED (Oakley + accessory)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250102')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250102', 'DELIVERED', 3050000, 0, 'VN123456790', DATEADD(DAY, -25, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer1@gmail.com';

-- Order 3: Customer1 - SHIPPED (Gucci frame)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250103')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250103', 'SHIPPED', 7500000, 0, 'VN123456791', DATEADD(DAY, -5, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer1@gmail.com';

-- Order 4: Customer2 - DELIVERED (Hoya lens + accessories)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250104')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250104', 'DELIVERED', 2100000, 0, 'VN123456792', DATEADD(DAY, -20, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer2@gmail.com';

-- Order 5: Customer2 - CONFIRMED (Ray-Ban Aviator Silver)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250105')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250105', 'CONFIRMED', 3500000, 0, DATEADD(DAY, -2, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer2@gmail.com';

-- Order 6: Customer3 - DELIVERED (Zeiss lens + Oakley frame)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250106')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250106', 'DELIVERED', 7300000, 0, 'VN123456793', DATEADD(DAY, -15, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer3@gmail.com';

-- Order 7: Customer3 - PENDING (Nikon lens)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250107')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250107', 'PENDING', 2900000, 0, DATEADD(HOUR, -6, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer3@gmail.com';

-- Order 8: Customer4 - DELIVERED (Ray-Ban + Essilor + accessory)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250108')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250108', 'DELIVERED', 6550000, 0, 'VN123456794', DATEADD(DAY, -18, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer4@gmail.com';

-- Order 9: Customer4 - CANCELLED (Rodenstock lens)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250109')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250109', 'CANCELLED', 5500000, 0, DATEADD(DAY, -10, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer4@gmail.com';

-- Order 10: Customer1 - RETURN_REQUESTED (Oakley Holbrook - tortoise)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250110')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250110', 'RETURN_REQUESTED', 2900000, 0, 'VN123456795', DATEADD(DAY, -8, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer1@gmail.com';

-- =============================================
-- 20. ORDER ITEMS (each row = 1 unit, no quantity column)
-- =============================================
-- ORD-20250101: Ray-Ban Aviator Gold (3,500,000) + Essilor 1.56 (2,500,000)
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250101' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RB-AV-GOLD'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 3500000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250101' AND v.sku = 'RB-AV-GOLD';

IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250101' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'ESS-CS-1.56'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 2500000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250101' AND v.sku = 'ESS-CS-1.56';

-- ORD-20250102: Oakley Holbrook Black (2,800,000) + Hộp kính (250,000)
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250102' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'OAK-HB-BLACK'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 2800000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250102' AND v.sku = 'OAK-HB-BLACK';

-- ORD-20250103: Gucci GG0061S (7,500,000)
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250103' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'GG-0061-BLACK'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 7500000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250103' AND v.sku = 'GG-0061-BLACK';

-- ORD-20250104: Hoya Blue Control 1.56 (1,800,000) + Khăn lau (50,000) + Dung dịch (120,000)
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250104' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'HY-BC-1.56'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 1800000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250104' AND v.sku = 'HY-BC-1.56';

-- ORD-20250105: Ray-Ban Aviator Silver (3,500,000)
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250105' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RB-AV-SILVER'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 3500000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250105' AND v.sku = 'RB-AV-SILVER';

-- ORD-20250106: Zeiss Digital Lens 1.6 (4,500,000) + Oakley Holbrook Black (2,800,000)
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250106' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'ZS-DL-1.6'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 4500000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250106' AND v.sku = 'ZS-DL-1.6';

IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250106' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'OAK-HB-BLACK'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 2800000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250106' AND v.sku = 'OAK-HB-BLACK';

-- ORD-20250107: Nikon Lite AS 1.6 (2,900,000)
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250107' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'NK-LA-1.6'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 2900000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250107' AND v.sku = 'NK-LA-1.6';

-- ORD-20250108: Ray-Ban Aviator Black (3,700,000) + Essilor 1.6 (3,200,000) combo => sale off a bit = 6,550,000 total
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250108' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RB-AV-BLACK'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 3700000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250108' AND v.sku = 'RB-AV-BLACK';

IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250108' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'ESS-CS-1.6'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 2850000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250108' AND v.sku = 'ESS-CS-1.6';

-- ORD-20250109: Rodenstock Multigressiv 1.6 (5,500,000)
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250109' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RD-MG-1.6'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 5500000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250109' AND v.sku = 'RD-MG-1.6';

-- ORD-20250110: Oakley Holbrook Tortoise (2,900,000)
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250110' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'OAK-HB-TORT'))
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 2900000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250110' AND v.sku = 'OAK-HB-TORT';

-- =============================================
-- 21. PAYMENTS
-- =============================================
-- ORD-20250101: COD - COMPLETED
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250101')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 6000000, 'COMPLETED' FROM [Orders] WHERE code = 'ORD-20250101';

-- ORD-20250102: BANK_TRANSFER - COMPLETED
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250102')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'BANK_TRANSFER', 3050000, 'COMPLETED' FROM [Orders] WHERE code = 'ORD-20250102';

-- ORD-20250103: PAYOS - COMPLETED
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250103')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 7500000, 'COMPLETED', 'PAYOS-20250103-001' FROM [Orders] WHERE code = 'ORD-20250103';

-- ORD-20250104: COD - COMPLETED
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250104')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 2100000, 'COMPLETED' FROM [Orders] WHERE code = 'ORD-20250104';

-- ORD-20250105: PAYOS - COMPLETED
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250105')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 3500000, 'COMPLETED', 'PAYOS-20250105-001' FROM [Orders] WHERE code = 'ORD-20250105';

-- ORD-20250106: BANK_TRANSFER - COMPLETED
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250106')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'BANK_TRANSFER', 7300000, 'COMPLETED' FROM [Orders] WHERE code = 'ORD-20250106';

-- ORD-20250107: COD - PENDING (order is still pending)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250107')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 2900000, 'PENDING' FROM [Orders] WHERE code = 'ORD-20250107';

-- ORD-20250108: PAYOS - COMPLETED
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250108')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 6550000, 'COMPLETED', 'PAYOS-20250108-001' FROM [Orders] WHERE code = 'ORD-20250108';

-- ORD-20250109: PAYOS - REFUNDED (cancelled order)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250109')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 5500000, 'REFUNDED', 'PAYOS-20250109-001' FROM [Orders] WHERE code = 'ORD-20250109';

-- ORD-20250110: COD - COMPLETED (return requested after delivery)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250110')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 2900000, 'COMPLETED' FROM [Orders] WHERE code = 'ORD-20250110';

-- =============================================
-- 22. REFUNDS
-- =============================================
-- Refund 1: ORD-20250110 - PENDING return request (Oakley Holbrook bị trầy kính)
IF NOT EXISTS (SELECT * FROM [Refunds] r JOIN [Orders] o ON r.order_id = o.order_id WHERE o.code = 'ORD-20250110' AND r.status = 'PENDING')
    INSERT INTO [Refunds] (refund_id, order_id, amount, reason, status, created_at)
    SELECT NEWID(), order_id, 2900000, N'Sản phẩm bị trầy xước mặt kính khi nhận hàng. Yêu cầu đổi trả.', 'PENDING', DATEADD(DAY, -3, GETUTCDATE())
    FROM [Orders] WHERE code = 'ORD-20250110';

-- Refund 2: ORD-20250109 - COMPLETED refund (customer cancelled, money returned)
IF NOT EXISTS (SELECT * FROM [Refunds] r JOIN [Orders] o ON r.order_id = o.order_id WHERE o.code = 'ORD-20250109' AND r.status = 'COMPLETED')
    INSERT INTO [Refunds] (refund_id, order_id, amount, reason, status, created_at)
    SELECT NEWID(), order_id, 5500000, N'Khách hàng hủy đơn trước khi giao hàng. Hoàn tiền đầy đủ qua PayOS.', 'COMPLETED', DATEADD(DAY, -9, GETUTCDATE())
    FROM [Orders] WHERE code = 'ORD-20250109';

-- Refund 3: ORD-20250104 - APPROVED (partial refund for wrong lens specification)
IF NOT EXISTS (SELECT * FROM [Refunds] r JOIN [Orders] o ON r.order_id = o.order_id WHERE o.code = 'ORD-20250104' AND r.status = 'APPROVED')
    INSERT INTO [Refunds] (refund_id, order_id, amount, reason, status, created_at)
    SELECT NEWID(), order_id, 500000, N'Tròng kính giao không đúng độ theo đơn. Hoàn tiền chênh lệch 500,000 VNĐ.', 'APPROVED', DATEADD(DAY, -12, GETUTCDATE())
    FROM [Orders] WHERE code = 'ORD-20250104';

-- =============================================
-- 23. ORDER STATUS LOGS
-- =============================================
-- ORD-20250101 status history: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250101' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250101' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250101' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã giao cho đơn vị vận chuyển'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250101' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250101' AND osl.new_status = 'DELIVERED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'DELIVERED', N'Khách hàng đã nhận hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250101' AND u.email = 'operation@cclearly.com';

-- ORD-20250109 status history: PENDING -> CANCELLED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250109' AND osl.new_status = 'CANCELLED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CANCELLED', N'Khách yêu cầu hủy đơn'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250109' AND u.email = 'sales@cclearly.com';

-- ORD-20250110 status history: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED -> RETURN_REQUESTED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250110' AND osl.new_status = 'RETURN_REQUESTED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'RETURN_REQUESTED', N'Khách hàng yêu cầu trả hàng - sản phẩm bị trầy xước'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250110' AND u.email = 'customer1@gmail.com';

-- =============================================
-- 24. PRESCRIPTIONS FOR EXISTING ORDERS
-- =============================================
-- ORD-20250101 (DELIVERED): Ray-Ban Aviator Gold + Essilor 1.56 → prescription order
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250101')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status, sales_note)
    SELECT NEWID(), oi.order_item_id, -2.00, -0.50, 90, -2.25, -0.75, 85, 63.0, 'APPROVED', N'Thông số đã được xác nhận bởi nhân viên bán hàng'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250101' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RB-AV-GOLD');

-- ORD-20250108 (DELIVERED): Ray-Ban Aviator Black + Essilor 1.6 → prescription order
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250108')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, add_od, sph_os, cyl_os, axis_os, add_os, pd, validation_status, sales_note)
    SELECT NEWID(), oi.order_item_id, -3.50, -1.00, 175, 1.50, -3.25, -0.75, 10, 1.50, 64.5, 'APPROVED', N'Kính lão viễn thị - đã xác nhận thông số'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250108' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RB-AV-BLACK');

-- =============================================
-- 25. ADDITIONAL ORDERS (14 orders for Sales & Operations Staff flows)
-- =============================================

-- === PENDING ORDERS (4) - Sales Staff can confirm/cancel ===

-- ORD-20250201: Customer2 - PENDING - Prescription (Ray-Ban Black + Essilor 1.67)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250201')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250201', 'PENDING', 8200000, DATEADD(HOUR, -3, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer2@gmail.com';

-- ORD-20250202: Customer3 - PENDING - Standard (Oakley Black only)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250202')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250202', 'PENDING', 2800000, DATEADD(HOUR, -2, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer3@gmail.com';

-- ORD-20250203: Customer4 - PENDING - Prescription (Gucci + Zeiss 1.67)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250203')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250203', 'PENDING', 13500000, DATEADD(HOUR, -1, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer4@gmail.com';

-- ORD-20250204: Customer1 - PENDING - Standard (Ray-Ban Silver only)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250204')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250204', 'PENDING', 3500000, DATEADD(MINUTE, -30, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer1@gmail.com';

-- === CONFIRMED ORDERS (4) - Operations Staff can process ===

-- ORD-20250205: Customer1 - CONFIRMED - Prescription (Oakley Tort + Hoya 1.6) → Lens lab needed
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250205')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250205', 'CONFIRMED', 5400000, DATEADD(DAY, -1, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer1@gmail.com';

-- ORD-20250206: Customer4 - CONFIRMED - Prescription (Ray-Ban Gold + Nikon 1.6) → Lens lab needed
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250206')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250206', 'CONFIRMED', 6400000, DATEADD(HOUR, -18, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer4@gmail.com';

-- ORD-20250207: Customer2 - CONFIRMED - Standard (Ray-Ban Silver) → Straight to packing
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250207')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250207', 'CONFIRMED', 3500000, DATEADD(HOUR, -12, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer2@gmail.com';

-- ORD-20250208: Customer3 - CONFIRMED - Standard (Oakley Black) → Straight to packing
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250208')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250208', 'CONFIRMED', 2800000, DATEADD(HOUR, -8, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer3@gmail.com';

-- === PROCESSING ORDERS (2) - Operations: QC pass/fail in lens lab ===

-- ORD-20250209: Customer1 - PROCESSING - Prescription (Ray-Ban Black + Essilor 1.74)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250209')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250209', 'PROCESSING', 10500000, DATEADD(DAY, -2, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer1@gmail.com';

-- ORD-20250210: Customer4 - PROCESSING - Prescription (Gucci + Rodenstock 1.67)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250210')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250210', 'PROCESSING', 14700000, DATEADD(DAY, -2, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer4@gmail.com';

-- === RETURN & RETURNED ORDERS (2) ===

-- ORD-20250211: Customer2 - RETURN_REQUESTED - Prescription (Ray-Ban Gold + Chemi 1.6)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250211')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250211', 'RETURN_REQUESTED', 5500000, 'VN202502001', DATEADD(DAY, -7, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer2@gmail.com';

-- ORD-20250212: Customer3 - RETURNED - Prescription (Oakley Tort + Hoya 1.67)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250212')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250212', 'RETURNED', 6400000, 'VN202502002', DATEADD(DAY, -14, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer3@gmail.com';

-- === SHIPPED ORDERS (2) - Operations: mark delivered ===

-- ORD-20250213: Customer2 - SHIPPED - Standard (Ray-Ban Gold)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250213')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250213', 'SHIPPED', 3500000, 'VN202502003', DATEADD(DAY, -3, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer2@gmail.com';

-- ORD-20250214: Customer3 - SHIPPED - Prescription (Oakley Black + Essilor 1.56)
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'ORD-20250214')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, tracking_number, created_at, address_id)
    SELECT NEWID(), u.user_id, 'ORD-20250214', 'SHIPPED', 5300000, 'VN202502004', DATEADD(DAY, -2, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer3@gmail.com';

-- =============================================
-- 26. ORDER ITEMS FOR NEW ORDERS
-- =============================================

-- ORD-20250201: Ray-Ban Black (frame) + Essilor 1.67 (lens) = 8,200,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250201')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, lens_variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, lv.variant_id, 8200000
    FROM [Orders] o, [Product_Variants] v, [Product_Variants] lv
    WHERE o.code = 'ORD-20250201' AND v.sku = 'RB-AV-BLACK' AND lv.sku = 'ESS-CS-1.67';

-- ORD-20250202: Oakley Black (frame only) = 2,800,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250202')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 2800000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250202' AND v.sku = 'OAK-HB-BLACK';

-- ORD-20250203: Gucci (frame) + Zeiss 1.67 (lens) = 13,500,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250203')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, lens_variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, lv.variant_id, 13500000
    FROM [Orders] o, [Product_Variants] v, [Product_Variants] lv
    WHERE o.code = 'ORD-20250203' AND v.sku = 'GG-0061-BLACK' AND lv.sku = 'ZS-DL-1.67';

-- ORD-20250204: Ray-Ban Silver (frame only) = 3,500,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250204')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 3500000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250204' AND v.sku = 'RB-AV-SILVER';

-- ORD-20250205: Oakley Tort (frame) + Hoya 1.6 (lens) = 5,400,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250205')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, lens_variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, lv.variant_id, 5400000
    FROM [Orders] o, [Product_Variants] v, [Product_Variants] lv
    WHERE o.code = 'ORD-20250205' AND v.sku = 'OAK-HB-TORT' AND lv.sku = 'HY-BC-1.6';

-- ORD-20250206: Ray-Ban Gold (frame) + Nikon 1.6 (lens) = 6,400,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250206')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, lens_variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, lv.variant_id, 6400000
    FROM [Orders] o, [Product_Variants] v, [Product_Variants] lv
    WHERE o.code = 'ORD-20250206' AND v.sku = 'RB-AV-GOLD' AND lv.sku = 'NK-LA-1.6';

-- ORD-20250207: Ray-Ban Silver (frame only) = 3,500,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250207')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 3500000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250207' AND v.sku = 'RB-AV-SILVER';

-- ORD-20250208: Oakley Black (frame only) = 2,800,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250208')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 2800000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250208' AND v.sku = 'OAK-HB-BLACK';

-- ORD-20250209: Ray-Ban Black (frame) + Essilor 1.74 (lens) = 10,500,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250209')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, lens_variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, lv.variant_id, 10500000
    FROM [Orders] o, [Product_Variants] v, [Product_Variants] lv
    WHERE o.code = 'ORD-20250209' AND v.sku = 'RB-AV-BLACK' AND lv.sku = 'ESS-CS-1.74';

-- ORD-20250210: Gucci (frame) + Rodenstock 1.67 (lens) = 14,700,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250210')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, lens_variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, lv.variant_id, 14700000
    FROM [Orders] o, [Product_Variants] v, [Product_Variants] lv
    WHERE o.code = 'ORD-20250210' AND v.sku = 'GG-0061-BLACK' AND lv.sku = 'RD-MG-1.67';

-- ORD-20250211: Ray-Ban Gold (frame) + Chemi 1.6 (lens) = 5,500,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250211')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, lens_variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, lv.variant_id, 5500000
    FROM [Orders] o, [Product_Variants] v, [Product_Variants] lv
    WHERE o.code = 'ORD-20250211' AND v.sku = 'RB-AV-GOLD' AND lv.sku = 'CH-CR-1.6';

-- ORD-20250212: Oakley Tort (frame) + Hoya 1.67 (lens) = 6,400,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250212')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, lens_variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, lv.variant_id, 6400000
    FROM [Orders] o, [Product_Variants] v, [Product_Variants] lv
    WHERE o.code = 'ORD-20250212' AND v.sku = 'OAK-HB-TORT' AND lv.sku = 'HY-BC-1.67';

-- ORD-20250213: Ray-Ban Gold (frame only) = 3,500,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250213')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, 3500000
    FROM [Orders] o, [Product_Variants] v WHERE o.code = 'ORD-20250213' AND v.sku = 'RB-AV-GOLD';

-- ORD-20250214: Oakley Black (frame) + Essilor 1.56 (lens) = 5,300,000
IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250214')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, lens_variant_id, unit_price)
    SELECT NEWID(), o.order_id, v.variant_id, lv.variant_id, 5300000
    FROM [Orders] o, [Product_Variants] v, [Product_Variants] lv
    WHERE o.code = 'ORD-20250214' AND v.sku = 'OAK-HB-BLACK' AND lv.sku = 'ESS-CS-1.56';

-- =============================================
-- 27. PRESCRIPTIONS FOR NEW ORDERS
-- =============================================

-- ORD-20250201: Cận thị nhẹ hai mắt
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250201')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status)
    SELECT NEWID(), oi.order_item_id, -2.50, -0.75, 90, -3.00, -0.50, 85, 63.0, 'PENDING'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250201' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RB-AV-BLACK');

-- ORD-20250203: Cận thị + loạn thị
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250203')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status)
    SELECT NEWID(), oi.order_item_id, -1.75, -1.25, 170, -2.00, -1.00, 10, 65.5, 'PENDING'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250203' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'GG-0061-BLACK');

-- ORD-20250205: Cận thị nặng
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250205')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status, sales_note)
    SELECT NEWID(), oi.order_item_id, -4.00, -0.50, 180, -3.75, -0.75, 175, 62.0, 'PENDING', N'Khách hàng gửi đơn kính từ bệnh viện mắt'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250205' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'OAK-HB-TORT');

-- ORD-20250206: Cận thị nhẹ - đã xác nhận
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250206')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status, sales_note)
    SELECT NEWID(), oi.order_item_id, -1.50, -0.25, 5, -1.25, -0.50, 170, 64.0, 'APPROVED', N'Thông số đã xác nhận - bắt đầu gia công'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250206' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RB-AV-GOLD');

-- ORD-20250209: Cận thị nặng + loạn - đang gia công
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250209')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status, sales_note)
    SELECT NEWID(), oi.order_item_id, -5.50, -1.50, 45, -5.25, -1.75, 140, 66.0, 'APPROVED', N'Kính cận nặng - cần tròng mỏng 1.74. Đã xác nhận thông số.'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250209' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RB-AV-BLACK');

-- ORD-20250210: Cận thị trung bình + loạn - đang gia công
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250210')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status, sales_note)
    SELECT NEWID(), oi.order_item_id, -3.25, -0.75, 120, -3.00, -1.00, 60, 63.5, 'APPROVED', N'Đơn hàng cao cấp Gucci + Rodenstock. Gia công cẩn thận.'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250210' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'GG-0061-BLACK');

-- ORD-20250211: Cận thị - đã giao nhưng sai thông số
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250211')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status, sales_note)
    SELECT NEWID(), oi.order_item_id, -2.00, -0.50, 95, -2.25, -0.25, 80, 62.5, 'APPROVED', N'Khách phản hồi tròng không đúng độ - cần kiểm tra lại'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250211' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'RB-AV-GOLD');

-- ORD-20250212: Đã trả hàng - lỗi gia công
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250212')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status, sales_note)
    SELECT NEWID(), oi.order_item_id, -1.00, -0.75, 55, -0.75, -0.50, 125, 61.0, 'APPROVED', N'Sản phẩm bị lỗi gia công - đã hoàn tiền cho khách'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250212' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'OAK-HB-TORT');

-- ORD-20250214: Cận thị nhẹ - đang giao
IF NOT EXISTS (SELECT * FROM [Prescriptions] p JOIN [Order_Items] oi ON p.order_item_id = oi.order_item_id
               JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'ORD-20250214')
    INSERT INTO [Prescriptions] (prescription_id, order_item_id, sph_od, cyl_od, axis_od, sph_os, cyl_os, axis_os, pd, validation_status, sales_note)
    SELECT NEWID(), oi.order_item_id, -0.50, -0.25, 180, -0.75, -0.25, 0, 60.0, 'APPROVED', N'Cận nhẹ - gia công hoàn tất, đã giao vận chuyển'
    FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id
    WHERE o.code = 'ORD-20250214' AND oi.variant_id = (SELECT variant_id FROM [Product_Variants] WHERE sku = 'OAK-HB-BLACK');

-- =============================================
-- 28. PAYMENTS FOR NEW ORDERS
-- =============================================

-- ORD-20250201: COD - PENDING (order still PENDING)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250201')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 8200000, 'PENDING' FROM [Orders] WHERE code = 'ORD-20250201';

-- ORD-20250202: BANK_TRANSFER - PENDING
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250202')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'BANK_TRANSFER', 2800000, 'PENDING' FROM [Orders] WHERE code = 'ORD-20250202';

-- ORD-20250203: PAYOS - COMPLETED (prepaid)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250203')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 13500000, 'COMPLETED', 'PAYOS-20250203-001' FROM [Orders] WHERE code = 'ORD-20250203';

-- ORD-20250204: COD - PENDING
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250204')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 3500000, 'PENDING' FROM [Orders] WHERE code = 'ORD-20250204';

-- ORD-20250205: PAYOS - COMPLETED (prepaid)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250205')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 5400000, 'COMPLETED', 'PAYOS-20250205-001' FROM [Orders] WHERE code = 'ORD-20250205';

-- ORD-20250206: BANK_TRANSFER - COMPLETED
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250206')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'BANK_TRANSFER', 6400000, 'COMPLETED' FROM [Orders] WHERE code = 'ORD-20250206';

-- ORD-20250207: COD - PENDING (will pay on delivery)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250207')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 3500000, 'PENDING' FROM [Orders] WHERE code = 'ORD-20250207';

-- ORD-20250208: PAYOS - COMPLETED (prepaid)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250208')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 2800000, 'COMPLETED', 'PAYOS-20250208-001' FROM [Orders] WHERE code = 'ORD-20250208';

-- ORD-20250209: COD - PENDING (will pay on delivery)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250209')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 10500000, 'PENDING' FROM [Orders] WHERE code = 'ORD-20250209';

-- ORD-20250210: BANK_TRANSFER - COMPLETED
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250210')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'BANK_TRANSFER', 14700000, 'COMPLETED' FROM [Orders] WHERE code = 'ORD-20250210';

-- ORD-20250211: COD - COMPLETED (was delivered then return requested)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250211')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 5500000, 'COMPLETED' FROM [Orders] WHERE code = 'ORD-20250211';

-- ORD-20250212: BANK_TRANSFER - REFUNDED (returned and refunded)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250212')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'BANK_TRANSFER', 6400000, 'REFUNDED' FROM [Orders] WHERE code = 'ORD-20250212';

-- ORD-20250213: PAYOS - COMPLETED (prepaid)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250213')
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 3500000, 'COMPLETED', 'PAYOS-20250213-001' FROM [Orders] WHERE code = 'ORD-20250213';

-- ORD-20250214: COD - COMPLETED (paid on delivery, now shipped)
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'ORD-20250214')
    INSERT INTO [Payments] (payment_id, order_id, method, status)
    SELECT NEWID(), order_id, 'COD', 'COMPLETED' FROM [Orders] WHERE code = 'ORD-20250214';

-- =============================================
-- 29. REFUNDS FOR NEW ORDERS
-- =============================================

-- ORD-20250211: PENDING refund - wrong lens specification
IF NOT EXISTS (SELECT * FROM [Refunds] r JOIN [Orders] o ON r.order_id = o.order_id WHERE o.code = 'ORD-20250211')
    INSERT INTO [Refunds] (refund_id, order_id, amount, reason, status, created_at)
    SELECT NEWID(), order_id, 5500000, N'Kính bị sai thông số tròng so với đơn kê. Khách yêu cầu hoàn tiền toàn bộ.', 'PENDING', DATEADD(DAY, -2, GETUTCDATE())
    FROM [Orders] WHERE code = 'ORD-20250211';

-- ORD-20250212: COMPLETED refund - lens manufacturing defect
IF NOT EXISTS (SELECT * FROM [Refunds] r JOIN [Orders] o ON r.order_id = o.order_id WHERE o.code = 'ORD-20250212')
    INSERT INTO [Refunds] (refund_id, order_id, amount, reason, status, created_at)
    SELECT NEWID(), order_id, 6400000, N'Sản phẩm bị lỗi gia công tròng kính. Đã xác nhận và hoàn tiền cho khách hàng.', 'COMPLETED', DATEADD(DAY, -10, GETUTCDATE())
    FROM [Orders] WHERE code = 'ORD-20250212';

-- =============================================
-- 30. ORDER STATUS LOGS FOR NEW ORDERS
-- =============================================

-- ORD-20250205: PENDING -> CONFIRMED (by sales)
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250205' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng kính thuốc - chuyển sang gia công'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250205' AND u.email = 'sales@cclearly.com';

-- ORD-20250206: PENDING -> CONFIRMED (by sales)
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250206' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng - thông số kính đã được duyệt'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250206' AND u.email = 'sales@cclearly.com';

-- ORD-20250207: PENDING -> CONFIRMED (by sales)
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250207' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn gọng kính - sẵn hàng trong kho'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250207' AND u.email = 'sales@cclearly.com';

-- ORD-20250208: PENDING -> CONFIRMED (by sales)
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250208' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn gọng kính'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250208' AND u.email = 'sales@cclearly.com';

-- ORD-20250209: PENDING -> CONFIRMED -> PROCESSING (by operation)
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250209' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng kính thuốc cao cấp'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250209' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250209' AND osl.new_status = 'PROCESSING')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'PROCESSING', N'Bắt đầu gia công tròng kính Essilor 1.74'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250209' AND u.email = 'operation@cclearly.com';

-- ORD-20250210: PENDING -> CONFIRMED -> PROCESSING (by operation)
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250210' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng Gucci + Rodenstock'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250210' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250210' AND osl.new_status = 'PROCESSING')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'PROCESSING', N'Bắt đầu gia công tròng kính Rodenstock 1.67'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250210' AND u.email = 'operation@cclearly.com';

-- ORD-20250211: Full flow: PENDING -> CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED -> RETURN_REQUESTED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250211' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250211' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250211' AND osl.new_status = 'PROCESSING')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'PROCESSING', N'Bắt đầu gia công tròng kính'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250211' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250211' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã giao cho đơn vị vận chuyển - VN202502001'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250211' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250211' AND osl.new_status = 'DELIVERED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'DELIVERED', N'Giao hàng thành công'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250211' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250211' AND osl.new_status = 'RETURN_REQUESTED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'RETURN_REQUESTED', N'Khách phản hồi tròng sai thông số - yêu cầu trả hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250211' AND u.email = 'customer2@gmail.com';

-- ORD-20250212: Full flow: PENDING -> CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED -> RETURN_REQUESTED -> RETURNED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250212' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng kính thuốc'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250212' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250212' AND osl.new_status = 'PROCESSING')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'PROCESSING', N'Gia công tròng kính Hoya 1.67'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250212' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250212' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã giao cho đơn vị vận chuyển - VN202502002'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250212' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250212' AND osl.new_status = 'DELIVERED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'DELIVERED', N'Giao hàng thành công'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250212' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250212' AND osl.new_status = 'RETURN_REQUESTED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'RETURN_REQUESTED', N'Khách phản hồi tròng kính bị lỗi gia công'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250212' AND u.email = 'customer3@gmail.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250212' AND osl.new_status = 'RETURNED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'RETURNED', N'Đã xác nhận trả hàng và hoàn tiền cho khách'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250212' AND u.email = 'sales@cclearly.com';

-- ORD-20250213: PENDING -> CONFIRMED -> SHIPPED (by operation)
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250213' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn gọng kính Ray-Ban'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250213' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250213' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã đóng gói và giao vận chuyển - VN202502003'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250213' AND u.email = 'operation@cclearly.com';

-- ORD-20250214: PENDING -> CONFIRMED -> PROCESSING -> SHIPPED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250214' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng kính thuốc'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250214' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250214' AND osl.new_status = 'PROCESSING')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'PROCESSING', N'Bắt đầu gia công tròng kính Essilor 1.56'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250214' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250214' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Gia công hoàn tất - đã đóng gói và giao vận chuyển - VN202502004'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250214' AND u.email = 'operation@cclearly.com';

-- =============================================
-- 31. MISSING STATUS LOGS FOR EXISTING ORDERS
-- =============================================

-- ORD-20250102: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250102' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250102' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250102' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã giao cho đơn vị vận chuyển'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250102' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250102' AND osl.new_status = 'DELIVERED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'DELIVERED', N'Khách hàng đã nhận hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250102' AND u.email = 'operation@cclearly.com';

-- ORD-20250103: PENDING -> CONFIRMED -> SHIPPED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250103' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng Gucci'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250103' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250103' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã đóng gói và giao vận chuyển'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250103' AND u.email = 'operation@cclearly.com';

-- ORD-20250104: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250104' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250104' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250104' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã giao cho đơn vị vận chuyển'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250104' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250104' AND osl.new_status = 'DELIVERED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'DELIVERED', N'Khách hàng đã nhận hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250104' AND u.email = 'operation@cclearly.com';

-- ORD-20250105: PENDING -> CONFIRMED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250105' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng Ray-Ban Silver'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250105' AND u.email = 'sales@cclearly.com';

-- ORD-20250106: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250106' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng Zeiss + Oakley'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250106' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250106' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã giao cho đơn vị vận chuyển'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250106' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250106' AND osl.new_status = 'DELIVERED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'DELIVERED', N'Khách hàng đã nhận hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250106' AND u.email = 'operation@cclearly.com';

-- ORD-20250108: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250108' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng kính thuốc'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250108' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250108' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã đóng gói và giao vận chuyển'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250108' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250108' AND osl.new_status = 'DELIVERED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'DELIVERED', N'Khách hàng đã nhận hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250108' AND u.email = 'operation@cclearly.com';

-- ORD-20250110: Full flow PENDING -> CONFIRMED -> SHIPPED -> DELIVERED -> RETURN_REQUESTED
IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250110' AND osl.new_status = 'CONFIRMED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'CONFIRMED', N'Xác nhận đơn hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250110' AND u.email = 'sales@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250110' AND osl.new_status = 'SHIPPED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'SHIPPED', N'Đã giao cho đơn vị vận chuyển'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250110' AND u.email = 'operation@cclearly.com';

IF NOT EXISTS (SELECT * FROM [Order_Status_Logs] osl JOIN [Orders] o ON osl.order_id = o.order_id WHERE o.code = 'ORD-20250110' AND osl.new_status = 'DELIVERED')
    INSERT INTO [Order_Status_Logs] (log_id, order_id, user_id, new_status, note)
    SELECT NEWID(), o.order_id, u.user_id, 'DELIVERED', N'Khách hàng đã nhận hàng'
    FROM [Orders] o, [Users] u WHERE o.code = 'ORD-20250110' AND u.email = 'operation@cclearly.com';

-- =============================================
-- 25. PRE-ORDER SETUP
-- =============================================
-- Mark some variants as preorder
UPDATE [Product_Variants] SET is_preorder = 1, expected_availability = DATEADD(DAY, 3, CAST(GETUTCDATE() AS DATE))
WHERE sku = 'GG-0061-BLACK';

UPDATE [Product_Variants] SET is_preorder = 1, expected_availability = DATEADD(DAY, -2, CAST(GETUTCDATE() AS DATE))
WHERE sku = 'OAK-HB-TORT';

-- =============================================
-- 26. PRE-ORDER ORDERS
-- =============================================
-- Preorder 1: Customer2 - CONFIRMED (waiting for Gucci GG0061S to arrive), deposit 50% = 3,750,000₫
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'PRE-20250201')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, is_preorder, preorder_deadline, payment_type, created_at, address_id)
    SELECT NEWID(), u.user_id, 'PRE-20250201', 'CONFIRMED', 7500000, 0, 1, DATEADD(DAY, 3, CAST(GETUTCDATE() AS DATE)), 'DEPOSIT',
           DATEADD(DAY, -4, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer2@gmail.com';

IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'PRE-20250201')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, pv.variant_id, 7500000
    FROM [Orders] o, [Product_Variants] pv WHERE o.code = 'PRE-20250201' AND pv.sku = 'GG-0061-BLACK';

-- Preorder 2: Customer3 - PENDING (Oakley Holbrook Tortoise), overdue, deposit 50% = 1,450,000₫
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'PRE-20250202')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, is_preorder, preorder_deadline, payment_type, created_at, address_id)
    SELECT NEWID(), u.user_id, 'PRE-20250202', 'PENDING', 2900000, 0, 1, DATEADD(DAY, -2, CAST(GETUTCDATE() AS DATE)), 'DEPOSIT',
           DATEADD(DAY, -9, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer3@gmail.com';

IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'PRE-20250202')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, pv.variant_id, 2900000
    FROM [Orders] o, [Product_Variants] pv WHERE o.code = 'PRE-20250202' AND pv.sku = 'OAK-HB-TORT';

-- Preorder 3: Customer4 - CONFIRMED (Gucci), deposit 50% = 3,750,000₫
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'PRE-20250203')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, is_preorder, preorder_deadline, payment_type, created_at, address_id)
    SELECT NEWID(), u.user_id, 'PRE-20250203', 'CONFIRMED', 7500000, 0, 1, DATEADD(DAY, 5, CAST(GETUTCDATE() AS DATE)), 'DEPOSIT',
           DATEADD(DAY, -2, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer4@gmail.com';

IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'PRE-20250203')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, pv.variant_id, 7500000
    FROM [Orders] o, [Product_Variants] pv WHERE o.code = 'PRE-20250203' AND pv.sku = 'GG-0061-BLACK';

-- Preorder 4: Customer5 - PROCESSING (already received stock, being processed), deposit 50% = 1,450,000₫
IF NOT EXISTS (SELECT * FROM [Orders] WHERE code = 'PRE-20250204')
    INSERT INTO [Orders] (order_id, user_id, code, status, final_amount, shipping_fee, is_preorder, preorder_deadline, payment_type, created_at, address_id)
    SELECT NEWID(), u.user_id, 'PRE-20250204', 'PROCESSING', 2900000, 0, 1, DATEADD(DAY, -1, CAST(GETUTCDATE() AS DATE)), 'DEPOSIT',
           DATEADD(DAY, -8, GETUTCDATE()),
           (SELECT TOP 1 address_id FROM [Addresses] WHERE user_id = u.user_id AND is_default = 1)
    FROM [Users] u WHERE u.email = 'customer5@gmail.com';

IF NOT EXISTS (SELECT * FROM [Order_Items] oi JOIN [Orders] o ON oi.order_id = o.order_id WHERE o.code = 'PRE-20250204')
    INSERT INTO [Order_Items] (order_item_id, order_id, variant_id, unit_price)
    SELECT NEWID(), o.order_id, pv.variant_id, 2900000
    FROM [Orders] o, [Product_Variants] pv WHERE o.code = 'PRE-20250204' AND pv.sku = 'OAK-HB-TORT';

-- =============================================
-- 27. PRE-ORDER PAYMENTS (split: PAYOS deposit 50% + COD remainder)
-- =============================================
-- PRE-20250201: PAYOS deposit 3,750,000₫ (50% of 7,500,000₫) + COD 3,750,000₫
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'PRE-20250201')
BEGIN
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 3750000, 'COMPLETED', 'PAYOS-PRE20250201' FROM [Orders] WHERE code = 'PRE-20250201';
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 3750000, 'PENDING' FROM [Orders] WHERE code = 'PRE-20250201';
END

-- PRE-20250202: PAYOS deposit 1,450,000₫ (50% of 2,900,000₫) + COD 1,450,000₫
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'PRE-20250202')
BEGIN
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 1450000, 'COMPLETED', 'PAYOS-PRE20250202' FROM [Orders] WHERE code = 'PRE-20250202';
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 1450000, 'PENDING' FROM [Orders] WHERE code = 'PRE-20250202';
END

-- PRE-20250203: PAYOS deposit 3,750,000₫ (50% of 7,500,000₫) + COD 3,750,000₫
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'PRE-20250203')
BEGIN
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 3750000, 'COMPLETED', 'PAYOS-PRE20250203' FROM [Orders] WHERE code = 'PRE-20250203';
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 3750000, 'PENDING' FROM [Orders] WHERE code = 'PRE-20250203';
END

-- PRE-20250204: PAYOS deposit 1,450,000₫ (50% of 2,900,000₫) + COD 1,450,000₫
IF NOT EXISTS (SELECT * FROM [Payments] p JOIN [Orders] o ON p.order_id = o.order_id WHERE o.code = 'PRE-20250204')
BEGIN
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status, payos_order_code)
    SELECT NEWID(), order_id, 'PAYOS', 1450000, 'COMPLETED', 'PAYOS-PRE20250204' FROM [Orders] WHERE code = 'PRE-20250204';
    INSERT INTO [Payments] (payment_id, order_id, method, amount, status)
    SELECT NEWID(), order_id, 'COD', 1450000, 'PENDING' FROM [Orders] WHERE code = 'PRE-20250204';
END