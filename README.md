# Java MVC E-Commerce Portal

A classic Java web application built with **Servlet**, **JSP**, **JSTL**, **JDBC**, **MySQL**, and the **MVC pattern**. The project contains a customer-facing e-commerce storefront and a role-protected admin panel.

This README is written as a complete setup guide for someone who is running the project for the first time.

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Step 1: Check Your Tools](#step-1-check-your-tools)
- [Step 2: Prepare the Database](#step-2-prepare-the-database)
- [Step 3: Configure Database Connection](#step-3-configure-database-connection)
- [Step 4: Run Automated Tests](#step-4-run-automated-tests)
- [Step 5: Build the WAR File](#step-5-build-the-war-file)
- [Step 6: Deploy to Apache Tomcat](#step-6-deploy-to-apache-tomcat)
- [Step 7: Open the Application](#step-7-open-the-application)
- [Default Users](#default-users)
- [Main Application URLs](#main-application-urls)
- [Running from an IDE](#running-from-an-ide)
- [Report PDF Generation](#report-pdf-generation)
- [Troubleshooting](#troubleshooting)

## Features

### Customer Side

- List active products on the storefront
- Filter products by category
- Search products by name or description
- View product detail
- See stock status
- Register as a customer
- Log in and log out
- Add products to a session-based cart
- Update product quantity in the cart
- Remove products from the cart
- Create an order from the cart
- Prevent checkout when the user is not logged in
- Prevent ordering more items than available stock
- View personal order history
- View personal order details

### Admin Side

- Admin login
- Role-based admin panel protection
- Dashboard statistics
- List users
- Create, update, delete, or deactivate categories
- Create, update, delete, or deactivate products
- Manage product stock
- List all orders
- View order details
- Update order status

### Technical Features

- MVC package organization
- JSP views with JSTL
- JDBC DAO layer
- MySQL schema and seed data
- PBKDF2 password hashing
- Transaction-safe order creation
- Automated DAO tests
- Embedded Tomcat web smoke tests
- PDF report generation script

## Project Structure

```text
java-mvc-ecommerce-portal/
|-- database/
|   `-- schema.sql
|-- docs/
|   |-- PROJE_RAPORU.md
|   |-- YOUTUBE_VIDEO_CIZELGESI.md
|   `-- 23060510_rapor.pdf
|-- scripts/
|   `-- build_report_pdf.py
|-- src/
|   |-- main/
|   |   |-- java/com/ecommerce/
|   |   |   |-- config/
|   |   |   |-- controller/
|   |   |   |-- dao/
|   |   |   |-- filter/
|   |   |   |-- model/
|   |   |   `-- util/
|   |   |-- resources/
|   |   |   `-- database.properties
|   |   `-- webapp/
|   |       |-- assets/css/style.css
|   |       |-- index.jsp
|   |       `-- WEB-INF/views/
|   `-- test/
|       |-- java/com/ecommerce/
|       `-- resources/database.properties
|-- pom.xml
`-- README.md
```

## Requirements

Install these tools before running the project:

| Tool | Required version | Purpose |
| --- | --- | --- |
| Java JDK | 17 or newer | Compile and run the Java code |
| Maven | 3.9 or newer | Download dependencies, run tests, build WAR |
| MySQL | 8 or compatible | Runtime database |
| Apache Tomcat | 10.1 or newer | Deploy and run the web application |
| Python | 3.10 or newer | Generate the PDF report |

Important: this project uses **Jakarta Servlet 6**. Use **Tomcat 10+**. Tomcat 9 uses the older `javax.servlet` namespace and will not run this project correctly.

## Step 1: Check Your Tools

Open a terminal in the project folder.

On Windows PowerShell:

```powershell
cd C:\Users\YourName\Desktop\java-mvc-ecommerce-portal
```

Check Java:

```bash
java -version
javac -version
```

Expected: Java 17 or newer.

Check Maven:

```bash
mvn -version
```

Expected: Maven 3.9 or newer.

Check MySQL:

```bash
mysql --version
```

If `mysql` is not recognized, MySQL may still be installed but not added to PATH. In that case, use MySQL Workbench or add MySQL's `bin` directory to your PATH.

## Step 2: Prepare the Database

The project includes the full SQL setup file:

```text
database/schema.sql
```

This file creates:

- `ecommerce_portal` database
- `users` table
- `categories` table
- `products` table
- `orders` table
- `order_items` table
- Indexes
- Seed admin/customer users
- Seed categories and products

### Option A: Using MySQL Command Line

Start MySQL, then run this command from the project root:

```bash
mysql -u root -p < database/schema.sql
```

Enter your MySQL password when asked.

If your root user has no password:

```bash
mysql -u root < database/schema.sql
```

### Option B: Using MySQL Workbench

1. Open MySQL Workbench.
2. Connect to your local MySQL server.
3. Open `database/schema.sql`.
4. Run the full script.
5. Confirm that the `ecommerce_portal` database appears in the schemas list.

### Verify the Database

Run:

```sql
USE ecommerce_portal;
SHOW TABLES;
SELECT id, full_name, email, role FROM users;
SELECT id, name, price, stock FROM products;
```

You should see the seeded admin and customer users, plus sample products.

## Step 3: Configure Database Connection

Runtime database settings are stored here:

```text
src/main/resources/database.properties
```

Default content:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/ecommerce_portal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
db.username=root
db.password=
```

If your MySQL password is `123456`, update it like this:

```properties
db.username=root
db.password=123456
```

If your MySQL runs on another port, update the URL. For example, port `3307`:

```properties
db.url=jdbc:mysql://localhost:3307/ecommerce_portal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
```

You can also override the same values with environment variables:

```bash
DB_USERNAME=root
DB_PASSWORD=your_password
DB_URL=jdbc:mysql://localhost:3306/ecommerce_portal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
```

Or with JVM system properties:

```bash
mvn clean package -Ddb.username=root -Ddb.password=your_password
```

## Step 4: Run Automated Tests

Before deploying the project, run the tests:

```bash
mvn test
```

The test suite does not use your MySQL database. It uses H2 in MySQL compatibility mode, so it is safe to run repeatedly.

Expected result:

```text
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Test reports are generated in:

```text
target/surefire-reports/
```

## Step 5: Build the WAR File

Create the deployable WAR package:

```bash
mvn clean package
```

When the build succeeds, the WAR file is created here:

```text
target/java-mvc-ecommerce-portal.war
```

## Step 6: Deploy to Apache Tomcat

### Option A: Deploy by Copying the WAR File

1. Stop Tomcat if it is already running.
2. Copy this file:

   ```text
   target/java-mvc-ecommerce-portal.war
   ```

3. Paste it into Tomcat's `webapps` folder.

Example Windows path:

```text
C:\apache-tomcat-10.1.x\webapps\
```

4. Start Tomcat.

Tomcat will automatically extract the WAR into:

```text
webapps/java-mvc-ecommerce-portal/
```

### Option B: Deploy as ROOT Application

If you want the project to run directly under `http://localhost:8080`, rename the WAR:

```text
ROOT.war
```

Then copy it to Tomcat's `webapps` folder.

## Step 7: Open the Application

If deployed with the default WAR name:

```text
http://localhost:8080/java-mvc-ecommerce-portal/products
```

If deployed as `ROOT.war`:

```text
http://localhost:8080/products
```

## Default Users

The SQL seed data creates these users:

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@portal.test` | `admin123` |
| Customer | `ayse@example.com` | `demo123` |

Use the admin account for:

```text
/admin/login
```

Use the customer account for:

```text
/login
```

## Main Application URLs

When the app is deployed as `java-mvc-ecommerce-portal.war`, prefix each path with:

```text
http://localhost:8080/java-mvc-ecommerce-portal
```

| Page | Path |
| --- | --- |
| Storefront | `/products` |
| Product detail | `/product?id=1` |
| Cart | `/cart` |
| Login | `/login` |
| Register | `/register` |
| My orders | `/my-orders` |
| Order detail | `/order-detail?id=1` |
| Admin login | `/admin/login` |
| Admin dashboard | `/admin/dashboard` |
| Admin categories | `/admin/categories` |
| Admin products | `/admin/products` |
| Admin orders | `/admin/orders` |
| Admin order detail | `/admin/order-detail?id=1` |
| Admin users | `/admin/users` |

## Running from an IDE

You can run the project from IntelliJ IDEA, Eclipse, or NetBeans.

### IntelliJ IDEA

1. Open the project folder.
2. Let IntelliJ import the Maven project.
3. Make sure the Project SDK is Java 17 or newer.
4. Add a Tomcat 10+ run configuration.
5. Add the WAR artifact or exploded WAR artifact.
6. Make sure MySQL is running.
7. Run `database/schema.sql` before starting the app.
8. Start the Tomcat run configuration.
9. Open `/products` in your browser.

### Eclipse

1. Import the project as an existing Maven project.
2. Install or configure Apache Tomcat 10+ in the Servers view.
3. Add the project to the Tomcat server.
4. Make sure MySQL is running.
5. Run `database/schema.sql`.
6. Start the server.
7. Open `/products` in your browser.

## Test Coverage

The test suite includes DAO-level and web-level checks.

### `DaoIntegrationTest`

Covers:

- Password verification
- User creation
- Product filtering
- Cart stock limit
- Order creation transaction
- Stock decrease after order
- Order status update
- Safe delete/deactivate behavior

### `WebSmokeTest`

Covers:

- Public pages
- Static CSS loading
- Product search
- Category filtering
- Out-of-stock product detail
- Registration
- Duplicate email handling
- Login
- Cart add/update/remove
- Checkout
- Admin access control
- Admin dashboard
- Category/product create-update-delete flows
- Admin form validation errors
- User listing
- Order status update

## Report PDF Generation

The report source file is:

```text
docs/PROJE_RAPORU.md
```

A final PDF is generated here:

```text
docs/23060510_rapor.pdf
```

To regenerate the final PDF:

```bash
python scripts/build_report_pdf.py docs/23060510_rapor.pdf --student-no 23060510 --name "Yasin Engin" --github "https://github.com/YasinEnginn/java-mvc-ecommerce-portal.git" --youtube "https://youtu.be/GJYREgF0e7U"
```

The final PDF file name must follow this format:

```text
studentnumber_rapor.pdf
```

## YouTube Video Plan

The timestamped video explanation plan is here:

```text
docs/YOUTUBE_VIDEO_CIZELGESI.md
```

The video should:

- Stay under 10 minutes
- Show the presenter's face/camera
- Be divided into timestamped requirement sections
- Show the related code for each requirement
- Show the running application output for each requirement
- Include only working features
- Mention the GitHub repository and report PDF

## Troubleshooting

### `mvn` is not recognized

Maven is not installed or not added to PATH.

Fix:

1. Install Apache Maven.
2. Add Maven's `bin` directory to PATH.
3. Open a new terminal.
4. Run:

   ```bash
   mvn -version
   ```

### `java` or `javac` is not recognized

Java JDK is not installed or not added to PATH.

Fix:

1. Install JDK 17 or newer.
2. Set `JAVA_HOME`.
3. Add `%JAVA_HOME%\bin` to PATH.
4. Open a new terminal.
5. Run:

   ```bash
   java -version
   javac -version
   ```

### Database connection fails

Common causes:

- MySQL is not running.
- `database/schema.sql` has not been executed.
- Wrong username or password in `database.properties`.
- MySQL is running on a different port.

Check:

```bash
mysql -u root -p
```

Then:

```sql
SHOW DATABASES;
USE ecommerce_portal;
SHOW TABLES;
```

### Login does not work

Make sure the seed data exists:

```sql
SELECT email, role FROM users;
```

Expected users:

```text
admin@portal.test
ayse@example.com
```

### Admin panel redirects to login

Only users with role `ADMIN` can access `/admin/*`.

Use:

```text
admin@portal.test / admin123
```

### Tomcat shows 404

Check the URL.

If the WAR name is `java-mvc-ecommerce-portal.war`, use:

```text
http://localhost:8080/java-mvc-ecommerce-portal/products
```

If deployed as `ROOT.war`, use:

```text
http://localhost:8080/products
```

### Tomcat 9 does not work

Use Tomcat 10+. This project uses Jakarta packages such as:

```text
jakarta.servlet.*
```

Tomcat 9 expects:

```text
javax.servlet.*
```

They are not compatible.

### Port 8080 is already in use

Another app is using port 8080.

Fix:

1. Stop the other app.
2. Or change Tomcat's port in `conf/server.xml`.
3. Restart Tomcat.

### Product images do not load

Seed products use external image URLs. If images do not load, check your internet connection or update `image_url` values from the admin product form.

## Notes

- Spring Boot, Hibernate, JPA, React, Angular, and Vue are intentionally not used.
- The cart is stored in the HTTP session.
- Checkout is protected: anonymous users are redirected to login.
- Order creation uses a database transaction and stock checks.
- Referenced products and categories are deactivated instead of being deleted when deletion would break historical data.
