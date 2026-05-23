CREATE DATABASE IF NOT EXISTS ecommerce_portal
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ecommerce_portal;

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    address TEXT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(160) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    image_url VARCHAR(600),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(40) NOT NULL DEFAULT 'Beklemede',
    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_active ON products(is_active);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);

INSERT INTO users (full_name, email, password, phone, address, role) VALUES
('Admin Kullanıcı', 'admin@portal.test', 'pbkdf2$65536$T5qCJznOTFDTAV22czjBcA==$T6eXwm5nkr5MfN00y9G4Bl9H++/VscMhipOvSds/t3I=', '05550000000', 'Yönetim ofisi', 'ADMIN'),
('Ayşe Demir', 'ayse@example.com', 'pbkdf2$65536$Oaj52bzEhYmYI5znmofhsg==$dbCNjK939KpHasameeOkCQMo75mmH5vt4BrjFiZhHy8=', '05551112233', 'İstanbul / Kadıköy', 'CUSTOMER');

INSERT INTO categories (name, description, is_active) VALUES
('Telefon ve Mobil', 'Akıllı telefonlar, saatler ve mobil yaşam ürünleri', TRUE),
('Bilgisayar ve Çevre Birimleri', 'Dizüstü bilgisayarlar, masaüstü sistemler ve üretkenlik ekipmanları', TRUE),
('Ses ve Aksesuar', 'Kulaklık, klavye, mouse, şarj ve günlük teknoloji aksesuarları', TRUE),
('Kitap ve Öğrenme', 'Yazılım, kariyer, üretkenlik ve kişisel gelişim kitapları', TRUE),
('Giyim ve Yaşam', 'Günlük kullanıma uygun sade, rahat ve işlevsel ürünler', TRUE),
('Ev ve Mutfak', 'Ev düzeni, kahve, mutfak ve yaşam alanı ürünleri', TRUE),
('Kırtasiye ve Hobi', 'Not defterleri, çizim araçları, masa düzeni ve hobi ürünleri', TRUE),
('Spor ve Outdoor', 'Hareketli yaşam, kamp ve şehir dışı kullanım ürünleri', TRUE),
('Oyun ve Konsol', 'Oyun deneyimini destekleyen konsol, kumanda ve ekipmanlar', TRUE),
('Akıllı Ev', 'Aydınlatma, enerji takibi ve bağlantılı ev çözümleri', TRUE);

INSERT INTO products (category_id, name, description, price, stock, image_url, is_active) VALUES
(1, 'Nova X Pro Telefon', 'Yüksek çözünürlüklü ekran, uzun pil ömrü ve güçlü kamera sistemi sunan akıllı telefon.', 24999.90, 14, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80', TRUE),
(1, 'Pocket Mini Telefon', 'Tek elle rahat kullanılan, hafif ve uygun fiyatlı akıllı telefon.', 11999.00, 0, 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80', TRUE),
(2, 'AtlasBook 14 Laptop', 'Ofis, okul ve yazılım geliştirme için dengeli performans sunan dizüstü bilgisayar.', 32999.50, 8, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', TRUE),
(2, 'Creator Station PC', 'Tasarım, video düzenleme ve çoklu görevler için güçlü masaüstü bilgisayar.', 45999.00, 5, 'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', TRUE),
(3, 'Kablosuz Kulaklık', 'Gürültü azaltma, hızlı şarj ve net mikrofon desteğiyle günlük kullanım kulaklığı.', 2499.90, 32, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80', TRUE),
(3, 'Mekanik Klavye', 'Sessiz switch, aydınlatma ve metal gövdeye sahip kompakt mekanik klavye.', 1899.90, 21, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', TRUE),
(4, 'Java MVC Rehberi', 'Servlet, JSP, JSTL ve JDBC ile web uygulaması geliştirmeyi anlatan pratik rehber.', 420.00, 18, 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=900&q=80', TRUE),
(5, 'Basic Kapüşonlu', 'Günlük kullanım için sade, pamuklu ve rahat kapüşonlu sweatshirt.', 899.00, 12, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=900&q=80', TRUE),
(7, 'Bulut Çizgili Not Defteri', 'Kalın kapaklı, noktalı sayfalı ve günlük planlama için ideal not defteri.', 179.90, 45, 'https://images.unsplash.com/photo-1517842645767-c639042777db?auto=format&fit=crop&w=900&q=80', TRUE),
(6, 'Seramik Kahve Seti', 'İki fincan, iki tabak ve mat dokulu özel servis setinden oluşan kahve takımı.', 649.90, 16, 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=900&q=80', TRUE),
(10, 'Akıllı LED Ampul', 'Uygulama üzerinden renk, parlaklık ve zamanlama ayarı yapılabilen LED ampul.', 329.90, 38, 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80', TRUE),
(8, 'Kaymaz Yoga Matı', 'Evde antrenman, esneme ve pilates için kaymaz yüzeye sahip mat.', 549.00, 27, 'https://images.unsplash.com/photo-1599901860904-17e6ed7083a0?auto=format&fit=crop&w=900&q=80', TRUE),
(9, 'Kablosuz Oyun Kumandası', 'Uzun pil ömrü ve düşük gecikme süresiyle konsol ve bilgisayar uyumlu kumanda.', 1699.00, 19, 'https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?auto=format&fit=crop&w=900&q=80', TRUE),
(2, 'Taşınabilir SSD 1TB', 'USB-C bağlantılı, kompakt ve yüksek hızlı harici depolama çözümü.', 2899.90, 24, 'https://images.unsplash.com/photo-1597138804456-e7dca7f59d36?auto=format&fit=crop&w=900&q=80', TRUE),
(7, 'Dijital Çizim Tableti', 'Basınç hassasiyetli kalem ve geniş çizim alanı sunan yaratıcı üretim tableti.', 3599.00, 9, 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=900&q=80', TRUE),
(6, 'Çelik Termal Matara', 'Sıcak ve soğuk içecekleri uzun süre koruyan, sızdırmaz kapaklı matara.', 499.90, 41, 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=80', TRUE),
(5, 'Minimal Sırt Çantası', 'Laptop bölmeli, suya dayanıklı ve şehir içi kullanıma uygun sade çanta.', 1199.90, 17, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80', TRUE),
(4, 'Veri Yapıları Kart Seti', 'Algoritma ve veri yapıları tekrarları için hazırlanmış kısa bilgi kartları.', 299.90, 35, 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=900&q=80', TRUE),
(3, 'Gürültü Önleyici Kulak Üstü', 'Uzun kullanımda konfor sağlayan, aktif gürültü engellemeli kulak üstü kulaklık.', 4299.90, 11, 'https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=900&q=80', TRUE),
(3, 'Type-C Hızlı Şarj Seti', '65W adaptör ve örgülü Type-C kablodan oluşan hızlı şarj seti.', 749.90, 54, 'https://images.unsplash.com/photo-1583863788434-e58a36330cf0?auto=format&fit=crop&w=900&q=80', TRUE),
(1, 'Pulse Akıllı Saat', 'Nabız takibi, uyku analizi ve bildirim desteği sunan hafif akıllı saat.', 3499.00, 22, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80', TRUE),
(6, 'Dijital Mutfak Tartısı', 'Gram hassasiyetinde ölçüm yapan, kompakt ve kolay temizlenebilir mutfak tartısı.', 389.90, 28, 'https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=80', TRUE),
(7, 'Masaüstü Organizer', 'Kalem, not, kablo ve küçük aksesuarları düzenlemek için çok bölmeli organizer.', 249.90, 47, 'https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=900&q=80', TRUE),
(8, 'Hafif Yağmurluk', 'Katlanabilir, nefes alabilen ve şehir dışı kullanıma uygun hafif yağmurluk.', 1399.00, 13, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80', TRUE),
(9, 'Retro Konsol Mini', 'Klasik oyun deneyimini kompakt tasarımla sunan HDMI çıkışlı mini konsol.', 2199.00, 7, 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=900&q=80', TRUE),
(4, 'Modern Java ile Veri Yapıları', 'Java örnekleriyle liste, kuyruk, ağaç, hash ve grafik yapılarını açıklayan kitap.', 560.00, 23, 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=900&q=80', TRUE),
(3, 'Ergonomik Dikey Mouse', 'Bilek yorgunluğunu azaltan, sessiz tıklamalı ve kablosuz dikey mouse.', 899.90, 34, 'https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=900&q=80', TRUE),
(10, 'Akıllı Priz Mini', 'Enerji tüketimini takip eden, zamanlayıcı destekli kompakt akıllı priz.', 449.90, 36, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?auto=format&fit=crop&w=900&q=80', TRUE),
(5, 'Günlük Sneaker', 'Yumuşak tabanlı, sade tasarımlı ve uzun yürüyüşlere uygun günlük sneaker.', 1599.00, 20, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=900&q=80', TRUE),
(7, 'Kamera Işık Halkası', 'Video çekimi ve canlı dersler için ayarlanabilir parlaklıkta ışık halkası.', 699.90, 29, 'https://images.unsplash.com/photo-1618609255910-1950ba2fb2db?auto=format&fit=crop&w=900&q=80', TRUE);
