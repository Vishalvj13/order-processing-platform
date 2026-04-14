INSERT INTO inventory_items (product_code, available_quantity, updated_at)
VALUES ('MACBOOK_PRO_14', 10, CURRENT_TIMESTAMP)
ON CONFLICT (product_code) DO NOTHING;

INSERT INTO inventory_items (product_code, available_quantity, updated_at)
VALUES ('AIRPODS_PRO_2', 25, CURRENT_TIMESTAMP)
ON CONFLICT (product_code) DO NOTHING;

INSERT INTO inventory_items (product_code, available_quantity, updated_at)
VALUES ('IPHONE_16', 15, CURRENT_TIMESTAMP)
ON CONFLICT (product_code) DO NOTHING;

INSERT INTO inventory_items (product_code, available_quantity, updated_at)
VALUES ('KINDLE_PAPERWHITE', 20, CURRENT_TIMESTAMP)
ON CONFLICT (product_code) DO NOTHING;
