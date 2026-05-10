SET search_path TO erp_finance_schema;

TRUNCATE erp_finance_schema.rm_stock_movement_tbl;

INSERT INTO erp_finance_schema.rm_stock_movement_tbl
    (movement_id, material_id, movement_type, quantity, unit_cost,
     movement_date, reference_type, reference_id, reason_code, notes)
VALUES
    ('MOV-000001', 'MAT-PB-FG',  'INBOUND',  150.000, 257.00, CURRENT_DATE - 15, 'OPENING', 'OPEN-2026', NULL, 'Opening stock'),
    ('MOV-000002', 'MAT-MG-FG',  'INBOUND',   80.000, 180.00, CURRENT_DATE - 15, 'OPENING', 'OPEN-2026', NULL, 'Opening stock'),
    ('MOV-000003', 'MAT-CA-FG',  'INBOUND',   60.000, 165.00, CURRENT_DATE - 15, 'OPENING', 'OPEN-2026', NULL, 'Opening stock'),
    ('MOV-000004', 'MAT-SB-FG',  'INBOUND',  100.000, 145.00, CURRENT_DATE - 15, 'OPENING', 'OPEN-2026', NULL, 'Opening stock'),
    ('MOV-000005', 'MAT-LBL-1L', 'INBOUND', 5000.000,   2.21, CURRENT_DATE - 15, 'OPENING', 'OPEN-2026', NULL, 'Opening stock'),
    ('MOV-000006', 'MAT-CAP-28', 'INBOUND',24000.000,   0.33, CURRENT_DATE - 15, 'OPENING', 'OPEN-2026', NULL, 'Opening stock'),

    ('MOV-000007', 'MAT-PB-FG',  'INBOUND',   30.000, 257.00, CURRENT_DATE - 10, 'GRN', 'GRN-OPEN-1', NULL, 'Receipt from US Steriles'),
    ('MOV-000008', 'MAT-LBL-1L', 'INBOUND',  250.000,   2.21, CURRENT_DATE - 10, 'GRN', 'GRN-OPEN-2', NULL, 'Receipt from Nixa'),

    ('MOV-000009', 'MAT-PB-FG',  'CONSUMPTION', -8.500,   NULL, CURRENT_DATE - 7, 'PRODUCTION', 'PROD-DAY-7', NULL, NULL),
    ('MOV-000010', 'MAT-MG-FG',  'CONSUMPTION', -3.200,   NULL, CURRENT_DATE - 7, 'PRODUCTION', 'PROD-DAY-7', NULL, NULL),
    ('MOV-000011', 'MAT-CA-FG',  'CONSUMPTION', -2.500,   NULL, CURRENT_DATE - 7, 'PRODUCTION', 'PROD-DAY-7', NULL, NULL),
    ('MOV-000012', 'MAT-LBL-1L', 'CONSUMPTION', -200.000, NULL, CURRENT_DATE - 7, 'PRODUCTION', 'PROD-DAY-7', NULL, NULL),
    ('MOV-000013', 'MAT-CAP-28', 'CONSUMPTION', -250.000, NULL, CURRENT_DATE - 7, 'PRODUCTION', 'PROD-DAY-7', NULL, NULL),

    ('MOV-000014', 'MAT-PB-FG',  'CONSUMPTION', -7.800,   NULL, CURRENT_DATE - 5, 'PRODUCTION', 'PROD-DAY-5', NULL, NULL),
    ('MOV-000015', 'MAT-LBL-1L', 'CONSUMPTION', -180.000, NULL, CURRENT_DATE - 5, 'PRODUCTION', 'PROD-DAY-5', NULL, NULL),
    ('MOV-000016', 'MAT-CAP-28', 'CONSUMPTION', -200.000, NULL, CURRENT_DATE - 5, 'PRODUCTION', 'PROD-DAY-5', NULL, NULL),

    ('MOV-000017', 'MAT-PB-FG',  'CONSUMPTION', -9.000,   NULL, CURRENT_DATE - 2, 'PRODUCTION', 'PROD-DAY-2', NULL, NULL),
    ('MOV-000018', 'MAT-MG-FG',  'CONSUMPTION', -3.500,   NULL, CURRENT_DATE - 2, 'PRODUCTION', 'PROD-DAY-2', NULL, NULL),
    ('MOV-000019', 'MAT-LBL-1L', 'CONSUMPTION', -220.000, NULL, CURRENT_DATE - 2, 'PRODUCTION', 'PROD-DAY-2', NULL, NULL),

    ('MOV-000020', 'MAT-LBL-1L', 'WASTAGE', -15.000, NULL, CURRENT_DATE - 6, 'SCRAP', 'SCRAP-001', 'DAMAGE',   'Roll edge tear'),
    ('MOV-000021', 'MAT-CAP-28', 'WASTAGE', -50.000, NULL, CURRENT_DATE - 4, 'SCRAP', 'SCRAP-002', 'DEFECT',   'Defective batch from supplier'),
    ('MOV-000022', 'MAT-PB-FG',  'WASTAGE',  -0.500, NULL, CURRENT_DATE - 3, 'SCRAP', 'SCRAP-003', 'SPILLAGE', 'Spilled while transferring');

SELECT setval('erp_finance_schema.movement_code_seq', 22, true);

-- Verify
SELECT material_id, SUM(quantity) AS stock_on_hand
FROM erp_finance_schema.rm_stock_movement_tbl
GROUP BY material_id
ORDER BY material_id;