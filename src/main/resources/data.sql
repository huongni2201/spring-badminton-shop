-- ======= PERMISSIONS =======
INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, updated_by)
VALUES ('View Products', '/api/v1/products', 'GET', 'Product', NOW(), NOW(), 'system', 'system'),
       ('Create Product', '/api/v1/products', 'POST', 'Product', NOW(), NOW(), 'system', 'system'),
       ('View Orders', '/api/v1/orders', 'GET', 'Order', NOW(), NOW(), 'system', 'system'),
       ('Manage Users', '/api/v1/users', 'GET', 'User', NOW(), NOW(), 'system', 'system'),
       ('Access Dashboard', '/api/v1/dashboard', 'GET', 'Dashboard', NOW(), NOW(), 'system', 'system');

-- ======= ROLES =======
INSERT INTO roles (name, description, active, created_at, updated_at, created_by, updated_by)
VALUES ('ADMIN', 'Quản trị hệ thống', true, NOW(), NOW(), 'system', 'system'),
       ('STAFF', 'Nhân viên bán hàng', true, NOW(), NOW(), 'system', 'system'),
       ('CUSTOMER', 'Khách hàng', true, NOW(), NOW(), 'system', 'system');

UPDATE roles SET active = true;

-- ======= PERMISSION_ROLE MAPPING =======
-- ADMIN: full access
INSERT INTO permission_role (role_id, permission_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5);

-- STAFF: view products, create product, view orders
INSERT INTO permission_role (role_id, permission_id)
VALUES (2, 1),
       (2, 2),
       (2, 3);

-- CUSTOMER: only view products
INSERT INTO permission_role (role_id, permission_id)
VALUES (3, 1);


-- ======= BRANDS =======
INSERT INTO brands (name, description, logo_url)
VALUES ('Yonex', 'Thương hiệu cầu lông hàng đầu Nhật Bản, nổi tiếng với vợt chất lượng cao.',
        'https://example.com/logos/yonex.png'),
       ('Li-Ning', 'Thương hiệu thể thao Trung Quốc với các sản phẩm cầu lông mạnh mẽ và hiện đại.',
        'https://example.com/logos/lining.png'),
       ('Victor', 'Thương hiệu Đài Loan chuyên dụng về thiết bị cầu lông.', 'https://example.com/logos/victor.png'),
       ('Apacs', 'Thương hiệu giá rẻ đến từ Malaysia, phù hợp với người mới chơi.',
        'https://example.com/logos/apacs.png'),
       ('Mizuno', 'Thương hiệu thể thao Nhật Bản với thiết kế đẹp và chất lượng tốt.',
        'https://example.com/logos/mizuno.png');

-- ======= CATEGORIES =======
INSERT INTO categories (name, description, created_at, updated_at)
VALUES ('Vợt cầu lông', 'Các loại vợt cho người chơi từ cơ bản đến chuyên nghiệp', NOW(), NOW()),
       ('Giày cầu lông', 'Giày thể thao chuyên dụng chống trượt và hỗ trợ di chuyển trên sân cầu lông', NOW(),
        NOW()),
       ('Quần cầu lông', 'Quần thể thao chuyên dùng cho thi đấu và luyện tập cầu lông', NOW(), NOW()),
       ('Áo cầu lông', 'Áo thể thao giúp thoáng mát khi chơi cầu lông', NOW(), NOW()),
       ('Váy cầu lông', 'Váy thi đấu cầu lông dành cho nữ, thoải mái và thẩm mỹ', NOW(), NOW()),
       ('Balo cầu lông', 'Balo thể thao chuyên dụng đựng vợt, giày và dụng cụ thi đấu', NOW(), NOW()),
       ('Túi cầu lông', 'Túi đựng vợt cầu lông chuyên dụng, có khả năng cách nhiệt', NOW(), NOW()),
       ('Phụ kiện cầu lông', 'Các phụ kiện như băng tay, quấn cán, ống cầu, grip, v.v.', NOW(), NOW());


-- ======= PRODUCT VARIANT ATTRIBUTES =======
INSERT INTO product_variant_attributes (name, slug)
VALUES ('color', 'color'),
       ('size', 'size'),
       ('weight', 'weight'),

