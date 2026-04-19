-- IPOS-CA DATABASE
-- InfoPharma Ordering System - Client Application
-- Team 11 - Group 4
-- Updated: i added fixes from the marking sheet and sample data stuff

-- Drop and recreate
DROP DATABASE IF EXISTS ipos_ca;
CREATE DATABASE ipos_ca;
USE ipos_ca;

-- TABLE 1: USERS (IPOS-CA-USER package)

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Pharmacist', 'Manager') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- TABLE 2: MERCHANT_CONFIG (System configuration)
-- fixed: VAT default changed to 0% and markup to 100% as seen in sample data (Cosymed Ltd: 0% VAT, 100% markup)

CREATE TABLE merchant_config (
    config_id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_name VARCHAR(100) NOT NULL,
    merchant_address TEXT,
    merchant_phone VARCHAR(20),
    merchant_email VARCHAR(100),
    merchant_logo_path VARCHAR(255),
    vat_rate DECIMAL(5,4) NOT NULL DEFAULT 0.0000,     -- 0% VAT (sample data spec)
    markup_rate DECIMAL(5,4) NOT NULL DEFAULT 1.0000,   -- 100% markup (sample data spec)
    low_stock_threshold INT DEFAULT 10,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by INT,
    FOREIGN KEY (updated_by) REFERENCES users(user_id)
);

-- TABLE 3: STOCK_ITEMS (IPOS-CA-Stock package)

CREATE TABLE stock_items (
    product_id VARCHAR(20) PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    unit_type VARCHAR(20),
    form VARCHAR(20),
    pack_size INT,
    wholesale_cost DECIMAL(10,2) NOT NULL,
    retail_price DECIMAL(10,2) NOT NULL,
    current_stock INT NOT NULL DEFAULT 0,
    reorder_level INT DEFAULT 10,
    supplier_code VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- TABLE 4: DISCOUNT_PLANS (IPOS-CA-CUST package)

CREATE TABLE discount_plans (
    discount_plan_id INT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(50) NOT NULL,
    plan_type ENUM('FIXED', 'FLEXIBLE') NOT NULL,
    discount_percentage DECIMAL(5,2),
    min_purchase_amount DECIMAL(10,2),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- TABLE 4b: DISCOUNT_TIERS
-- Stores the tiered rates for flexible discount plans
-- e.g. <£100: 0%, £100-£300: 1%, £300+: 2%

CREATE TABLE discount_tiers (
    tier_id INT AUTO_INCREMENT PRIMARY KEY,
    discount_plan_id INT NOT NULL,
    min_value DECIMAL(10,2) NOT NULL,
    max_value DECIMAL(10,2),           -- NULL means no upper limit
    discount_rate DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (discount_plan_id) REFERENCES discount_plans(discount_plan_id) ON DELETE CASCADE
);

-- TABLE 5: ACCOUNT_HOLDERS (IPOS-CA-CUST package)

CREATE TABLE account_holders (
    account_id VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    credit_limit DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    current_balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    account_status ENUM('Normal', 'Suspended', 'In Default') DEFAULT 'Normal',
    discount_plan_id INT,
    status_1st_reminder ENUM('no_need', 'due', 'sent') DEFAULT 'no_need',
    status_2nd_reminder ENUM('no_need', 'due', 'sent') DEFAULT 'no_need',
    date_1st_reminder DATE NULL,
    date_2nd_reminder DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (discount_plan_id) REFERENCES discount_plans(discount_plan_id),
    INDEX idx_status (account_status),
    INDEX idx_balance (current_balance),
    INDEX idx_reminders (status_1st_reminder, status_2nd_reminder)
);

-- TABLE 6: SALES (IPOS-CA-Sales package)

CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_reference VARCHAR(30) UNIQUE NOT NULL,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    customer_type ENUM('Account Holder', 'Occasional Customer') NOT NULL,
    account_id VARCHAR(20) NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    vat_amount DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('Cash', 'Card', 'Credit') NOT NULL,
    payment_status ENUM('Paid', 'Pending', 'Failed') DEFAULT 'Paid',
    served_by INT NOT NULL,
    notes TEXT,
    FOREIGN KEY (account_id) REFERENCES account_holders(account_id),
    FOREIGN KEY (served_by) REFERENCES users(user_id)
);

-- TABLE 7: SALE_ITEMS

CREATE TABLE sale_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES stock_items(product_id)
);

-- TABLE 8: PAYMENT_RECORDS

CREATE TABLE payment_records (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    payment_method ENUM('Cash', 'Card', 'Credit') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    card_type VARCHAR(20),
    card_first_four CHAR(4),
    card_last_four CHAR(4),
    card_expiry_month INT,
    card_expiry_year INT,
    authorization_code VARCHAR(50),
    reduces_balance BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
);

-- TABLE 9: ORDERS_TO_INFOPHARMA (IPOS-CA-ORD package)

CREATE TABLE orders_to_infopharma (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_reference VARCHAR(30) UNIQUE NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_status ENUM('Draft', 'Submitted', 'Accepted', 'Processing', 'Dispatched', 'Delivered', 'Cancelled') DEFAULT 'Draft',
    total_amount DECIMAL(10,2),
    placed_by INT NOT NULL,
    dispatch_date DATE NULL,
    delivery_date DATE NULL,
    courier VARCHAR(50),
    tracking_number VARCHAR(100),
    notes TEXT,
    FOREIGN KEY (placed_by) REFERENCES users(user_id)
);

-- TABLE 10: ORDER_ITEMS

CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    item_id VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,
    total_cost DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders_to_infopharma(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES stock_items(product_id)
);

-- TABLE 11: REMINDERS (IPOS-CA-CUST package)

CREATE TABLE reminders (
    reminder_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(20) NOT NULL,
    reminder_type ENUM('1st Reminder', '2nd Reminder') NOT NULL,
    amount_owed DECIMAL(10,2) NOT NULL,
    reminder_text TEXT NOT NULL,
    date_generated DATE NOT NULL,
    date_sent DATE,
    sent_via ENUM('Print', 'Email') DEFAULT 'Print',
    status ENUM('Generated', 'Sent', 'Failed') DEFAULT 'Generated',
    FOREIGN KEY (account_id) REFERENCES account_holders(account_id)
);

-- TABLE 12: REMINDER_TEMPLATES (IPOS-CA-Templates package)
-- new table i had to add, required by marking sheet
-- Stores editable templates for 1st and 2nd reminder letters

CREATE TABLE reminder_templates (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    template_type ENUM('1st Reminder', '2nd Reminder') NOT NULL UNIQUE,
    subject VARCHAR(255) NOT NULL,
    body_text TEXT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by INT,
    FOREIGN KEY (updated_by) REFERENCES users(user_id)
);

-- TABLE 13: ONLINE_ORDERS (Integration with IPOS-PU)
-- fixes: fulfillment_status updated to match demo checklist: "received", "ready for shipment", "dispatched", "delivered", added delivery_address column (required by marking sheet)

CREATE TABLE online_orders (
    online_order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_reference VARCHAR(30) UNIQUE NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    customer_email VARCHAR(100),
    delivery_address TEXT,                              -- required for demo
    items_json TEXT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('Pending', 'Paid', 'Failed') DEFAULT 'Paid',
    fulfillment_status ENUM(                           -- matches marking sheet statuses now
        'Received',
        'Ready for Shipment',
        'Dispatched',
        'Delivered',
        'Failed'
    ) DEFAULT 'Received',
    stock_deducted BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP NULL,
    processed_by INT,
    FOREIGN KEY (processed_by) REFERENCES users(user_id)
);

-- TABLE 14: ACCOUNT_HOLDER_PAYMENTS
-- Records payments made by account holders to reduce balance
-- Needed for: "restore account to normal after payment" logic

CREATE TABLE account_holder_payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('Cash', 'Card', 'Bank Transfer') NOT NULL,
    card_type VARCHAR(20),
    card_first_four CHAR(4),
    card_last_four CHAR(4),
    card_expiry_month INT,
    card_expiry_year INT,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    recorded_by INT NOT NULL,
    notes TEXT,
    FOREIGN KEY (account_id) REFERENCES account_holders(account_id),
    FOREIGN KEY (recorded_by) REFERENCES users(user_id)
);


-- SEED DATA - Based on IPOS_SampleData_2026.pdf
-- need to load this before the demo (after clearing all tables)

-- Users (from sample data - Cosymed Ltd CA logins)
INSERT INTO users (username, password_hash, role, full_name, is_active) VALUES
('sysdba',    'masterkey',    'Admin',      'System Administrator',  TRUE),
('manager',   'Get_it_done',  'Manager',    'Operations Manager',    TRUE),
('accountant','Count_money',  'Pharmacist', 'Senior Accountant',     TRUE),
('clerk',     'Paperwork',    'Pharmacist', 'Accounts Clerk',        TRUE);
-- NOTE: In production store bcrypt hashes, not plaintext. These are plaintext to match the demo sample data exactly.

-- Merchant config for Cosymed Ltd
INSERT INTO merchant_config (merchant_name, merchant_address, merchant_phone, vat_rate, markup_rate) VALUES
('Cosymed Ltd', '25, Bond Street, London WC1V 8LS', '0207 321 8001', 0.0000, 1.0000);

-- Discount plans for account holders
INSERT INTO discount_plans (plan_name, plan_type, discount_percentage, description) VALUES
('Fixed 3%', 'FIXED', 3.00, 'Fixed 3% discount on all purchases');

INSERT INTO discount_plans (plan_name, plan_type, description) VALUES
('Variable Volume', 'FLEXIBLE', 'Volume-based: 0% under £100, 1% £100-£300, 2% over £300');

-- Discount tiers for discount_plan_id 2 (Variable Volume)
INSERT INTO discount_tiers (discount_plan_id, min_value, max_value, discount_rate) VALUES
(2, 0.00,   99.99,  0.00),
(2, 100.00, 299.99, 1.00),
(2, 300.00, NULL,   2.00);

-- Account holders (from sample data - Cosymed Ltd customers)
INSERT INTO account_holders (account_id, full_name, address, phone, credit_limit, current_balance, account_status, discount_plan_id) VALUES
('ACC0001', 'Ms Eva Bauyer',      '1, Liverpool Street, London EC2V 8NS', '0207 321 8001', 500.00, 0.00, 'Normal', 1),
('ACC0002', 'Mr Glynne Morrison', '1, Liverpool Street, London EC2V 8NS', '0207 321 8001', 500.00, 0.00, 'Normal', 2);

-- Stock items (from sample data - Cosymed Ltd inventory)
-- Retail price = wholesale_cost * 2 (100% markup, 0% VAT)
INSERT INTO stock_items (product_id, product_name, unit_type, form, pack_size, wholesale_cost, retail_price, current_stock, reorder_level) VALUES
('10000001', 'Paracetamol',            'box',    'Caps', 20, 0.10,  0.20,  121, 10),
('10000002', 'Aspirin',                'box',    'Caps', 20, 0.50,  1.00,  201, 15),
('10000003', 'Analgin',                'box',    'Caps', 10, 1.20,  2.40,  25,  10),
('10000004', 'Celebrex, caps 100 mg',  'box',    'Caps', 10, 10.00, 20.00, 43,  10),
('10000005', 'Celebrex, caps 200 mg',  'box',    'Caps', 10, 18.50, 37.00, 35,  5),
('10000006', 'Retin-A Tretin, 30 g',   'box',    'Caps', 20, 25.00, 50.00, 28,  10),
('10000007', 'Lipitor TB, 20 mg',      'box',    'Caps', 30, 15.50, 31.00, 10,  10),
('10000008', 'Claritin CR, 60g',       'box',    'Caps', 20, 19.50, 39.00, 21,  10),
('20000004', 'Iodine tincture',        'bottle', 'ml',  100, 0.30,  0.60,  35,  10),
('20000005', 'Rhynol',                 'bottle', 'ml',  200, 2.50,  5.00,  14,  15),
('30000001', 'Ospen',                  'box',    'Caps', 20, 10.50, 21.00, 78,  10),
('30000002', 'Amopen',                 'box',    'Caps', 30, 15.00, 30.00, 90,  15),
('40000001', 'Vitamin C',              'box',    'Caps', 30, 1.20,  2.40,  22,  15),
('40000002', 'Vitamin B12',            'box',    'Caps', 30, 1.30,  2.60,  43,  15);

-- Default reminder templates (IPOS-CA-Templates package)
INSERT INTO reminder_templates (template_type, subject, body_text) VALUES
('1st Reminder',
 'REMINDER - Outstanding Balance on Your Account',
 'Dear {customer_name},\n\nREMINDER - ACCOUNT NO.: {account_id}\nTotal Amount Outstanding: £{amount_owed}\n\nAccording to our records, it appears that we have not yet received payment for goods purchased from {merchant_name}.\n\nWe would appreciate payment in full by {payment_due_date}.\n\nIf you have already sent a payment to us recently, please accept our apologies.\n\nYours sincerely,\n{merchant_name}'),
('2nd Reminder',
 'SECOND REMINDER - Outstanding Balance on Your Account',
 'Dear {customer_name},\n\nSECOND REMINDER - ACCOUNT NO.: {account_id}\nTotal Amount Outstanding: £{amount_owed}\n\nIt appears that we still have not received payment for goods purchased from {merchant_name}, despite the reminder sent to you on {first_reminder_date}.\n\nWe would appreciate it if you would settle this account in full by {payment_due_date}.\n\nIf you have already sent a payment to us recently, please accept our apologies.\n\nYours sincerely,\n{merchant_name}');