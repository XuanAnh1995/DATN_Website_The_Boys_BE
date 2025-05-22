--DROP DATABASE DATNV01

CREATE DATABASE DATNV01
Go

USE DATNV01
GO

CREATE TABLE brand (
    id INT IDENTITY(1,1) NOT NULL,
    brand_name NVARCHAR(100) ,
    [status] BIT  
);
GO

CREATE TABLE category (
    id INT IDENTITY(1,1) NOT NULL,
    category_name NVARCHAR(100) ,
    [status] BIT  
);
GO

CREATE TABLE material (
    id INT IDENTITY(1,1) NOT NULL,
    material_name NVARCHAR(100) ,
    [status] BIT  
);
GO

-- DROP TABLE product

CREATE TABLE [product] (
    id INT IDENTITY(1,1) NOT NULL,
    brand_id INT ,
    category_id INT ,
	material_id INT ,
	product_code VARCHAR(10) NOT NULL,
    product_name NVARCHAR(255) ,
    [status] BIT 
);
GO

CREATE TABLE promotion (
    id INT IDENTITY(1,1) NOT NULL,
    promotion_name NVARCHAR(255) ,
    promotion_percent INT ,
    [start_date] DATETIME ,
    end_date DATETIME ,
    [description] NVARCHAR(500),
    [status] BIT 
);
GO

CREATE TABLE color (
    id INT IDENTITY(1,1) NOT NULL,
    color_name NVARCHAR(100),
    [status] BIT  
);
GO

CREATE TABLE size (
    id INT IDENTITY(1,1) NOT NULL,
    size_name NVARCHAR(100),
    [status] BIT  
);
GO

CREATE TABLE collar(
	id INT IDENTITY(1,1) NOT NULL,
	collar_name NVARCHAR(100) ,
    [status] BIT  
);

CREATE TABLE sleeve(
	id INT IDENTITY(1,1) NOT NULL,
	sleeve_name NVARCHAR(100),
    [status] BIT 
)
GO

-- DROP TABLE product_detail

CREATE TABLE product_detail (
    id INT IDENTITY(1,1) NOT NULL,
    product_id INT,
    size_id INT ,
    color_id INT ,
    promotion_id INT,
	collar_id INT,
	sleeve_id INT,
	photo VARCHAR(250) ,
    product_detail_code VARCHAR(50) NOT NULL,
    import_price DECIMAL(18, 2),
    sale_price DECIMAL(18, 2),
	quantity INT,
    [description] NVARCHAR(500),
    [status] BIT  	
);
GO

--ALTER TABLE product_detail
--ADD photo VARCHAR(250) NOT NULL;

CREATE TABLE voucher (
    id INT IDENTITY(1,1) NOT NULL,
    voucher_code VARCHAR(50) NOT NULL ,
	voucher_name NVARCHAR(250) ,
    [description] NVARCHAR(255),
    min_condition DECIMAL(18, 2),
    max_discount DECIMAL(18, 2),
	reduced_percent FLOAT,
    [start_date] DATETIME,
    end_date DATETIME,
    [status] BIT
);
GO

CREATE TABLE [role] (
    id INT IDENTITY(1,1) NOT NULL,
    [name] VARCHAR(100) NOT NULL
);
GO

--DROP TABLE employee

CREATE TABLE employee (
    id INT IDENTITY(1,1) NOT NULL,
    employee_code VARCHAR(50) NOT NULL,
	role_id INT,
    fullname NVARCHAR(255),
    username VARCHAR(100) ,
    [password] VARCHAR(255),
    email VARCHAR(255) ,
    phone VARCHAR(20) ,
	photo VARCHAR(250) ,
    [status] INT ,
    create_date DATETIME ,
	update_date DATETIME ,
	[address] NVARCHAR(255) ,
	forget_password BIT,
    gender BIT 
);
GO
--ALTER TABLE employee
--ALTER COLUMN gender BIT NOT NULL;

--DROP TABLE customer

CREATE TABLE customer (
    id INT IDENTITY(1,1) NOT NULL ,
    customer_code VARCHAR(50),
    fullname NVARCHAR(255),
	username VARCHAR(100),
    [password] VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(20),
    create_date DATETIME,
	update_date DATETIME,
	forget_password BIT,
    [status] BIT
);
GO

CREATE TABLE [address] (
    id INT IDENTITY(1,1) NOT NULL,
    customer_id INT ,
    province_id INT ,
	province_name NVARCHAR(50),
    district_id INT ,
	district_name NVARCHAR(50) ,
    ward_id INT,
	ward_name NVARCHAR(50) ,
    address_detail NVARCHAR(255) ,
);
GO

CREATE TABLE [order] (
    id INT IDENTITY(1,1) NOT NULL,
    employee_id INT,
    voucher_id INT,
    customer_id INT,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    create_date DATETIME,
    total_amount INT,
	original_total DECIMAL(18,2),
    total_bill DECIMAL(18, 2),
    payment_method INT ,
								-- 0: Tiền mặt
								-- 1: VNPay
    kind_of_order BIT CHECK (kind_of_order IN (0, 1)),  
                                     -- 0: Online
                                     -- 1: POS
    status_order INT CHECK (status_order IN (-1, 0, 1, 2, 3, 4, 5))
                                      -- -1: Đã hủy
                                      --  0: Chờ xác nhận
                                      --  1: Chờ thanh toán
                                      --  2: Đã xác nhận
                                      --  3: Đang giao hàng
                                      --  4: Giao hàng không thành công
                                      --  5: Hoàn thành
);
GO

CREATE TABLE order_detail (
    id INT IDENTITY(1,1) NOT NULL ,
    order_id INT,
    product_detail_id INT,
    quantity INT,
);
GO


CREATE TABLE cart (
    id INT IDENTITY(1,1) NOT NULL,
    customer_id INT ,
    product_detail_id INT ,
    quantity INT
);
GO


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

ALTER TABLE customer ADD CONSTRAINT UQ_customer_username UNIQUE(username);
ALTER TABLE customer ADD CONSTRAINT UQ_customer_email UNIQUE(email);
ALTER TABLE customer ADD CONSTRAINT UQ_customer_phone UNIQUE(phone);

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
GO

INSERT INTO category (category_name, [status]) VALUES 
(N'Áo thun', 1),
(N'Áo sơ mi', 1),
(N'Áo khoác', 1),
(N'Áo hoodie', 1),
(N'Áo len', 1),
(N'Áo polo', 1);
GO

INSERT INTO material (material_name, [status]) VALUES 
(N'Cotton', 1),
(N'Polyester', 1),
(N'Len', 1),
(N'Jean', 1),
(N'Nỉ', 1),
(N'Vải thun lạnh', 1);
GO

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
GO

INSERT INTO promotion (promotion_name, promotion_percent, start_date, end_date, [description], [status]) VALUES 
(N'Khuyến mãi Tết Nguyên Đán', 20, '2025-01-15', '2025-02-15', N'Giảm 20% toàn bộ sản phẩm nhân dịp Tết Nguyên Đán.', 1),
(N'Black Friday Sale', 50, '2025-11-25', '2025-11-30', N'Giảm giá cực sốc 50% cho tất cả sản phẩm trong tuần lễ Black Friday.', 1),
(N'Khuyến mãi 8/3', 15, '2025-03-01', '2025-03-08', N'Giảm 15% dành cho khách hàng nữ nhân dịp Quốc tế Phụ nữ.', 1),
(N'Back to School', 10, '2025-08-15', '2025-09-05', N'Giảm 10% cho các sản phẩm áo sơ mi và áo thun chào đón năm học mới.', 1),
(N'Giảm giá hè sôi động', 30, '2025-06-01', '2025-06-30', N'Ưu đãi lên đến 30% cho các sản phẩm áo khoác và hoodie.', 1),
(N'Sale cuối năm', 40, '2025-12-20', '2025-12-31', N'Giảm sốc 40% cho các sản phẩm trong dịp lễ Giáng Sinh và năm mới.', 1);
GO

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
GO

INSERT INTO size (size_name, [status]) VALUES 
(N'XS', 1),
(N'S', 1),
(N'M', 1),
(N'L', 1),
(N'XL', 1),
(N'XXL', 1);
GO

INSERT INTO collar (collar_name, [status]) VALUES 
(N'Cổ tròn', 1),
(N'Cổ bẻ', 1),
(N'Cổ tim', 1);

INSERT INTO sleeve (sleeve_name, [status]) VALUES 
(N'Tay ngắn', 1),
(N'Tay dài', 1),
(N'Sát nách', 1);
GO

--DELETE FROM [product_detail];
--DBCC CHECKIDENT ('[product_detail]', RESEED, 0);

INSERT INTO product_detail (product_id, size_id, color_id, collar_id, sleeve_id, photo, product_detail_code, import_price, sale_price, quantity, [description], [status]) VALUES
(1, 1, 1, 1, 1, 'photo1.jpg', 'PDT001', 100000, 120000, 50, N'Áo thun cổ tròn màu đen, kích thước XS, giảm giá 20%', 1),
(2, 2, 2, 2, 2, 'photo2.jpg', 'PDT002', 150000, 180000, 30, N'Áo thun thể thao màu trắng, kích thước S, giảm giá 50%', 1),
(3, 3, 3, 3, 1, 'photo3.jpg', 'PDT003', 120000, 140000, 40, N'Áo sơ mi tay dài màu xám, kích thước M, giảm giá 15%', 1),
(4, 4, 4, 1, 2, 'photo4.jpg', 'PDT004', 130000, 160000, 35, N'Áo sơ mi họa tiết màu xanh, kích thước L, giảm giá 10%', 1),
(5, 5, 5, 2, 1, 'photo5.jpg', 'PDT005', 200000, 250000, 25, N'Áo khoác jeans màu đỏ, kích thước XL, giảm giá 20%', 1),
(6, 6, 6, 3, 2, 'photo6.jpg', 'PDT006', 250000, 300000, 20, N'Áo khoác nỉ màu cam, kích thước XXL, giảm giá 30%', 1),
(7, 1, 7, 1, 1, 'photo7.jpg', 'PDT007', 120000, 150000, 45, N'Áo hoodie cổ tròn màu tím, kích thước XS, giảm giá 20%', 1),
(8, 2, 8, 2, 2, 'photo8.jpg', 'PDT008', 170000, 200000, 30, N'Áo hoodie thể thao màu xanh lá, kích thước S, giảm giá 50%', 1),
(9, 3, 9, 3, 1, 'photo9.jpg', 'PDT009', 140000, 160000, 40, N'Áo len cao cổ màu nâu, kích thước M, giảm giá 15%', 1),
(10, 4, 10, 1, 2, 'photo10.jpg', 'PDT010', 180000, 210000, 30, N'Áo len họa tiết màu vàng, kích thước L, giảm giá 10%', 1),
(1, 5, 1, 2, 1, 'photo11.jpg', 'PDT011', 190000, 240000, 35, N'Áo polo thể thao màu đen, kích thước XL, giảm giá 20%', 1),
(2, 6, 2, 3, 2, 'photo12.jpg', 'PDT012', 220000, 270000, 20, N'Áo polo thể thao màu trắng, kích thước XXL, giảm giá 30%', 1),
(3, 1, 3, 1, 1, 'photo13.jpg', 'PDT013', 110000, 130000, 50, N'Áo thun cổ tròn màu đỏ, kích thước XS, giảm giá 20%', 1),
(4, 2, 4, 2, 2, 'photo14.jpg', 'PDT014', 160000, 190000, 40, N'Áo thun thể thao màu cam, kích thước S, giảm giá 50%', 1),
(5, 3, 5, 3, 1, 'photo15.jpg', 'PDT015', 130000, 150000, 45, N'Áo sơ mi tay dài màu xám, kích thước M, giảm giá 15%', 1),
(6, 4, 6, 1, 2, 'photo16.jpg', 'PDT016', 140000, 170000, 30, N'Áo sơ mi họa tiết màu xanh, kích thước L, giảm giá 10%', 1),
(7, 5, 7, 2, 1, 'photo17.jpg', 'PDT017', 200000, 240000, 25, N'Áo khoác jeans màu vàng, kích thước XL, giảm giá 20%', 1),
(8, 6, 8, 3, 2, 'photo18.jpg', 'PDT018', 210000, 250000, 20, N'Áo khoác nỉ màu tím, kích thước XXL, giảm giá 30%', 1),
(9, 1, 9, 1, 1, 'photo19.jpg', 'PDT019', 110000, 130000, 50, N'Áo thun cổ tròn màu xanh, kích thước XS, giảm giá 20%', 1),
(10, 2, 10, 2, 2, 'photo20.jpg', 'PDT020', 150000, 180000, 30, N'Áo thun thể thao màu đỏ, kích thước S, giảm giá 50%', 1);

GO

INSERT INTO voucher (voucher_code, voucher_name, [description], min_condition, max_discount, reduced_percent, [start_date], end_date, [status]) VALUES
('VOUCHER01', N'Giảm giá mùa xuân', N'Giảm giá 10% cho đơn hàng từ 500.000đ', 500000, 100000, 10.0, '2025-02-20', '2025-03-31', 1),
('VOUCHER02', N'Giảm giá cho khách mới', N'Giảm giá 15% cho khách hàng lần đầu mua sắm', 300000, 50000, 15.0, '2025-02-15', '2025-02-28', 1),
('VOUCHER03', N'Giảm giá nhân dịp lễ', N'Giảm giá 20% cho đơn hàng từ 1.000.000đ', 1000000, 200000, 20.0, '2025-03-01', '2025-03-15', 1),
('VOUCHER04', N'Khuyến mãi sinh nhật', N'Giảm giá 25% cho tất cả các sản phẩm', 0, 300000, 25.0, '2025-03-10', '2025-03-20', 1),
('VOUCHER05', N'Giảm giá cho khách hàng VIP', N'Giảm giá 30% cho khách hàng VIP trên 1.500.000đ', 1500000, 450000, 30.0, '2025-04-01', '2025-04-30', 1),
('VOUCHER06', N'Giảm giá cuối mùa', N'Giảm giá 50% cho tất cả các sản phẩm còn lại', 0, 500000, 50.0, '2025-04-05', '2025-04-15', 1);
GO

INSERT INTO [role] ([name]) VALUES
('ADMIN'),
('STAFF');
GO

INSERT INTO employee (employee_code, role_id, fullname, username, [password], email, phone, photo, [status], create_date, update_date, [address], forget_password, gender) VALUES
('ADMIN', 1, N'Nguyễn Văn A', 'admin', '$2a$10$6.jaq4UVcich29opquLaFOZYHy1OGz7PK.poVK/.AQIAXtDknkvI2', 'nguyenvana@example.com', '0912345678', 'photo1.jpg', 1, '2025-02-17', '2025-02-17',N'Hà Nội' , 0, 1),
('STAFF', 2, N'Nguyễn Thị B', 'staff', '$2a$10$6.jaq4UVcich29opquLaFOZYHy1OGz7PK.poVK/.AQIAXtDknkvI2', 'nguyenthitha@example.com', '0912345679', 'photo2.jpg', 1, '2025-02-17', '2025-02-17', N'Hà Nam', 0, 0),
('EMP003', 2, N'Phạm Minh C', 'phamminhc', 'password123', 'phamminhc@example.com', '0912345680', 'photo3.jpg', 1, '2025-02-17', '2025-02-17',N'Hà Nội', 0, 1),
('EMP004', 2, N'Lê Quang D', 'lequangd', 'password123', 'lequangd@example.com', '0912345681', 'photo4.jpg', 1, '2025-02-17', '2025-02-17',N'Tây Ninh' ,0, 1),
('EMP005', 2, N'Vũ Minh E', 'vuminhE', 'password123', 'vuminhE@example.com', '0912345682', 'photo5.jpg', 1, '2025-02-17', '2025-02-17',N'Phú Thọ', 0, 0),
('EMP006', 2, N'Trần Thi F', 'tranthif', 'password123', 'tranthif@example.com', '0912345683', 'photo6.jpg', 1, '2025-02-17', '2025-02-17',N'Hà Nội', 0, 0),
('EMP007', 1, N'Hồ Hoàng G', 'hohoangg', 'password123', 'hohoangg@example.com', '0912345684', 'photo7.jpg', 1, '2025-02-17', '2025-02-17', N'Hà Nam', 0, 1),
('EMP008', 2, N'Ngô Minh H', 'ngominhh', 'password123', 'ngominhh@example.com', '0912345685', 'photo8.jpg', 1, '2025-02-17', '2025-02-17', N'Nam Định', 0, 0);
GO

INSERT INTO customer (customer_code, fullname, username, [password], email, phone, create_date, update_date, forget_password, [status]) 
VALUES 
('USER', N'Nguyễn Hoàng A', 'user', '$2a$10$6.jaq4UVcich29opquLaFOZYHy1OGz7PK.poVK/.AQIAXtDknkvI2', 'nguyenhoanga@example.com', '0912345678', '2025-02-17', '2025-02-17', 0, 1),
('CUST002', N'Nguyễn Thị B', 'nguyenthithb', 'password123', 'nguyenthithb@example.com', '0912345679',  '2025-02-17', '2025-02-17', 0, 1),
('CUST003', N'Phạm Minh C', 'phamminhc_c', 'password123', 'phamminhc@example.com', '0912345680',  '2025-02-17', '2025-02-17', 0, 1),
('CUST004', N'Lê Quang D', 'lequangd_c', 'password123', 'lequangd@example.com', '0912345681', '2025-02-17', '2025-02-17', 0, 1),
('CUST005', N'Vũ Minh E', 'vuminhe_c', 'password123', 'vuminhe@example.com', '0912345682', '2025-02-17', '2025-02-17', 0, 1),
('CUST006', N'Trần Thi F', 'tranthif_c', 'password123', 'tranthif@example.com', '0912345683', '2025-02-17', '2025-02-17', 0, 1),
('CUST007', N'Hồ Hoàng G', 'hohoangg_c', 'password123', 'hohoangg@example.com', '0912345684', '2025-02-17', '2025-02-17', 0, 1),
('CUST008', N'Ngô Minh H', 'ngominhh_c', 'password123', 'ngominhh@example.com', '0912345685', '2025-02-17', '2025-02-17', 0, 1);
GO



SET IDENTITY_INSERT customer ON;

INSERT INTO customer (id, customer_code, fullname, username, [password], email, phone, create_date, update_date, forget_password, [status]) 
VALUES (-1, 'GUEST', N'Khách vãng lai', NULL, NULL, NULL, NULL, GETDATE(), GETDATE(), 0, 1);

SET IDENTITY_INSERT customer OFF;

ALTER TABLE [order]
ADD phone VARCHAR(15),
    [address] NVARCHAR(255),
    shipfee DECIMAL(18, 2),
    discount DECIMAL(18, 2),
	note NVARCHAR(255) NULL;
GO

ALTER TABLE [order_detail]
ADD price DECIMAL(18, 2);
GO


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