package com.ecommerce.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecommerce.testutil.SqlScriptRunner;
import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebSmokeTest {
    private static Tomcat tomcat;
    private static URI baseUri;

    @BeforeAll
    static void startTomcat() throws Exception {
        SqlScriptRunner.resetDatabase();

        tomcat = new Tomcat();
        tomcat.setBaseDir(Files.createTempDirectory("ecommerce-tomcat").toString());
        tomcat.setPort(0);
        tomcat.getConnector();

        Context context = tomcat.addWebapp("", Path.of("src", "main", "webapp").toAbsolutePath().toString());
        context.setParentClassLoader(Thread.currentThread().getContextClassLoader());
        WebappLoader loader = new WebappLoader();
        loader.setDelegate(true);
        context.setLoader(loader);

        WebResourceRoot resources = new StandardRoot(context);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                Path.of("target", "test-classes").toAbsolutePath().toString(), "/"));
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                Path.of("target", "classes").toAbsolutePath().toString(), "/"));
        context.setResources(resources);

        tomcat.start();
        baseUri = URI.create("http://localhost:" + tomcat.getConnector().getLocalPort());
    }

    @AfterAll
    static void stopTomcat() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    @BeforeEach
    void resetDatabase() {
        SqlScriptRunner.resetDatabase();
    }

    @Test
    void rendersPublicPagesAndStaticAssets() throws Exception {
        HttpClient client = newClient();

        HttpResponse<String> home = get(client, "/products");
        HttpResponse<String> detail = get(client, "/product?id=3");
        HttpResponse<String> outOfStock = get(client, "/product?id=2");
        HttpResponse<String> css = get(client, "/assets/css/style.css");

        assertEquals(200, home.statusCode());
        assertTrue(home.body().contains("Ürünler"));
        assertTrue(home.body().contains("Nova X Pro Telefon"));
        assertEquals(200, detail.statusCode());
        assertTrue(detail.body().contains("AtlasBook 14 Laptop"));
        assertEquals(200, outOfStock.statusCode());
        assertTrue(outOfStock.body().contains("Stokta yok"));
        assertTrue(outOfStock.body().contains("disabled"));
        assertEquals(200, css.statusCode());
        assertTrue(css.body().contains(":root"));
    }

    @Test
    void filtersProductsByCategoryAndSearchTerm() throws Exception {
        HttpClient client = newClient();

        HttpResponse<String> category = get(client, "/products?categoryId=1");
        HttpResponse<String> search = get(client, "/products?q=java");

        assertEquals(200, category.statusCode());
        assertTrue(category.body().contains("Nova X Pro Telefon"));
        assertTrue(category.body().contains("Pocket Mini Telefon"));
        assertEquals(200, search.statusCode());
        assertTrue(search.body().contains("Java MVC Rehberi"));
        assertTrue(!search.body().contains("Nova X Pro Telefon"));
    }

    @Test
    void redirectsAdminDashboardWhenNotLoggedIn() throws Exception {
        HttpClient client = newClient();

        HttpResponse<String> response = get(client, "/admin/dashboard");

        assertEquals(200, response.statusCode());
        assertTrue(response.uri().getPath().endsWith("/admin/login"));
        assertTrue(response.body().contains("Admin girişi"));
    }

    @Test
    void redirectsCheckoutToLoginWhenCustomerIsNotLoggedIn() throws Exception {
        HttpClient client = newClient();

        HttpResponse<String> response = post(client, "/checkout", Map.of());

        assertEquals(200, response.statusCode());
        assertTrue(response.uri().getPath().endsWith("/login"));
        assertTrue(response.body().contains("Sipariş oluşturmak için giriş yapmalısınız"));
    }

    @Test
    void registersNewCustomerAndRejectsDuplicateEmail() throws Exception {
        HttpClient duplicateClient = newClient();
        HttpResponse<String> duplicate = post(duplicateClient, "/register", Map.of(
                "fullName", "Ayşe Demir",
                "email", "ayse@example.com",
                "password", "demo123",
                "phone", "05551112233",
                "address", "Istanbul"
        ));

        HttpClient newCustomerClient = newClient();
        HttpResponse<String> register = post(newCustomerClient, "/register", Map.of(
                "fullName", "Mehmet Test",
                "email", "mehmet@example.com",
                "password", "secret123",
                "phone", "05553334455",
                "address", "Ankara"
        ));
        HttpResponse<String> login = post(newCustomerClient, "/login", Map.of(
                "email", "mehmet@example.com",
                "password", "secret123"
        ));

        assertEquals(200, duplicate.statusCode());
        assertTrue(duplicate.body().contains("Bu e-posta adresi zaten kayıtlı"));
        assertEquals(200, register.statusCode());
        assertTrue(register.uri().getPath().endsWith("/login"));
        assertTrue(register.body().contains("Kayıt başarılı"));
        assertEquals(200, login.statusCode());
        assertTrue(login.body().contains("Hoş geldiniz, Mehmet Test"));
    }

    @Test
    void customerCanLoginUseCartAndCheckout() throws Exception {
        HttpClient client = newClient();

        HttpResponse<String> login = post(client, "/login", Map.of(
                "email", "ayse@example.com",
                "password", "demo123"
        ));
        HttpResponse<String> cart = post(client, "/cart", Map.of(
                "action", "add",
                "productId", "3",
                "quantity", "2"
        ));
        HttpResponse<String> checkout = post(client, "/checkout", Map.of());

        assertEquals(200, login.statusCode());
        assertTrue(login.body().contains("Hoş geldiniz"));
        assertEquals(200, cart.statusCode());
        assertTrue(cart.body().contains("AtlasBook 14 Laptop"));
        assertEquals(200, checkout.statusCode());
        assertTrue(checkout.body().contains("Siparişiniz başarıyla oluşturuldu"));
        assertTrue(checkout.body().contains("#1"));
    }

    @Test
    void customerCanUpdateAndRemoveCartItems() throws Exception {
        HttpClient client = newClient();

        post(client, "/login", Map.of(
                "email", "ayse@example.com",
                "password", "demo123"
        ));
        post(client, "/cart", Map.of(
                "action", "add",
                "productId", "5",
                "quantity", "5"
        ));
        HttpResponse<String> updated = post(client, "/cart", Map.of(
                "action", "update",
                "productId", "5",
                "quantity", "2"
        ));
        HttpResponse<String> removed = post(client, "/cart", Map.of(
                "action", "remove",
                "productId", "5"
        ));

        assertEquals(200, updated.statusCode());
        assertTrue(updated.body().contains("Kablosuz Kulaklık"));
        assertTrue(updated.body().contains("value=\"2\""));
        assertEquals(200, removed.statusCode());
        assertTrue(removed.body().contains("Sepetiniz boş"));
    }

    @Test
    void customerLoginDoesNotOpenAdminPanel() throws Exception {
        HttpClient client = newClient();
        post(client, "/login", Map.of(
                "email", "ayse@example.com",
                "password", "demo123"
        ));

        HttpResponse<String> dashboard = get(client, "/admin/dashboard");

        assertEquals(200, dashboard.statusCode());
        assertTrue(dashboard.uri().getPath().endsWith("/admin/login"));
    }

    @Test
    void adminCanLoginAndSeeDashboard() throws Exception {
        HttpClient client = newClient();

        HttpResponse<String> login = post(client, "/admin/login", Map.of(
                "email", "admin@portal.test",
                "password", "admin123"
        ));
        HttpResponse<String> dashboard = get(client, "/admin/dashboard");

        assertEquals(200, login.statusCode());
        assertTrue(login.uri().getPath().endsWith("/admin/dashboard"));
        assertEquals(200, dashboard.statusCode());
        assertTrue(dashboard.body().contains("Yönetim paneli"));
        assertTrue(dashboard.body().contains("Toplam ürün"));
    }

    @Test
    void adminCanManageCategoryProductUsersAndOrderStatus() throws Exception {
        HttpClient admin = newClient();
        post(admin, "/admin/login", Map.of(
                "email", "admin@portal.test",
                "password", "admin123"
        ));

        HttpResponse<String> categoryPage = post(admin, "/admin/categories", Map.of(
                "name", "Oyuncak",
                "description", "Test kategorisi",
                "active", "on"
        ));
        int categoryId = new CategoryDAO().findAll().stream()
                .filter(category -> category.getName().equals("Oyuncak"))
                .findFirst()
                .orElseThrow()
                .getId();
        HttpResponse<String> updatedCategory = post(admin, "/admin/categories", Map.of(
                "id", String.valueOf(categoryId),
                "name", "Oyuncak ve Hobi",
                "description", "Guncellenen test kategorisi",
                "active", "on"
        ));

        HttpResponse<String> productPage = post(admin, "/admin/products", Map.of(
                "name", "Test Ürünü",
                "categoryId", String.valueOf(categoryId),
                "description", "Admin tarafından eklenen test ürünü",
                "price", "123.45",
                "stock", "9",
                "imageUrl", "https://example.com/test.jpg",
                "active", "on"
        ));
        int productId = new ProductDAO().findAll().stream()
                .filter(product -> product.getName().equals("Test Ürünü"))
                .findFirst()
                .orElseThrow()
                .getId();
        HttpResponse<String> updatedProduct = post(admin, "/admin/products", Map.of(
                "id", String.valueOf(productId),
                "name", "Test Ürünü Güncel",
                "categoryId", String.valueOf(categoryId),
                "description", "Admin tarafından güncellenen test ürünü",
                "price", "199.90",
                "stock", "11",
                "imageUrl", "https://example.com/test-updated.jpg",
                "active", "on"
        ));

        HttpResponse<String> toggledProduct = post(admin, "/admin/products", Map.of(
                "action", "toggle",
                "id", String.valueOf(productId),
                "active", "false"
        ));
        HttpResponse<String> deletedProduct = post(admin, "/admin/products", Map.of(
                "action", "delete",
                "id", String.valueOf(productId)
        ));
        HttpResponse<String> deletedCategory = post(admin, "/admin/categories", Map.of(
                "action", "delete",
                "id", String.valueOf(categoryId)
        ));
        HttpResponse<String> users = get(admin, "/admin/users");

        HttpClient customer = newClient();
        post(customer, "/login", Map.of(
                "email", "ayse@example.com",
                "password", "demo123"
        ));
        post(customer, "/cart", Map.of(
                "action", "add",
                "productId", "3",
                "quantity", "1"
        ));
        post(customer, "/checkout", Map.of());

        HttpResponse<String> orders = get(admin, "/admin/orders");
        HttpResponse<String> orderDetail = get(admin, "/admin/order-detail?id=1");
        HttpResponse<String> updatedOrder = post(admin, "/admin/orders", Map.of(
                "id", "1",
                "status", "Tamamlandı"
        ));

        assertEquals(200, categoryPage.statusCode());
        assertTrue(categoryPage.body().contains("Oyuncak"));
        assertTrue(categoryPage.body().contains("Kategori eklendi"));
        assertEquals(200, updatedCategory.statusCode());
        assertTrue(updatedCategory.body().contains("Oyuncak ve Hobi"));
        assertTrue(updatedCategory.body().contains("Kategori güncellendi"));
        assertEquals(200, productPage.statusCode());
        assertTrue(productPage.body().contains("Test Ürünü"));
        assertTrue(productPage.body().contains("Ürün eklendi"));
        assertEquals(200, updatedProduct.statusCode());
        assertTrue(updatedProduct.body().contains("Test Ürünü Güncel"));
        assertTrue(updatedProduct.body().contains("Ürün güncellendi"));
        assertEquals(200, toggledProduct.statusCode());
        assertTrue(toggledProduct.body().contains("Ürün durumu güncellendi"));
        assertEquals(200, deletedProduct.statusCode());
        assertTrue(deletedProduct.body().contains("Ürün silindi"));
        assertEquals(200, deletedCategory.statusCode());
        assertTrue(deletedCategory.body().contains("Kategori silindi"));
        assertEquals(200, users.statusCode());
        assertTrue(users.body().contains("Ayşe Demir"));
        assertEquals(200, orders.statusCode());
        assertTrue(orders.body().contains("#1"));
        assertEquals(200, orderDetail.statusCode());
        assertTrue(orderDetail.body().contains("AtlasBook 14 Laptop"));
        assertEquals(200, updatedOrder.statusCode());
        assertTrue(updatedOrder.body().contains("Sipariş durumu güncellendi"));
        assertTrue(updatedOrder.body().contains("Tamamlandı"));
    }

    @Test
    void adminFormsShowServerSideValidationErrors() throws Exception {
        HttpClient admin = newClient();
        post(admin, "/admin/login", Map.of(
                "email", "admin@portal.test",
                "password", "admin123"
        ));

        HttpResponse<String> invalidCategory = post(admin, "/admin/categories", Map.of(
                "name", "",
                "description", "Eksik kategori"
        ));
        HttpResponse<String> invalidProduct = post(admin, "/admin/products", Map.of(
                "name", "",
                "categoryId", "",
                "description", "Eksik urun",
                "price", "0",
                "stock", "-1",
                "imageUrl", ""
        ));

        assertEquals(200, invalidCategory.statusCode());
        assertTrue(invalidCategory.body().contains("Kategori adı boş olamaz"));
        assertEquals(200, invalidProduct.statusCode());
        assertTrue(invalidProduct.body().contains("Ürün adı boş olamaz"));
        assertTrue(invalidProduct.body().contains("Kategori seçilmelidir"));
        assertTrue(invalidProduct.body().contains("Fiyat 0"));
        assertTrue(invalidProduct.body().contains("büyük olmalıdır"));
        assertTrue(invalidProduct.body().contains("Stok miktarı negatif olamaz"));
    }

    private static HttpClient newClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    private static HttpResponse<String> get(HttpClient client, String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve(path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static HttpResponse<String> post(HttpClient client, String path, Map<String, String> form)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve(path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(encode(form)))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static String encode(Map<String, String> form) {
        return form.entrySet().stream()
                .map(entry -> urlEncode(entry.getKey()) + "=" + urlEncode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
