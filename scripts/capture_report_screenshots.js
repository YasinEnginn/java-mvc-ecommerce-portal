const fs = require("fs");
const path = require("path");
const { chromium } = require("playwright");

const baseUrl = "http://localhost:8080/java-mvc-ecommerce-portal";
const outDir = path.join(__dirname, "..", "docs", "screenshots");

const screenshots = [
  ["01-ana-sayfa-urun-listeleme.png", "Ana sayfa ve aktif ürün listeleme"],
  ["02-kategori-filtreleme.png", "Kategoriye göre ürün listeleme"],
  ["03-urun-arama.png", "Ürün arama sonucu"],
  ["04-urun-detay.png", "Ürün detay sayfası ve stok bilgisi"],
  ["05-kullanici-kayit.png", "Kullanıcı kayıt formu"],
  ["06-kullanici-giris.png", "Kullanıcı giriş formu"],
  ["07-sepet.png", "Session tabanlı sepet sayfası"],
  ["08-siparis-basarili.png", "Sipariş oluşturma sonrası başarı mesajı"],
  ["09-siparislerim.png", "Kullanıcının kendi siparişleri"],
  ["10-siparis-detay.png", "Sipariş detay sayfası"],
  ["11-admin-giris.png", "Admin giriş sayfası"],
  ["12-admin-dashboard.png", "Yönetim paneli özet ekranı"],
  ["13-admin-kategori-yonetimi.png", "Admin kategori yönetimi"],
  ["14-admin-urun-yonetimi.png", "Admin ürün yönetimi"],
  ["15-admin-urun-formu.png", "Admin ürün ekleme/güncelleme formu"],
  ["16-admin-siparis-yonetimi.png", "Admin sipariş yönetimi"],
  ["17-admin-siparis-detay.png", "Admin sipariş detay ve durum güncelleme"],
  ["18-admin-kullanici-listeleme.png", "Admin kullanıcı listeleme"],
];

async function goto(page, url) {
  await page.goto(url, { waitUntil: "domcontentloaded", timeout: 30000 });
  await page.waitForTimeout(900);
}

async function shot(page, fileName) {
  await page.screenshot({
    path: path.join(outDir, fileName),
    fullPage: false,
  });
}

async function clickFirstIfExists(page, selector) {
  const item = page.locator(selector).first();
  if ((await item.count()) > 0) {
    await item.click();
    await page.waitForLoadState("domcontentloaded");
    await page.waitForTimeout(700);
    return true;
  }
  return false;
}

(async () => {
  fs.mkdirSync(outDir, { recursive: true });

  const browser = await chromium.launch({
    channel: "msedge",
    headless: true,
  });

  const context = await browser.newContext({
    viewport: { width: 1440, height: 900 },
    locale: "tr-TR",
  });
  const page = await context.newPage();

  await goto(page, `${baseUrl}/products`);
  await shot(page, "01-ana-sayfa-urun-listeleme.png");

  await goto(page, `${baseUrl}/products?categoryId=1`);
  await shot(page, "02-kategori-filtreleme.png");

  await goto(page, `${baseUrl}/products?q=java`);
  await shot(page, "03-urun-arama.png");

  await goto(page, `${baseUrl}/product?id=1`);
  await shot(page, "04-urun-detay.png");

  await goto(page, `${baseUrl}/register`);
  await shot(page, "05-kullanici-kayit.png");

  await goto(page, `${baseUrl}/login`);
  await shot(page, "06-kullanici-giris.png");

  await page.locator('input[name="email"]').fill("ayse@example.com");
  await page.locator('input[name="password"]').fill("demo123");
  await Promise.all([
    page.waitForNavigation({ waitUntil: "domcontentloaded" }),
    page.locator('form.stack-form button[type="submit"]').click(),
  ]);
  await page.waitForTimeout(700);

  await goto(page, `${baseUrl}/product?id=1`);
  await Promise.all([
    page.waitForNavigation({ waitUntil: "domcontentloaded" }),
    page.locator('form.buy-form button[type="submit"]').click(),
  ]);
  await page.waitForTimeout(700);
  await shot(page, "07-sepet.png");

  await Promise.all([
    page.waitForNavigation({ waitUntil: "domcontentloaded" }),
    page.locator('form[action$="/checkout"] button[type="submit"]').click(),
  ]);
  await page.waitForTimeout(900);
  await shot(page, "08-siparis-basarili.png");

  await goto(page, `${baseUrl}/my-orders`);
  await shot(page, "09-siparislerim.png");

  await clickFirstIfExists(page, 'a[href*="order-detail"]');
  await shot(page, "10-siparis-detay.png");

  await context.close();

  const adminContext = await browser.newContext({
    viewport: { width: 1440, height: 900 },
    locale: "tr-TR",
  });
  const adminPage = await adminContext.newPage();

  await goto(adminPage, `${baseUrl}/admin/login`);
  await shot(adminPage, "11-admin-giris.png");

  await adminPage.locator('input[name="email"]').fill("admin@portal.test");
  await adminPage.locator('input[name="password"]').fill("admin123");
  await Promise.all([
    adminPage.waitForNavigation({ waitUntil: "domcontentloaded" }),
    adminPage.locator('form.stack-form button[type="submit"]').click(),
  ]);
  await adminPage.waitForTimeout(700);
  await shot(adminPage, "12-admin-dashboard.png");

  await goto(adminPage, `${baseUrl}/admin/categories`);
  await shot(adminPage, "13-admin-kategori-yonetimi.png");

  await goto(adminPage, `${baseUrl}/admin/products`);
  await shot(adminPage, "14-admin-urun-yonetimi.png");

  await goto(adminPage, `${baseUrl}/admin/products?action=new`);
  await shot(adminPage, "15-admin-urun-formu.png");

  await goto(adminPage, `${baseUrl}/admin/orders`);
  await shot(adminPage, "16-admin-siparis-yonetimi.png");

  await clickFirstIfExists(adminPage, 'a[href*="admin/order-detail"]');
  await shot(adminPage, "17-admin-siparis-detay.png");

  await goto(adminPage, `${baseUrl}/admin/users`);
  await shot(adminPage, "18-admin-kullanici-listeleme.png");

  await adminContext.close();
  await browser.close();

  fs.writeFileSync(
    path.join(outDir, "manifest.json"),
    JSON.stringify(
      screenshots.map(([file, title]) => ({ file, title })),
      null,
      2
    ),
    "utf8"
  );

  console.log(`Captured ${screenshots.length} screenshots in ${outDir}`);
})().catch((error) => {
  console.error(error);
  process.exit(1);
});
