# Final Proje Ödevi: Java MVC ve JSTL ile Temel E-Ticaret Portalı

## Kapak Bilgileri

| Alan | Bilgi |
| --- | --- |
| Ders | Web Programlama |
| Proje adı | Java MVC ve JSTL ile Temel E-Ticaret Portalı |
| Öğrenci | Yasin Engin |
| Öğrenci no | 23060510 |
| GitHub kaynak kod bağlantısı | https://github.com/YasinEnginn/java-mvc-ecommerce-portal.git |
| YouTube video bağlantısı | https://youtu.be/GJYREgF0e7U |
| Rapor dosya adı | 23060510_rapor.pdf |

Bu rapor, Web Programlama final projesi kapsamında geliştirilen Java tabanlı e-ticaret portalının teknik yapısını, kullanılan teknolojileri, veritabanı tasarımını, MVC mimarisini, gerçekleştirilen kullanıcı ve yönetici işlemlerini, test sonuçlarını ve teslim bilgilerini açıklamaktadır.

## 1. Projenin Amacı

Bu proje kapsamında Java tabanlı MVC mimarisi kullanılarak temel düzeyde çalışan bir e-ticaret sistemi geliştirilmiştir. Uygulamada kullanıcıların ürünleri görüntüleyebilmesi, kategoriye göre filtreleme yapabilmesi, ürün detaylarını inceleyebilmesi, sepete ürün ekleyebilmesi ve sipariş oluşturabilmesi amaçlanmıştır.

Yönetici paneli tarafında kategori, ürün, stok, sipariş ve kullanıcı yönetimi işlemleri gerçekleştirilmiştir. Proje özellikle Servlet, JSP, JSTL, JDBC, session yönetimi, form işlemleri, rol tabanlı erişim kontrolü ve veritabanı işlemlerinin birlikte uygulanmasını hedeflemektedir.

## 2. Kullanılan Teknolojiler

| Teknoloji | Kullanım amacı |
| --- | --- |
| Java 17 | Uygulama geliştirme dili |
| Jakarta Servlet 6 | HTTP isteklerinin karşılanması ve controller katmanı |
| JSP | Kullanıcı arayüzü sayfalarının oluşturulması |
| JSTL 3 | JSP içinde dinamik listeleme, koşullu gösterim ve formatlama |
| HTML ve CSS | Arayüz düzeni ve sayfa tasarımı |
| JDBC | Veritabanı bağlantısı ve sorgu işlemleri |
| MySQL / MariaDB | Uygulamanın ana veritabanı |
| Apache Tomcat 10+ | Web uygulaması sunucusu |
| Maven | Bağımlılık yönetimi, test çalıştırma ve WAR paketleme |
| JUnit 5, H2, Embedded Tomcat | DAO ve web akışlarının otomatik test edilmesi |

Projede Spring Boot, Hibernate, JPA, React, Angular veya Vue gibi ileri seviye frameworkler kullanılmamıştır. Veritabanı işlemleri doğrudan JDBC ve DAO sınıfları üzerinden yürütülmüştür.

## 3. Genel Sistem Yapısı ve Roller

Uygulama iki ana bölümden oluşmaktadır: kullanıcı tarafı e-ticaret portalı ve yönetici paneli. Kullanıcı tarafında ziyaretçiler aktif ürünleri listeleyebilmekte, ürünleri kategoriye göre filtreleyebilmekte, ürün araması yapabilmekte, ürün detaylarını inceleyebilmekte, sepet işlemlerini gerçekleştirebilmekte ve giriş yaptıktan sonra sipariş oluşturabilmektedir.

Yönetici panelinde admin rolündeki kullanıcılar kategori yönetimi, ürün yönetimi, stok yönetimi, sipariş listeleme, sipariş durumu güncelleme ve kullanıcı listeleme işlemlerini gerçekleştirebilmektedir.

## 4. Kullanıcı Tarafı İsterleri

| İster | Uygulanan bölüm |
| --- | --- |
| Aktif ürün listeleme | `HomeServlet`, `ProductDAO`, `index.jsp` |
| Kategori filtreleme | `categoryId` parametresi ve kategori bağlantıları |
| Ürün arama | `q` parametresi ile ürün adı/açıklaması üzerinde arama |
| Ürün detay sayfası | `ProductDetailServlet`, `product-detail.jsp` |
| Kullanıcı kayıt işlemi | `RegisterServlet`, `UserDAO`, `PasswordUtil` |
| Kullanıcı giriş işlemi | `LoginServlet`, `currentUser` session bilgisi |
| Session tabanlı sepet | `Cart`, `CartItem`, `CartServlet`, `cart.jsp` |
| Sipariş oluşturma | `CheckoutServlet`, `OrderDAO.createOrder` |
| Siparişlerim sayfası | `MyOrdersServlet`, `OrderDetailServlet` |

Sepet işlemlerinde stoktan fazla ürün eklenmesi engellenmiştir. Sipariş oluşturma işlemi transaction içinde yürütülmektedir; sipariş, sipariş kalemleri ve stok düşme işlemleri birlikte tamamlanır.

## 5. Yönetim Paneli İsterleri

| İster | Uygulanan bölüm |
| --- | --- |
| Admin girişi | `AdminLoginServlet`, `AdminAuthFilter` |
| Yönetim paneli ana sayfası | `AdminDashboardServlet`, `DashboardStats`, `dashboard.jsp` |
| Kategori yönetimi | `AdminCategoryServlet`, `CategoryDAO` |
| Ürün ve stok yönetimi | `AdminProductServlet`, `ProductDAO` |
| Sipariş yönetimi | `AdminOrderServlet`, `OrderDAO.updateStatus` |
| Kullanıcı listeleme | `AdminUserServlet`, `UserDAO` |

Admin olmayan kullanıcıların yönetim paneline erişimi engellenmiştir. Ürün ve kategori silme işlemlerinde ilişkili kayıtları korumak için gerekli durumlarda doğrudan silme yerine pasife alma yaklaşımı kullanılmıştır.

## 6. Veritabanı Tasarımı

Veritabanı şeması `database/schema.sql` dosyasında bulunmaktadır. Şema dosyası veritabanını, tabloları, foreign key ilişkilerini, indeksleri ve başlangıç verilerini oluşturmaktadır.

| Tablo | Alanlar | Açıklama |
| --- | --- | --- |
| `users` | `id`, `full_name`, `email`, `password`, `phone`, `address`, `role`, `created_at` | Kullanıcı ve admin bilgileri |
| `categories` | `id`, `name`, `description`, `is_active` | Ürün kategorileri |
| `products` | `id`, `category_id`, `name`, `description`, `price`, `stock`, `image_url`, `is_active`, `created_at` | Ürün bilgileri |
| `orders` | `id`, `user_id`, `order_date`, `total_amount`, `status` | Sipariş üst bilgileri |
| `order_items` | `id`, `order_id`, `product_id`, `quantity`, `unit_price`, `subtotal` | Sipariş ürün kalemleri |

Veritabanı ilişki şeması aşağıdaki ERD görselinde gösterilmiştir.

![Veritabanı ilişki şeması](screenshots/19-veritabani-iliski-semasi.png)

## 7. MVC Mimarisi

| Katman | Konum | Açıklama |
| --- | --- | --- |
| Model | `src/main/java/com/ecommerce/model` | Uygulama verisini temsil eden sınıflar |
| DAO | `src/main/java/com/ecommerce/dao` | JDBC sorguları ve veritabanı işlemleri |
| Controller / Servlet | `src/main/java/com/ecommerce/controller` | HTTP istekleri, form işlemleri, session ve yönlendirme |
| Filter | `src/main/java/com/ecommerce/filter` | Encoding, flash mesaj ve admin yetki kontrolü |
| View / JSP | `src/main/webapp/WEB-INF/views` | Kullanıcı ve admin arayüzleri |

## 8. JSTL, Session ve Form Doğrulama

Projede `<c:forEach>`, `<c:if>`, `<c:choose>`, `<fmt:formatNumber>` ve `<fmt:formatDate>` etiketleri kullanılmıştır. Listeleme işlemleri JSP içinde scriptlet kullanılmadan JSTL ile gerçekleştirilmiştir.

Session yönetimi `currentUser`, `adminUser`, `cart`, `flashSuccess` ve `flashError` alanları üzerinden yapılmıştır. Kullanıcı kayıt, giriş, admin giriş, kategori, ürün ve sepet adet güncelleme formlarında sunucu taraflı doğrulama uygulanmıştır.

## 9. Ekran Görüntüleri

Rapor kapsamında uygulamanın kullanıcı ve yönetici tarafındaki temel işlevlerini göstermek amacıyla aşağıdaki ekran görüntüleri alınmıştır. Görseller `docs/screenshots` klasörü altında saklanmaktadır.

![Ana sayfa ve aktif ürün listeleme](screenshots/01-ana-sayfa-urun-listeleme.png)
![Kategoriye göre ürün listeleme](screenshots/02-kategori-filtreleme.png)
![Ürün arama sonucu](screenshots/03-urun-arama.png)
![Ürün detay sayfası ve stok bilgisi](screenshots/04-urun-detay.png)
![Kullanıcı kayıt formu](screenshots/05-kullanici-kayit.png)
![Kullanıcı giriş formu](screenshots/06-kullanici-giris.png)
![Session tabanlı sepet sayfası](screenshots/07-sepet.png)
![Sipariş oluşturma sonrası başarı mesajı](screenshots/08-siparis-basarili.png)
![Kullanıcının kendi siparişleri](screenshots/09-siparislerim.png)
![Sipariş detay sayfası](screenshots/10-siparis-detay.png)
![Admin giriş sayfası](screenshots/11-admin-giris.png)
![Yönetim paneli özet ekranı](screenshots/12-admin-dashboard.png)
![Admin kategori yönetimi](screenshots/13-admin-kategori-yonetimi.png)
![Admin ürün yönetimi](screenshots/14-admin-urun-yonetimi.png)
![Admin ürün ekleme/güncelleme formu](screenshots/15-admin-urun-formu.png)
![Admin sipariş yönetimi](screenshots/16-admin-siparis-yonetimi.png)
![Admin sipariş detay ve durum güncelleme](screenshots/17-admin-siparis-detay.png)
![Admin kullanıcı listeleme](screenshots/18-admin-kullanici-listeleme.png)

## 10. Test ve Doğrulama

Proje `mvn clean package` komutu ile derlenmiş, test edilmiş ve WAR paketi başarıyla oluşturulmuştur.

| Test grubu | Kapsam | Sonuç |
| --- | --- | --- |
| `DaoIntegrationTest` | Şifre doğrulama, kullanıcı kaydı, ürün filtreleme, sepet stok sınırı, sipariş transaction'ı, stok düşme, sipariş durumu ve pasife alma işlemleri | 8/8 başarılı |
| `WebSmokeTest` | Public sayfalar, CSS, arama, kategori filtreleme, kayıt, giriş, sepet, checkout, admin yetki kontrolü, admin CRUD akışları ve sipariş durumu güncelleme | 11/11 başarılı |

Son test sonucu:

```text
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 11. Karşılaşılan Problemler ve Çözümler

Sipariş oluşturma sürecinde stok tutarlılığını korumak için transaction kullanılmıştır. JSP/EL kullanımında `cart.empty` ifadesinin Tomcat üzerinde uyumluluk sorunu oluşturabildiği tespit edildiği için standart EL kullanımı olan `empty cart.items` ifadesi tercih edilmiştir.

## 12. Teslim Bilgileri

| Teslim ölçütü | Durum |
| --- | --- |
| Kaynak kodlar | GitHub bağlantısı: https://github.com/YasinEnginn/java-mvc-ecommerce-portal.git |
| Veritabanı SQL dosyası | `database/schema.sql` |
| Proje raporu | `docs/23060510_rapor.pdf` |
| YouTube anlatım videosu | https://youtu.be/GJYREgF0e7U |
| WAR çıktısı | `target/java-mvc-ecommerce-portal.war` |

## 13. Sonuç

Bu proje ile Java Servlet, JSP, JSTL ve JDBC teknolojileri kullanılarak MVC mimarisine uygun temel bir e-ticaret portalı geliştirilmiştir. Kullanıcı tarafında ürün listeleme, kategori filtreleme, ürün detay görüntüleme, kayıt, giriş, sepet ve sipariş akışları tamamlanmıştır. Yönetici tarafında kategori, ürün, stok, sipariş ve kullanıcı görüntüleme işlemleri uygulanmıştır.
