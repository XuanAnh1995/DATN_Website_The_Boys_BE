--DROP DATABASE DATNV01

CREATE DATABASE DATNV01

USE DATNV01
GO

CREATE TABLE brand (
    id INT IDENTITY(1,1) NOT NULL,
    brand_name NVARCHAR(100) ,
    [status] BIT  
);

CREATE TABLE category (
    id INT IDENTITY(1,1) NOT NULL,
    category_name NVARCHAR(100) ,
    [status] BIT  
);

CREATE TABLE material (
    id INT IDENTITY(1,1) NOT NULL,
    material_name NVARCHAR(100) ,
    [status] BIT  
);

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

CREATE TABLE promotion (
    id INT IDENTITY(1,1) NOT NULL,
    promotion_name NVARCHAR(255) ,
    promotion_percent INT ,
    [start_date] DATETIME ,
    end_date DATETIME ,
    [description] NVARCHAR(500),
    [status] BIT 
);

CREATE TABLE color (
    id INT IDENTITY(1,1) NOT NULL,
    color_name NVARCHAR(100),
    [status] BIT  
);

CREATE TABLE size (
    id INT IDENTITY(1,1) NOT NULL,
    size_name NVARCHAR(100),
    [status] BIT  
);

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

CREATE TABLE [role] (
    id INT IDENTITY(1,1) NOT NULL,
    [name] VARCHAR(100) NOT NULL
);

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

CREATE TABLE [order] (
    id INT IDENTITY(1,1) NOT NULL,

    employee_id INT,							-- Nhân viên tạo đơn (POS)
    voucher_id INT,								-- Mã giảm giá áp dụng
    customer_id INT,							-- Khách hàng (đối với đơn online)

    order_code VARCHAR(50) NOT NULL UNIQUE,		-- Mã đơn hàng
    create_date DATETIME,						-- Ngày tạo đơn

    total_amount DECIMAL(18,2),					-- Tổng tiền trước giảm giá
	discount DECIMAL(18,2) DEFAULT 0,			-- Tổng giảm giá (voucher, khuyến mãi)
	original_total DECIMAL(18,2),				-- Tổng tiền trước giảm giá
	shipfee DECIMAL(18,2) DEFAULT 0,			-- Phí giao hàng
    total_bill DECIMAL(18, 2),					-- Tổng cần thanh toán

    payment_method INT ,						-- 0: Tiền mặt
												-- 1: VNPay

	payment_status INT CHECK (payment_status IN (0, 1, 2)),
												-- 0: Chưa thanh toán
												-- 1: Đã thanh toán
												-- 2: Đã hoàn tiền

    kind_of_order BIT CHECK (kind_of_order IN (0, 1)),  
												-- 0: Online
												-- 1: POS

    status_order INT CHECK (status_order IN (-1, 0, 1, 2, 3, 4, 5)),
                                      -- -1: Đã hủy
                                      --  0: Chờ xác nhận
                                      --  1: Chờ thanh toán
                                      --  2: Đã xác nhận
                                      --  3: Đang giao hàng
                                      --  4: Giao hàng không thành công
                                      --  5: Hoàn thành

	phone VARCHAR(20),                  -- Số điện thoại nhận hàng
    [address] NVARCHAR(255),              -- Địa chỉ nhận hàng

	note NVARCHAR(255)  -- Lý do thay đổi trạng thái đơn
);

CREATE TABLE order_detail (
    id INT IDENTITY(1,1) NOT NULL ,
    order_id INT,
    product_detail_id INT,
    quantity INT,
	price DECIMAL(18, 2)
);


CREATE TABLE cart (
    id INT IDENTITY(1,1) NOT NULL,
    customer_id INT ,
    product_detail_id INT ,
    quantity INT
);

-- Thêm index
CREATE INDEX idx_promotion_name ON promotion (promotion_name);
CREATE INDEX idx_promotion_dates ON promotion ([start_date], end_date);
CREATE INDEX idx_promotion_percent ON promotion (promotion_percent);

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


INSERT INTO [role] ([name]) VALUES
('ADMIN'),
('STAFF');
GO

INSERT INTO employee (employee_code, role_id, fullname, username, [password], email, phone, photo, [status], create_date, update_date, [address], forget_password, gender) VALUES
('ADMIN', 1, N'Nguyễn Văn A', 'admin', 'password123', 'nguyenvana@example.com', '0912345678', 'photo1.jpg', 1, '2025-02-17', '2025-02-17',N'Hà Nội' , 0, 1),
('STAFF', 2, N'Nguyễn Thị B', 'staff', 'password123', 'nguyenthitha@example.com', '0912345679', 'photo2.jpg', 1, '2025-02-17', '2025-02-17', N'Hà Nam', 0, 0),
('EMP003', 2, N'Phạm Minh C', 'phamminhc', 'password123', 'phamminhc@example.com', '0912345680', 'photo3.jpg', 1, '2025-02-17', '2025-02-17',N'Hà Nội', 0, 1),
('EMP004', 2, N'Lê Quang D', 'lequangd', 'password123', 'lequangd@example.com', '0912345681', 'photo4.jpg', 1, '2025-02-17', '2025-02-17',N'Tây Ninh' ,0, 1),
('EMP005', 2, N'Vũ Minh E', 'vuminhE', 'password123', 'vuminhE@example.com', '0912345682', 'photo5.jpg', 1, '2025-02-17', '2025-02-17',N'Phú Thọ', 0, 0),
('EMP006', 2, N'Trần Thi F', 'tranthif', 'password123', 'tranthif@example.com', '0912345683', 'photo6.jpg', 1, '2025-02-17', '2025-02-17',N'Hà Nội', 0, 0),
('EMP007', 1, N'Hồ Hoàng G', 'hohoangg', 'password123', 'hohoangg@example.com', '0912345684', 'photo7.jpg', 1, '2025-02-17', '2025-02-17', N'Hà Nam', 0, 1),
('EMP008', 2, N'Ngô Minh H', 'ngominhh', 'password123', 'ngominhh@example.com', '0912345685', 'photo8.jpg', 1, '2025-02-17', '2025-02-17', N'Nam Định', 0, 0);
GO

INSERT INTO customer (customer_code, fullname, username, [password], email, phone, create_date, update_date, forget_password, [status]) 
VALUES 
('USER', N'Nguyễn Hoàng A', 'user', 'password123', 'nguyenhoanga@example.com', '0912345678', '2025-02-17', '2025-02-17', 0, 1),
('CUST002', N'Nguyễn Thị B', 'nguyenthithb', 'password123', 'nguyenthithb@example.com', '0912345679',  '2025-02-17', '2025-02-17', 0, 1),
('CUST003', N'Phạm Minh C', 'phamminhc', 'password123', 'phamminhc@example.com', '0912345680',  '2025-02-17', '2025-02-17', 0, 1),
('CUST004', N'Lê Quang D', 'lequangd', 'password123', 'lequangd@example.com', '0912345681', '2025-02-17', '2025-02-17', 0, 1),
('CUST005', N'Vũ Minh E', 'vuminhe', 'password123', 'vuminhe@example.com', '0912345682', '2025-02-17', '2025-02-17', 0, 1),
('CUST006', N'Trần Thi F', 'tranthif', 'password123', 'tranthif@example.com', '0912345683', '2025-02-17', '2025-02-17', 0, 1),
('CUST007', N'Hồ Hoàng G', 'hohoangg', 'password123', 'hohoangg@example.com', '0912345684', '2025-02-17', '2025-02-17', 0, 1),
('CUST008', N'Ngô Minh H', 'ngominhh', 'password123', 'ngominhh@example.com', '0912345685', '2025-02-17', '2025-02-17', 0, 1);
GO
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
GO

--DELETE FROM [order];
--DBCC CHECKIDENT ('[order]', RESEED, 0);


SET IDENTITY_INSERT customer ON;

INSERT INTO customer (id, customer_code, fullname, username, [password], email, phone, create_date, update_date, forget_password, [status]) 
VALUES (-1, 'GUEST', N'Khách vãng lai', NULL, NULL, NULL, NULL, GETDATE(), GETDATE(), 0, 1);

SET IDENTITY_INSERT customer OFF;

Update employee
set password = '$2a$10$7.Q1TnBDminn441ESszN0ugdJP4xgqo1vWM8rkIEghpD1f4J04Xii'
where username = 'admin'

Update employee
set password = '$2a$10$7.Q1TnBDminn441ESszN0ugdJP4xgqo1vWM8rkIEghpD1f4J04Xii'
where username = 'staff'

SELECT * FROM brand
SELECT * FROM category
SELECT * FROM material
SELECT * FROM [product]

SELECT * FROM color
SELECT * FROM collar
SELECT * FROM sleeve
SELECT * FROM size
SELECT * FROM promotion
SELECT * FROM [product_detail]

SELECT * FROM [role]
SELECT * FROM employee

SELECT * FROM customer
SELECT * FROM [address]

SELECT * FROM voucher
SELECT * FROM [order]
SELECT * FROM [order_detail]

SELECT * FROM cart

