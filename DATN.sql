--DROP DATABASE DATNV01

CREATE DATABASE DATNV01

USE DATNV01
GO

CREATE TABLE brand (
    id INT IDENTITY(1,1) NOT NULL,
    brand_name NVARCHAR(100) NOT NULL,
    [status] BIT NOT NULL 
);

CREATE TABLE category (
    id INT IDENTITY(1,1) NOT NULL,
    category_name NVARCHAR(100) NOT NULL,
    [status] BIT NOT NULL 
);

CREATE TABLE material (
    id INT IDENTITY(1,1) NOT NULL,
    material_name NVARCHAR(100) NOT NULL,
    [status] BIT NOT NULL 
);

-- DROP TABLE product

CREATE TABLE [product] (
    id INT IDENTITY(1,1) NOT NULL,
    brand_id INT NOT NULL,
    category_id INT NOT NULL,
	material_id INT NOT NULL,
	product_code VARCHAR(10) NOT NULL,
    product_name NVARCHAR(255) NOT NULL,
    [status] BIT NOT NULL 
);

CREATE TABLE promotion (
    id INT IDENTITY(1,1) NOT NULL,
    promotion_name NVARCHAR(255) NOT NULL,
    promotion_percent INT NOT NULL,
    [start_date] DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    [description] NVARCHAR(500) NOT NULL,
    [status] BIT NOT NULL
);

CREATE TABLE color (
    id INT IDENTITY(1,1) NOT NULL,
    color_name NVARCHAR(100) NOT NULL,
    [status] BIT NOT NULL 
);

CREATE TABLE size (
    id INT IDENTITY(1,1) NOT NULL,
    size_name NVARCHAR(100) NOT NULL,
    [status] BIT NOT NULL 
);

CREATE TABLE collar(
	id INT IDENTITY(1,1) NOT NULL,
	collar_name NVARCHAR(100) NOT NULL,
    [status] BIT NOT NULL 
);

CREATE TABLE sleeve(
	id INT IDENTITY(1,1) NOT NULL,
	sleeve_name NVARCHAR(100) NOT NULL,
    [status] BIT NOT NULL 
)

-- DROP TABLE product_detail

CREATE TABLE product_detail (
    id INT IDENTITY(1,1) NOT NULL,
    product_id INT NOT NULL,
    size_id INT NOT NULL ,
    color_id INT NOT NULL,
    promotion_id INT NOT NULL,
	collar_id INT,
	sleeve_id INT,
	photo VARCHAR(250) NOT NULL,
    product_detail_code VARCHAR(50) NOT NULL,
    import_price DECIMAL(18, 2) NOT NULL,
    sale_price DECIMAL(18, 2) NOT NULL,
	quantity INT NOT NULL,
    [description] NVARCHAR(500) NOT NULL,
    [status] BIT NOT NULL 	
);

--ALTER TABLE product_detail
--ADD photo VARCHAR(250) NOT NULL;

CREATE TABLE voucher (
    id INT IDENTITY(1,1) NOT NULL,
    voucher_code VARCHAR(50) NOT NULL ,
	voucher_name NVARCHAR(250) NOT NULL,
    [description] NVARCHAR(255),
    min_condition DECIMAL(18, 2) NOT NULL,
    max_discount DECIMAL(18, 2) NOT NULL,
	reduced_percent FLOAT NOT NULL,
    [start_date] DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    [status] BIT NOT NULL
);

CREATE TABLE [role] (
    id INT IDENTITY(1,1) NOT NULL,
    [name] VARCHAR(100) NOT NULL
);

--DROP TABLE employee

CREATE TABLE employee (
    id INT IDENTITY(1,1) NOT NULL,
    employee_code VARCHAR(50) NOT NULL,
	role_id INT NOT NULL,
    fullname NVARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL,
    [password] VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
	photo VARCHAR(250) NOT NULL,
    [status] INT NOT NULL,
    create_date DATETIME NOT NULL,
	update_date DATETIME NOT NULL,
	[address] NVARCHAR(255) NOT NULL,
	forget_password BIT,
    gender BIT NOT NULL
);

--ALTER TABLE employee
--ALTER COLUMN gender BIT NOT NULL;

--DROP TABLE customer

CREATE TABLE customer (
    id INT IDENTITY(1,1) NOT NULL ,
    customer_code VARCHAR(50) NOT NULL,
    fullname NVARCHAR(255),
	username VARCHAR(100) NOT NULL,
    [password] VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    create_date DATETIME,
	update_date DATETIME,
	forget_password BIT,
    [status] BIT
);

CREATE TABLE [address] (
    id INT IDENTITY(1,1) NOT NULL,
    customer_id INT NOT NULL,
    province_id INT NOT NULL,
	province_name NVARCHAR(50) NOT NULL,
    district_id INT NOT NULL,
	district_name NVARCHAR(50) NOT NULL,
    ward_id INT NOT NULL,
	ward_name NVARCHAR(50) NOT NULL,
    address_detail NVARCHAR(255) NOT NULL,
);

CREATE TABLE [order] (
    id INT IDENTITY(1,1) NOT NULL,
    employee_id INT,
    voucher_id INT,
    customer_id INT NOT NULL,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    create_date DATETIME NOT NULL,
    total_amount INT NOT NULL,
    total_bill DECIMAL(18, 2) NOT NULL,
    payment_method INT NOT NULL,
    kind_of_order BIT NOT NULL CHECK (kind_of_order IN (0, 1)),  
                                     -- 0: Online
                                     -- 1: Offline
    status_order INT NOT NULL CHECK (status_order IN (-1, 0, 1, 2, 3, 4, 5))
                                      -- -1: Đã hủy
                                      --  0: Chờ xác nhận
                                      --  1: Chờ thanh toán
                                      --  2: Đã xác nhận
                                      --  3: Đang giao hàng
                                      --  4: Giao hàng không thành công
                                      --  5: Hoàn thành
);

CREATE TABLE order_detail (
    id INT IDENTITY(1,1) NOT NULL ,
    order_id INT NOT NULL,
    product_detail_id INT NOT NULL,
    quantity INT NOT NULL,
);


CREATE TABLE cart (
    id INT IDENTITY(1,1) NOT NULL,
    customer_id INT NOT NULL,
    product_detail_id INT NOT NULL,
    quantity INT NOT NULL
);

-- ** Chỉ mục (INDEX) tối ưu hiệu suất tìm kiếm

CREATE INDEX idx_product_code ON [product](product_code);
CREATE INDEX idx_order_code ON [order](order_code);
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_employee_username ON employee(username);


-- ** PHẦN PK, FK CHO CÁC TABLE CỦA DATABASE

ALTER TABLE promotion ADD CONSTRAINT PK_promotion PRIMARY KEY (id)
ALTER TABLE color ADD CONSTRAINT PK_color PRIMARY KEY (id)
ALTER TABLE size ADD CONSTRAINT PK_size PRIMARY KEY(id)
ALTER TABLE brand ADD CONSTRAINT PK_brand PRIMARY KEY (id)
ALTER TABLE collar ADD CONSTRAINT PK_collar PRIMARY KEY (id)
ALTER TABLE sleeve ADD CONSTRAINT PK_sleeve PRIMARY KEY (id)
ALTER TABLE category ADD CONSTRAINT PK_category PRIMARY KEY (id)
ALTER TABLE material ADD CONSTRAINT PK_material PRIMARY KEY(id)
ALTER TABLE [product] ADD CONSTRAINT PK_product PRIMARY KEY (id)
ALTER TABLE product_detail ADD CONSTRAINT PK_product_detail PRIMARY KEY (id)
ALTER TABLE voucher ADD CONSTRAINT PK_voucher PRIMARY KEY (id)
ALTER TABLE [role] ADD CONSTRAINT PK_role PRIMARY KEY (id)
ALTER TABLE employee ADD CONSTRAINT PK_employee PRIMARY KEY(id)
ALTER TABLE customer ADD CONSTRAINT PK_customer PRIMARY KEY(id)
ALTER TABLE [order] ADD CONSTRAINT PK_order PRIMARY KEY(id)
ALTER TABLE order_detail ADD CONSTRAINT PK_order_detail PRIMARY KEY(id)
ALTER TABLE cart ADD CONSTRAINT PK_cart PRIMARY KEY(id)
ALTER TABLE [address] ADD CONSTRAINT PK_address PRIMARY KEY (id);
ALTER TABLE [address] ADD is_default BIT NOT NULL DEFAULT(0);


-- Thiết lập mã code là duy nhất cho 1 số bảng 
ALTER TABLE color ADD CONSTRAINT UQ_color_name UNIQUE (color_name);
ALTER TABLE brand ADD CONSTRAINT UQ_brand_name UNIQUE (brand_name);
ALTER TABLE category ADD CONSTRAINT UQ_category_name UNIQUE (category_name);
ALTER TABLE collar ADD CONSTRAINT UQ_collar_name UNIQUE (collar_name);
ALTER TABLE material ADD CONSTRAINT UQ_material_name UNIQUE (material_name);
ALTER TABLE size ADD CONSTRAINT UQ_size_name UNIQUE (size_name);
ALTER TABLE sleeve ADD CONSTRAINT UQ_sleeve_name UNIQUE (sleeve_name);


ALTER TABLE [order] ADD CONSTRAINT UQ_order_code UNIQUE (order_code);
ALTER TABLE product_detail ADD CONSTRAINT UQ_product_detail_code UNIQUE(product_detail_code);
ALTER TABLE customer ADD CONSTRAINT UQ_customer_code UNIQUE(customer_code);
ALTER TABLE voucher ADD CONSTRAINT UQ_voucher_code UNIQUE(voucher_code);
ALTER TABLE employee ADD CONSTRAINT UQ_employee_code UNIQUE(employee_code);
ALTER TABLE [product] ADD CONSTRAINT UQ_product_code UNIQUE(product_code);

-- Thiết lập khóa ngoại cho các bảng

--ALTER TABLE [product] DROP CONSTRAINT FK_product_brand

ALTER TABLE [product] ADD CONSTRAINT FK_product_brand 
					  FOREIGN KEY (brand_id) REFERENCES brand(id)
					  --ON DELETE SET NULL
					  ON UPDATE CASCADE

--ALTER TABLE [product] DROP CONSTRAINT FK_product_category

ALTER TABLE [product] ADD CONSTRAINT FK_product_category
					  FOREIGN KEY(category_id) REFERENCES category(id)
					  --ON DELETE SET NULL
					  ON UPDATE CASCADE

--ALTER TABLE [product] DROP CONSTRAINT FK_product_material

ALTER TABLE [product] ADD CONSTRAINT FK_product_material
					  FOREIGN KEY(material_id) REFERENCES material(id)
					  --ON DELETE SET NULL
					  ON UPDATE CASCADE

--ALTER TABLE product_detail DROP CONSTRAINT FK_product_detail_product					  

ALTER TABLE product_detail ADD CONSTRAINT FK_product_detail_product
						   FOREIGN KEY(product_id) REFERENCES [product](id)
						   --ON DELETE SET NULL 
						   ON UPDATE CASCADE

--ALTER TABLE product_detail DROP CONSTRAINT FK_product_detail_size 

ALTER TABLE product_detail ADD CONSTRAINT FK_product_detail_size 
						   FOREIGN KEY(size_id) REFERENCES size(id)
						   --ON DELETE SET NULL
						   ON UPDATE CASCADE

--ALTER TABLE product_detail DROP CONSTRAINT FK_product_detail_color 

ALTER TABLE product_detail ADD CONSTRAINT FK_product_detail_color 
						   FOREIGN KEY(color_id) REFERENCES color(id)
						   --ON DELETE SET NULL
						   ON UPDATE CASCADE
	
--ALTER TABLE product_detail DROP CONSTRAINT FK_product_detail_promotion

ALTER TABLE product_detail ADD CONSTRAINT FK_product_detail_promotion
						   FOREIGN KEY(promotion_id) REFERENCES promotion(id)
						   --ON DELETE SET NULL
						   ON UPDATE CASCADE

-- ALTER TABLE product_detail	DROP CONSTRAINT FK_product_detail_collar

ALTER TABLE product_detail	ADD CONSTRAINT FK_product_detail_collar
							FOREIGN KEY(collar_id) REFERENCES collar(id)
							--ON DELETE SET NULL
						   ON UPDATE CASCADE

-- ALTER TABLE product_detail	DROP CONSTRAINT FK_product_detail_sleeve

ALTER TABLE product_detail	ADD CONSTRAINT FK_product_detail_sleeve
							FOREIGN KEY(sleeve_id) REFERENCES sleeve(id)
							--ON DELETE SET NULL
						   ON UPDATE CASCADE

--ALTER TABLE employee DROP CONSTRAINT FK_employee_role

ALTER TABLE employee ADD CONSTRAINT FK_employee_role 
				   FOREIGN KEY(role_id) REFERENCES role(id)
				   --ON DELETE SET NULL
				   ON UPDATE CASCADE

--ALTER TABLE [order] DROP CONSTRAINT FK_order_employee

ALTER TABLE [order] ADD CONSTRAINT FK_order_employee
					FOREIGN KEY(employee_id) REFERENCES employee(id)
					--ON DELETE SET NULL
					ON UPDATE CASCADE

--ALTER TABLE [order] DROP CONSTRAINT FK_order_voucher

ALTER TABLE [order] ADD CONSTRAINT FK_order_voucher
					FOREIGN KEY(voucher_id) REFERENCES voucher(id)
					--ON DELETE SET NULL
					ON UPDATE CASCADE

--ALTER TABLE [order] DROP CONSTRAINT FK_order_customer

ALTER TABLE [order] ADD CONSTRAINT FK_order_customer
					FOREIGN KEY(customer_id) REFERENCES customer(id)
					--ON DELETE SET NULL
					ON UPDATE CASCADE

--ALTER TABLE order_detail DROP CONSTRAINT FK_order_detail_order

ALTER TABLE order_detail ADD CONSTRAINT FK_order_detail_order
						 FOREIGN KEY(order_id) REFERENCES [order](id)
						 --ON DELETE SET NULL
						 ON UPDATE CASCADE

--ALTER TABLE order_detail DROP CONSTRAINT FK_order_detail_product_detail

ALTER TABLE order_detail ADD CONSTRAINT FK_order_detail_product_detail
						 FOREIGN KEY(product_detail_id) REFERENCES product_detail(id)
						 --ON DELETE SET NULL
						 ON UPDATE CASCADE

--ALTER TABLE [address] DROP CONSTRAINT FK_address_customer

ALTER TABLE [address] ADD CONSTRAINT FK_address_customer
					  FOREIGN KEY(customer_id) REFERENCES customer(id)
					  --ON DELETE SET NULL
					  ON UPDATE CASCADE

--ALTER TABLE cart DROP CONSTRAINT FK_cart_detail_customer

ALTER TABLE cart ADD CONSTRAINT FK_cart_customer
						FOREIGN KEY(customer_id) REFERENCES customer(id)
						--ON DELETE SET NULL
					    ON UPDATE CASCADE

--ALTER TABLE cart DROP CONSTRAINT FK_cart_product_detail

ALTER TABLE cart ADD CONSTRAINT FK_cart_product_detail
						FOREIGN KEY(product_detail_id) REFERENCES product_detail(id)
						--ON DELETE SET NULL
					    ON UPDATE CASCADE

--- ** PHẦN INSERT DỮ LIỆU_ GENCODE
INSERT INTO brand (brand_name, [status]) VALUES 
(N'Nike', 1),
(N'Adidas', 1),
(N'Puma', 1),
(N'Reebok', 1),
(N'Under Armour', 1),
(N'New Balance', 1);

INSERT INTO category (category_name, [status]) VALUES 
(N'Áo thun', 1),
(N'Áo sơ mi', 1),
(N'Áo khoác', 1),
(N'Áo hoodie', 1),
(N'Áo len', 1),
(N'Áo polo', 1);

INSERT INTO material (material_name, [status]) VALUES 
(N'Cotton', 1),
(N'Polyester', 1),
(N'Len', 1),
(N'Jean', 1),
(N'Nỉ', 1),
(N'Vải thun lạnh', 1);

INSERT INTO [product] (brand_id, category_id, material_id, product_code, product_name,[status]) 
VALUES 
		(1, 1, 1, 'PR001', N'Áo thun cổ tròn Nike', 1),
		(1, 1, 2, 'PR002', N'Áo thun thể thao Nike Dri-FIT', 1),
		(2, 2, 1, 'PR003', N'Áo sơ mi tay dài Adidas', 1),
		(2, 2, 3, 'PR004', N'Áo sơ mi họa tiết Adidas', 1),
		(3, 3, 4, 'PR005', N'Áo khoác jeans Puma', 1),
		(3, 3, 5, 'PR006', N'Áo khoác nỉ Puma', 1),
		(4, 4, 6, 'PR007', N'Áo hoodie nỉ Reebok', 1),
		(4, 4, 2, 'PR008', N'Áo hoodie thể thao Reebok', 1),
		(5, 5, 1, 'PR009', N'Áo len cao cổ Under Armour', 1),
		(6, 6, 2, 'PR0010',N'Áo polo thể thao New Balance', 1);

INSERT INTO promotion (promotion_name, promotion_percent, start_date, end_date, [description], [status]) VALUES 
(N'Khuyến mãi Tết Nguyên Đán', 20, '2025-01-15', '2025-02-15', N'Giảm 20% toàn bộ sản phẩm nhân dịp Tết Nguyên Đán.', 1),
(N'Black Friday Sale', 50, '2025-11-25', '2025-11-30', N'Giảm giá cực sốc 50% cho tất cả sản phẩm trong tuần lễ Black Friday.', 1),
(N'Khuyến mãi 8/3', 15, '2025-03-01', '2025-03-08', N'Giảm 15% dành cho khách hàng nữ nhân dịp Quốc tế Phụ nữ.', 1),
(N'Back to School', 10, '2025-08-15', '2025-09-05', N'Giảm 10% cho các sản phẩm áo sơ mi và áo thun chào đón năm học mới.', 1),
(N'Giảm giá hè sôi động', 30, '2025-06-01', '2025-06-30', N'Ưu đãi lên đến 30% cho các sản phẩm áo khoác và hoodie.', 1),
(N'Sale cuối năm', 40, '2025-12-20', '2025-12-31', N'Giảm sốc 40% cho các sản phẩm trong dịp lễ Giáng Sinh và năm mới.', 1);

INSERT INTO color (color_name, [status]) VALUES 
(N'Đen', 1),
(N'Trắng', 1),
(N'Xám', 1),
(N'Xanh dương', 1),
(N'Xanh lá', 1),
(N'Đỏ', 1),
(N'Vàng', 1),
(N'Cam', 1),
(N'Tím', 1),
(N'Nâu', 1);

INSERT INTO size (size_name, [status]) VALUES 
(N'XS', 1),
(N'S', 1),
(N'M', 1),
(N'L', 1),
(N'XL', 1),
(N'XXL', 1);

INSERT INTO collar (collar_name, [status]) VALUES 
(N'Cổ tròn', 1),
(N'Cổ bẻ', 1),
(N'Cổ tim', 1);

INSERT INTO sleeve (sleeve_name, [status]) VALUES 
(N'Tay ngắn', 1),
(N'Tay dài', 1),
(N'Sát nách', 1);

--DELETE FROM [product_detail];
--DBCC CHECKIDENT ('[product_detail]', RESEED, 0);

INSERT INTO product_detail (product_id, size_id, color_id, promotion_id, collar_id, sleeve_id, photo, product_detail_code, import_price, sale_price, quantity, [description], [status]) VALUES
(1, 1, 1, 1, 1, 1, 'photo1.jpg', 'PDT001', 100000, 120000, 50, N'Áo thun cổ tròn màu đen, kích thước XS, giảm giá 20%', 1),
(2, 2, 2, 2, 2, 2, 'photo2.jpg', 'PDT002', 150000, 180000, 30, N'Áo thun thể thao màu trắng, kích thước S, giảm giá 50%', 1),
(3, 3, 3, 3, 3, 1, 'photo3.jpg', 'PDT003', 120000, 140000, 40, N'Áo sơ mi tay dài màu xám, kích thước M, giảm giá 15%', 1),
(4, 4, 4, 4, 1, 2, 'photo4.jpg', 'PDT004', 130000, 160000, 35, N'Áo sơ mi họa tiết màu xanh, kích thước L, giảm giá 10%', 1),
(5, 5, 5, 5, 2, 1, 'photo5.jpg', 'PDT005', 200000, 250000, 25, N'Áo khoác jeans màu đỏ, kích thước XL, giảm giá 20%', 1),
(6, 6, 6, 6, 3, 2, 'photo6.jpg', 'PDT006', 250000, 300000, 20, N'Áo khoác nỉ màu cam, kích thước XXL, giảm giá 30%', 1),
(7, 1, 7, 1, 1, 1, 'photo7.jpg', 'PDT007', 120000, 150000, 45, N'Áo hoodie cổ tròn màu tím, kích thước XS, giảm giá 20%', 1),
(8, 2, 8, 2, 2, 2, 'photo8.jpg', 'PDT008', 170000, 200000, 30, N'Áo hoodie thể thao màu xanh lá, kích thước S, giảm giá 50%', 1),
(9, 3, 9, 3, 3, 1, 'photo9.jpg', 'PDT009', 140000, 160000, 40, N'Áo len cao cổ màu nâu, kích thước M, giảm giá 15%', 1),
(10, 4, 10, 4, 1, 2, 'photo10.jpg', 'PDT010', 180000, 210000, 30, N'Áo len họa tiết màu vàng, kích thước L, giảm giá 10%', 1),
(1, 5, 1, 5, 2, 1, 'photo11.jpg', 'PDT011', 190000, 240000, 35, N'Áo polo thể thao màu đen, kích thước XL, giảm giá 20%', 1),
(2, 6, 2, 6, 3, 2, 'photo12.jpg', 'PDT012', 220000, 270000, 20, N'Áo polo thể thao màu trắng, kích thước XXL, giảm giá 30%', 1),
(3, 1, 3, 1, 1, 1, 'photo13.jpg', 'PDT013', 110000, 130000, 50, N'Áo thun cổ tròn màu đỏ, kích thước XS, giảm giá 20%', 1),
(4, 2, 4, 2, 2, 2, 'photo14.jpg', 'PDT014', 160000, 190000, 40, N'Áo thun thể thao màu cam, kích thước S, giảm giá 50%', 1),
(5, 3, 5, 3, 3, 1, 'photo15.jpg', 'PDT015', 130000, 150000, 45, N'Áo sơ mi tay dài màu xám, kích thước M, giảm giá 15%', 1),
(6, 4, 6, 4, 1, 2, 'photo16.jpg', 'PDT016', 140000, 170000, 30, N'Áo sơ mi họa tiết màu xanh, kích thước L, giảm giá 10%', 1),
(7, 5, 7, 5, 2, 1, 'photo17.jpg', 'PDT017', 200000, 240000, 25, N'Áo khoác jeans màu vàng, kích thước XL, giảm giá 20%', 1),
(8, 6, 8, 6, 3, 2, 'photo18.jpg', 'PDT018', 210000, 250000, 20, N'Áo khoác nỉ màu tím, kích thước XXL, giảm giá 30%', 1),
(9, 1, 9, 1, 1, 1, 'photo19.jpg', 'PDT019', 110000, 130000, 50, N'Áo thun cổ tròn màu xanh, kích thước XS, giảm giá 20%', 1),
(10, 2, 10, 2, 2, 2, 'photo20.jpg', 'PDT020', 150000, 180000, 30, N'Áo thun thể thao màu đỏ, kích thước S, giảm giá 50%', 1),
(1, 3, 1, 3, 3, 1, 'photo21.jpg', 'PDT021', 120000, 140000, 40, N'Áo sơ mi tay dài màu tím, kích thước M, giảm giá 15%', 1),
(2, 4, 2, 4, 1, 2, 'photo22.jpg', 'PDT022', 130000, 160000, 35, N'Áo sơ mi họa tiết màu xám, kích thước L, giảm giá 10%', 1),
(3, 5, 3, 5, 2, 1, 'photo23.jpg', 'PDT023', 190000, 230000, 25, N'Áo polo thể thao màu cam, kích thước XL, giảm giá 20%', 1),
(4, 6, 4, 6, 3, 2, 'photo24.jpg', 'PDT024', 220000, 260000, 20, N'Áo polo thể thao màu xanh, kích thước XXL, giảm giá 30%', 1),
(5, 1, 5, 1, 1, 1, 'photo25.jpg', 'PDT025', 100000, 120000, 50, N'Áo thun cổ tròn màu cam, kích thước XS, giảm giá 20%', 1),
(6, 2, 6, 2, 2, 2, 'photo26.jpg', 'PDT026', 160000, 190000, 30, N'Áo thun thể thao màu xám, kích thước S, giảm giá 50%', 1),
(7, 3, 7, 3, 3, 1, 'photo27.jpg', 'PDT027', 130000, 150000, 45, N'Áo sơ mi tay dài màu đỏ, kích thước M, giảm giá 15%', 1),
(8, 4, 8, 4, 1, 2, 'photo28.jpg', 'PDT028', 140000, 170000, 30, N'Áo sơ mi họa tiết màu vàng, kích thước L, giảm giá 10%', 1),
(9, 5, 9, 5, 2, 1, 'photo29.jpg', 'PDT029', 200000, 240000, 25, N'Áo khoác jeans màu tím, kích thước XL, giảm giá 20%', 1),
(10, 6, 10, 6, 3, 2, 'photo30.jpg', 'PDT030', 220000, 270000, 20, N'Áo khoác nỉ màu xanh, kích thước XXL, giảm giá 30%', 1);

INSERT INTO voucher (voucher_code, voucher_name, [description], min_condition, max_discount, reduced_percent, [start_date], end_date, [status]) VALUES
('VOUCHER01', N'Giảm giá mùa xuân', N'Giảm giá 10% cho đơn hàng từ 500.000đ', 500000, 100000, 10.0, '2025-02-20', '2025-03-31', 1),
('VOUCHER02', N'Giảm giá cho khách mới', N'Giảm giá 15% cho khách hàng lần đầu mua sắm', 300000, 50000, 15.0, '2025-02-15', '2025-02-28', 1),
('VOUCHER03', N'Giảm giá nhân dịp lễ', N'Giảm giá 20% cho đơn hàng từ 1.000.000đ', 1000000, 200000, 20.0, '2025-03-01', '2025-03-15', 1),
('VOUCHER04', N'Khuyến mãi sinh nhật', N'Giảm giá 25% cho tất cả các sản phẩm', 0, 300000, 25.0, '2025-03-10', '2025-03-20', 1),
('VOUCHER05', N'Giảm giá cho khách hàng VIP', N'Giảm giá 30% cho khách hàng VIP trên 1.500.000đ', 1500000, 450000, 30.0, '2025-04-01', '2025-04-30', 1),
('VOUCHER06', N'Giảm giá cuối mùa', N'Giảm giá 50% cho tất cả các sản phẩm còn lại', 0, 500000, 50.0, '2025-04-05', '2025-04-15', 1);

INSERT INTO [role] ([name]) VALUES
('ADMIN'),
('MANAGER'),
('STAFF');

INSERT INTO employee (employee_code, role_id, fullname, username, [password], email, phone, photo, [status], create_date, update_date, [address], forget_password, gender) VALUES
('EMP001', 1, N'Nguyễn Văn A', 'nguyenvana', 'password123', 'nguyenvana@example.com', '0912345678', 'photo1.jpg', 1, '2025-02-17', '2025-02-17',N'Hà Nội' , 0, 1),
('EMP002', 2, N'Nguyễn Thị B', 'nguyenthitha', 'password123', 'nguyenthitha@example.com', '0912345679', 'photo2.jpg', 1, '2025-02-17', '2025-02-17', N'Hà Nam', 0, 0),
('EMP003', 1, N'Phạm Minh C', 'phamminhc', 'password123', 'phamminhc@example.com', '0912345680', 'photo3.jpg', 1, '2025-02-17', '2025-02-17',N'Hà Nội', 0, 1),
('EMP004', 3, N'Lê Quang D', 'lequangd', 'password123', 'lequangd@example.com', '0912345681', 'photo4.jpg', 1, '2025-02-17', '2025-02-17',N'Tây Ninh' ,0, 1),
('EMP005', 2, N'Vũ Minh E', 'vuminhE', 'password123', 'vuminhE@example.com', '0912345682', 'photo5.jpg', 1, '2025-02-17', '2025-02-17',N'Phú Thọ', 0, 0),
('EMP006', 3, N'Trần Thi F', 'tranthif', 'password123', 'tranthif@example.com', '0912345683', 'photo6.jpg', 1, '2025-02-17', '2025-02-17',N'Hà Nội', 0, 0),
('EMP007', 1, N'Hồ Hoàng G', 'hohoangg', 'password123', 'hohoangg@example.com', '0912345684', 'photo7.jpg', 1, '2025-02-17', '2025-02-17', N'Hà Nam', 0, 1),
('EMP008', 2, N'Ngô Minh H', 'ngominhh', 'password123', 'ngominhh@example.com', '0912345685', 'photo8.jpg', 1, '2025-02-17', '2025-02-17', N'Nam Định', 0, 0);

INSERT INTO customer (customer_code, fullname, username, [password], email, phone, create_date, update_date, forget_password, [status]) VALUES
('CUST001', N'Nguyễn Hoàng A', 'nguyenhoanga', 'password123', 'nguyenhoanga@example.com', '0912345678', '2025-02-17', '2025-02-17', 0, 1),
('CUST002', N'Nguyễn Thị B', 'nguyenthithb', 'password123', 'nguyenthithb@example.com', '0912345679',  '2025-02-17', '2025-02-17', 0, 1),
('CUST003', N'Phạm Minh C', 'phamminhc', 'password123', 'phamminhc@example.com', '0912345680',  '2025-02-17', '2025-02-17', 0, 1),
('CUST004', N'Lê Quang D', 'lequangd', 'password123', 'lequangd@example.com', '0912345681', '2025-02-17', '2025-02-17', 0, 1),
('CUST005', N'Vũ Minh E', 'vuminhe', 'password123', 'vuminhe@example.com', '0912345682', '2025-02-17', '2025-02-17', 0, 1),
('CUST006', N'Trần Thi F', 'tranthif', 'password123', 'tranthif@example.com', '0912345683', '2025-02-17', '2025-02-17', 0, 1),
('CUST007', N'Hồ Hoàng G', 'hohoangg', 'password123', 'hohoangg@example.com', '0912345684', '2025-02-17', '2025-02-17', 0, 1),
('CUST008', N'Ngô Minh H', 'ngominhh', 'password123', 'ngominhh@example.com', '0912345685', '2025-02-17', '2025-02-17', 0, 1);

--DELETE FROM [address];
--DBCC CHECKIDENT ('[address]', RESEED, 0);

INSERT INTO [address] (customer_id, province_id, province_name, district_id, district_name, ward_id, ward_name, address_detail) VALUES
(1, 1, N'TP. HCM', 1, N'Quận 1', 1, N'Phường Bến Nghé', N'123 Đường ABC, Phường Bến Nghé, Quận 1'),
(2, 2, N'Hà Nội', 2, N'Quận Hoàn Kiếm', 2, N'Phường Tràng Tiền', N'456 Đường DEF, Phường Tràng Tiền, Quận Hoàn Kiếm'),
(3, 3, N'Đà Nẵng', 3, N'Quận Hải Châu', 3, N'Phường Thuận Phước', N'789 Đường GHI, Phường Thuận Phước, Quận Hải Châu'),
(4, 1, N'TP. HCM', 1, N'Quận 5', 4, N'Phường 1', N'101 Đường JKL, Phường 1, Quận 5'),
(5, 2, N'Hà Nội', 2, N'Quận Ba Đình', 5, N'Phường Liễu Giai', N'202 Đường MNO, Phường Liễu Giai, Quận Ba Đình'),
(6, 3, N'Đà Nẵng', 3, N'Quận Sơn Trà', 6, N'Phường Mỹ An', N'303 Đường PQR, Phường Mỹ An, Quận Sơn Trà'),
(7, 1, N'TP. HCM', 1, N'Quận 3', 7, N'Phường Võ Thị Sáu', N'404 Đường STU, Phường Võ Thị Sáu, Quận 3'),
(8, 2, N'Hà Nội', 2, N'Quận Cầu Giấy', 8, N'Phường Dịch Vọng', N'505 Đường VWX, Phường Dịch Vọng, Quận Cầu Giấy');

--DELETE FROM [order];
--DBCC CHECKIDENT ('[order]', RESEED, 0);

INSERT INTO [order] (employee_id, voucher_id, customer_id, order_code, create_date, total_amount, total_bill, payment_method, status_order, kind_of_order)
VALUES
    (1, 1, 1, 'ORD001', '2025-02-01 10:30:00', 5, 600000, 1, 1, 0),
    (2, 2, 2, 'ORD002', '2025-02-01 11:00:00', 3, 450000, 2, 2, 0),
    (3, 3, 3, 'ORD003', '2025-02-01 12:15:00', 4, 550000, 1, 2, 1),
    (3, 4, 4, 'ORD004', '2025-02-02 14:45:00', 2, 300000, 2, 3, 0),
    (5, 5, 5, 'ORD005', '2025-02-02 16:00:00', 6, 720000, 1, 1, 1),
    (4, 6, 6, 'ORD006', '2025-02-02 18:00:00', 5, 650000, 2, 2, 0),
    (7, 1, 7, 'ORD007', '2025-02-03 09:00:00', 7, 840000, 1, 1, 1),
    (5, 2, 8, 'ORD008', '2025-02-03 10:30:00', 8, 960000, 1, 3, 0),
    (1, 3, 1, 'ORD009', '2025-02-03 12:00:00', 4, 500000, 1, 2, 1),
    (6, 4, 2, 'ORD010', '2025-02-03 14:30:00', 3, 400000, 2, 1, 0),
    (7, 5, 3, 'ORD011', '2025-02-03 15:00:00', 6, 720000, 1, 3, 0),
    (4, 6, 4, 'ORD012', '2025-02-04 09:15:00', 5, 600000, 1, 2, 1),
    (8, 1, 5, 'ORD013', '2025-02-04 11:00:00', 4, 480000, 2, 1, 0),
    (1, 2, 6, 'ORD014', '2025-02-04 13:30:00', 3, 360000, 1, 2, 0),
    (2, 3, 7, 'ORD015', '2025-02-04 15:00:00', 6, 720000, 1, 1, 0),
    (8, 4, 8, 'ORD016', '2025-02-05 10:00:00', 7, 840000, 1, 2, 1),
    (1, 5, 1, 'ORD017', '2025-02-05 11:30:00', 3, 450000, 2, 1, 1),
    (3, 6, 2, 'ORD018', '2025-02-05 12:00:00', 4, 500000, 1, 3, 0),
    (4, 1, 3, 'ORD019', '2025-02-05 13:45:00', 5, 600000, 2, 1, 0),
    (4, 2, 4, 'ORD020', '2025-02-05 14:30:00', 6, 720000, 1, 1, 1);

INSERT INTO [order] (employee_id, voucher_id, customer_id, order_code, create_date, total_amount, total_bill, payment_method, status_order, kind_of_order)
VALUES
    (1, 1, 1, 'ORD021', '2025-02-01 10:30:00', 5, 600000, 1, 1, 0),
    (2, 2, 2, 'ORD022', '2025-10-01 11:00:00', 3, 450000, 2, 2, 0),
    (3, 3, 3, 'ORD023', '2025-10-01 12:15:00', 4, 550000, 1, 3, 1),
    (3, 4, 4, 'ORD024', '2025-12-02 14:45:00', 2, 300000, 2, 4, 0),
    (5, 5, 5, 'ORD025', '2025-09-02 16:00:00', 6, 720000, 1, 5, 1),
    (4, 6, 6, 'ORD026', '2025-08-02 18:00:00', 5, 650000, 2, 5, 0),
    (7, 1, 7, 'ORD027', '2025-04-03 09:00:00', 7, 840000, 1, 5, 1),
    (5, 2, 8, 'ORD028', '2025-03-03 10:30:00', 8, 960000, 1, 3, 0),
    (1, 3, 1, 'ORD029', '2025-05-03 12:00:00', 4, 500000, 1, 5, 1),
    (6, 4, 2, 'ORD030', '2025-06-03 14:30:00', 3, 400000, 2, 1, 0),
    (7, 5, 3, 'ORD031', '2025-07-03 15:00:00', 6, 720000, 1, 3, 0),
    (4, 6, 4, 'ORD032', '2025-08-04 09:15:00', 5, 600000, 1, 5, 1),
    (8, 1, 5, 'ORD033', '2025-09-04 11:00:00', 4, 480000, 2, 1, 0),
    (1, 2, 6, 'ORD034', '2025-01-04 13:30:00', 3, 360000, 1, 2, 0),
    (2, 3, 7, 'ORD035', '2025-03-04 15:00:00', 6, 720000, 1, 5, 0),
    (8, 4, 8, 'ORD036', '2025-08-05 10:00:00', 7, 840000, 1, 2, 1),
    (1, 5, 1, 'ORD037', '2025-09-05 11:30:00', 3, 450000, 2, 1, 1),
    (3, 6, 2, 'ORD038', '2025-10-05 12:00:00', 4, 500000, 1, 5, 0),
    (4, 1, 3, 'ORD039', '2025-11-05 13:45:00', 5, 600000, 2, 1, 0),
    (4, 2, 4, 'ORD040', '2025-12-05 14:30:00', 6, 720000, 1, 5, 1);

INSERT INTO order_detail (order_id, product_detail_id, quantity)
VALUES
	(1, 1, 2),   -- Order 1, Product 1, Quantity 2
	(2, 2, 3),   -- Order 2, Product 2, Quantity 3
	(3, 3, 4),   -- Order 3, Product 3, Quantity 4
	(4, 4, 2),   -- Order 4, Product 4, Quantity 2
	(5, 5, 6),   -- Order 5, Product 5, Quantity 6
	(6, 6, 5),   -- Order 6, Product 6, Quantity 5
	(7, 7, 7),   -- Order 7, Product 7, Quantity 7
	(8, 8, 8),   -- Order 8, Product 8, Quantity 8
	(9, 9, 4),   -- Order 9, Product 9, Quantity 4
	(10, 10, 3),  -- Order 10, Product 10, Quantity 3
	(11, 11, 2),  -- Order 11, Product 11, Quantity 2
	(12, 12, 5),  -- Order 12, Product 12, Quantity 5
	(13, 13, 3),  -- Order 13, Product 13, Quantity 3
	(14, 14, 4),  -- Order 14, Product 14, Quantity 4
	(15, 15, 2),  -- Order 15, Product 15, Quantity 2
	(16, 16, 6),  -- Order 16, Product 16, Quantity 6
	(17, 17, 3),  -- Order 17, Product 17, Quantity 3
	(18, 18, 2),  -- Order 18, Product 18, Quantity 2
	(19, 19, 4),  -- Order 19, Product 19, Quantity 4
	(20, 20, 3);  -- Order 20, Product 20, Quantity 3
INSERT INTO order_detail (order_id, product_detail_id, quantity)
VALUES
	(21, 1, 2),   -- Order 1, Product 1, Quantity 2
	(22, 2, 3),   -- Order 2, Product 2, Quantity 3
	(23, 3, 4),   -- Order 3, Product 3, Quantity 4
	(24, 4, 2),   -- Order 4, Product 4, Quantity 2
	(25, 5, 6),   -- Order 5, Product 5, Quantity 6
	(26, 6, 5),   -- Order 6, Product 6, Quantity 5
	(27, 7, 7),   -- Order 7, Product 7, Quantity 7
	(28, 8, 8),   -- Order 8, Product 8, Quantity 8
	(29, 9, 4),   -- Order 9, Product 9, Quantity 4
	(30, 10, 3),  -- Order 10, Product 10, Quantity 3
	(31, 11, 2),  -- Order 11, Product 11, Quantity 2
	(32, 12, 5),  -- Order 12, Product 12, Quantity 5
	(33, 13, 3),  -- Order 13, Product 13, Quantity 3
	(34, 14, 4),  -- Order 14, Product 14, Quantity 4
	(35, 15, 2),  -- Order 15, Product 15, Quantity 2
	(36, 16, 6),  -- Order 16, Product 16, Quantity 6
	(37, 17, 3),  -- Order 17, Product 17, Quantity 3
	(38, 18, 2),  -- Order 18, Product 18, Quantity 2
	(39, 19, 4),  -- Order 19, Product 19, Quantity 4
	(40, 20, 3);  -- Order 20, Product 20, Quantity 3

INSERT INTO cart (customer_id, product_detail_id, quantity)
VALUES
    (1, 1, 2),  -- Customer 1, Product 1, Quantity 2
    (2, 2, 3),  -- Customer 2, Product 2, Quantity 3
    (3, 3, 1),  -- Customer 3, Product 3, Quantity 1
    (4, 4, 4),  -- Customer 4, Product 4, Quantity 4
    (5, 5, 2),  -- Customer 5, Product 5, Quantity 2
    (6, 6, 3);  -- Customer 6, Product 6, Quantity 3

SELECT * FROM [address]
SELECT * FROM brand
SELECT * FROM cart
SELECT * FROM category
SELECT * FROM color
SELECT * FROM customer
SELECT * FROM material
SELECT * FROM collar
SELECT * FROM sleeve
SELECT * FROM [order]
SELECT * FROM [order_detail]
SELECT * FROM [product]
SELECT * FROM [product_detail]
SELECT * FROM promotion
SELECT * FROM [role]
SELECT * FROM size
SELECT * FROM voucher
SELECT * FROM employee

