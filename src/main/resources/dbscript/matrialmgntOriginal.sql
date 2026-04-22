-- ============================================================================
-- RAW MATERIAL MANAGEMENT SYSTEM - SQL DDL SCRIPT
-- ============================================================================
-- Database: raw_material_db
-- Last Updated: 2026-04-10
-- ============================================================================

-- Drop existing database/tables (optional - for fresh setup)
-- DROP DATABASE IF EXISTS raw_material_db;
-- CREATE DATABASE raw_material_db;
-- USE raw_material_db;

-- ============================================================================
-- 1. CORE TABLES
-- ============================================================================

-- ============================================================================
-- Table: RawMaterialCategory
-- Description: Categories for organizing raw materials
-- ============================================================================
CREATE TABLE RawMaterialCategory (
    CategoryID INT PRIMARY KEY AUTO_INCREMENT,
    CategoryName VARCHAR(100) NOT NULL UNIQUE,
    Description TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: Supplier
-- Description: Supplier information and contact details
-- ============================================================================
CREATE TABLE Supplier (
    SupplierID INT PRIMARY KEY AUTO_INCREMENT,
    SupplierName VARCHAR(255) NOT NULL,
    ContactPerson VARCHAR(100),
    ContactEmail VARCHAR(100) UNIQUE,
    PhoneNumber VARCHAR(20),
    Address VARCHAR(255),
    City VARCHAR(100),
    State VARCHAR(100),
    ZipCode VARCHAR(20),
    Country VARCHAR(100),
    LeadTimeDays INT DEFAULT 7,
    PaymentTerms VARCHAR(100),
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: RawMaterial
-- Description: Master table for raw materials
-- ============================================================================
CREATE TABLE RawMaterial (
    RawMaterialID INT PRIMARY KEY AUTO_INCREMENT,
    MaterialName VARCHAR(255) NOT NULL,
    Description TEXT,
    CategoryID INT NOT NULL,
    UnitOfMeasure VARCHAR(50) NOT NULL,
    ReorderLevel INT DEFAULT 100,
    SafetyStockLevel INT DEFAULT 50,
    PreferredSupplierID INT,
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (CategoryID) REFERENCES RawMaterialCategory(CategoryID) ON DELETE RESTRICT,
    FOREIGN KEY (PreferredSupplierID) REFERENCES Supplier(SupplierID) ON DELETE SET NULL,
    UNIQUE KEY uk_material_name (MaterialName),
    INDEX idx_category (CategoryID),
    INDEX idx_supplier (PreferredSupplierID),
    INDEX idx_material_active (IsActive)
);

-- ============================================================================
-- Table: Location (Warehouse/Storage)
-- Description: Physical storage locations
-- ============================================================================
CREATE TABLE Location (
    LocationID INT PRIMARY KEY AUTO_INCREMENT,
    LocationName VARCHAR(100) NOT NULL UNIQUE,
    LocationDescription TEXT,
    WarehouseSection VARCHAR(50),
    Capacity DECIMAL(12, 2),
    StorageType ENUM('Shelf', 'Rack', 'Freezer', 'Container', 'Bin', 'Other') DEFAULT 'Shelf',
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_location_active (IsActive)
);

-- ============================================================================
-- Table: RawMaterialInventory
-- Description: Current inventory levels by material and location
-- ============================================================================
CREATE TABLE RawMaterialInventory (
    InventoryID INT PRIMARY KEY AUTO_INCREMENT,
    RawMaterialID INT NOT NULL,
    LocationID INT NOT NULL,
    QuantityOnHand DECIMAL(12, 2) NOT NULL DEFAULT 0,
    LotNumber VARCHAR(100),
    ExpiryDate DATE,
    DateReceived DATE,
    Condition ENUM('New', 'Aged', 'Damaged', 'Quarantined', 'Good') DEFAULT 'Good',
    LastInventoryCheck DATETIME,
    LastUpdated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (RawMaterialID) REFERENCES RawMaterial(RawMaterialID) ON DELETE RESTRICT,
    FOREIGN KEY (LocationID) REFERENCES Location(LocationID) ON DELETE RESTRICT,
    UNIQUE KEY uk_inventory_material_location (RawMaterialID, LocationID),
    INDEX idx_material_inventory (RawMaterialID),
    INDEX idx_location_inventory (LocationID),
    INDEX idx_expiry_date (ExpiryDate),
    INDEX idx_condition (Condition)
);

-- ============================================================================
-- 2. PROCUREMENT TABLES
-- ============================================================================

-- ============================================================================
-- Table: RawMaterialOrder
-- Description: Purchase orders for raw materials
-- ============================================================================
CREATE TABLE RawMaterialOrder (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    OrderNumber VARCHAR(50) NOT NULL UNIQUE,
    SupplierID INT NOT NULL,
    OrderDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    ExpectedDeliveryDate DATE,
    ActualDeliveryDate DATE,
    Status ENUM('Draft', 'Submitted', 'Confirmed', 'Shipped', 'Delivered', 'Cancelled', 'Rejected') DEFAULT 'Draft',
    TotalAmount DECIMAL(12, 2),
    CreatedBy VARCHAR(100),
    ApprovedBy VARCHAR(100),
    Notes TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID) ON DELETE RESTRICT,
    INDEX idx_order_date (OrderDate),
    INDEX idx_order_status (Status),
    INDEX idx_supplier_order (SupplierID),
    INDEX idx_delivery_date (ExpectedDeliveryDate)
);

-- ============================================================================
-- Table: RawMaterialOrderDetail
-- Description: Line items in purchase orders
-- ============================================================================
CREATE TABLE RawMaterialOrderDetail (
    OrderDetailID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID INT NOT NULL,
    RawMaterialID INT NOT NULL,
    QuantityOrdered DECIMAL(12, 2) NOT NULL,
    UnitPrice DECIMAL(10, 4) NOT NULL,
    QuantityReceived DECIMAL(12, 2) DEFAULT 0,
    LineTotal DECIMAL(12, 2) GENERATED ALWAYS AS (QuantityOrdered * UnitPrice) STORED,
    Status ENUM('Pending', 'Partial', 'Complete', 'Cancelled') DEFAULT 'Pending',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (OrderID) REFERENCES RawMaterialOrder(OrderID) ON DELETE CASCADE,
    FOREIGN KEY (RawMaterialID) REFERENCES RawMaterial(RawMaterialID) ON DELETE RESTRICT,
    INDEX idx_order_detail_order (OrderID),
    INDEX idx_order_detail_material (RawMaterialID),
    INDEX idx_order_detail_status (Status)
);

-- ============================================================================
-- Table: RawMaterialReceiving
-- Description: Goods receiving and intake
-- ============================================================================
CREATE TABLE RawMaterialReceiving (
    ReceivingID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID INT NOT NULL,
    ReceivingNumber VARCHAR(50) NOT NULL UNIQUE,
    ReceivingDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    ReceivedBy VARCHAR(100) NOT NULL,
    QuantityReceived DECIMAL(12, 2) NOT NULL,
    Status ENUM('Complete', 'Partial', 'Over-received', 'Damaged', 'Rejected') DEFAULT 'Complete',
    Notes TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (OrderID) REFERENCES RawMaterialOrder(OrderID) ON DELETE RESTRICT,
    INDEX idx_receiving_date (ReceivingDate),
    INDEX idx_receiving_order (OrderID),
    INDEX idx_receiving_status (Status)
);

-- ============================================================================
-- 3. QUALITY TABLES
-- ============================================================================

-- ============================================================================
-- Table: QualityInspection
-- Description: Quality inspection records for incoming materials
-- ============================================================================
CREATE TABLE QualityInspection (
    InspectionID INT PRIMARY KEY AUTO_INCREMENT,
    ReceivingID INT NOT NULL,
    RawMaterialID INT NOT NULL,
    InspectionDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    InspectedBy VARCHAR(100) NOT NULL,
    InspectionResult ENUM('Pass', 'Fail', 'Conditional Pass', 'Hold') DEFAULT 'Pass',
    Remarks TEXT,
    CertificateNumber VARCHAR(100),
    CertificateFileURL VARCHAR(255),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ReceivingID) REFERENCES RawMaterialReceiving(ReceivingID) ON DELETE RESTRICT,
    FOREIGN KEY (RawMaterialID) REFERENCES RawMaterial(RawMaterialID) ON DELETE RESTRICT,
    INDEX idx_inspection_date (InspectionDate),
    INDEX idx_inspection_result (InspectionResult),
    INDEX idx_inspection_material (RawMaterialID)
);

-- ============================================================================
-- 4. USAGE TRACKING TABLES
-- ============================================================================

-- ============================================================================
-- Table: RawMaterialUsage
-- Description: Track material consumption and usage
-- ============================================================================
CREATE TABLE RawMaterialUsage (
    UsageID INT PRIMARY KEY AUTO_INCREMENT,
    RawMaterialID INT NOT NULL,
    InventoryID INT NOT NULL,
    UsageDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    QuantityUsed DECIMAL(12, 2) NOT NULL,
    Department VARCHAR(100),
    UsedBy VARCHAR(100),
    Purpose ENUM('Production', 'Maintenance', 'Testing', 'Wastage', 'Other') DEFAULT 'Production',
    ProjectID VARCHAR(50),
    Notes TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (RawMaterialID) REFERENCES RawMaterial(RawMaterialID) ON DELETE RESTRICT,
    FOREIGN KEY (InventoryID) REFERENCES RawMaterialInventory(InventoryID) ON DELETE RESTRICT,
    INDEX idx_usage_date (UsageDate),
    INDEX idx_usage_material (RawMaterialID),
    INDEX idx_usage_department (Department),
    INDEX idx_usage_purpose (Purpose)
);

-- ============================================================================
-- 5. AUDIT & ADJUSTMENTS
-- ============================================================================

-- ============================================================================
-- Table: InventoryAdjustment
-- Description: Track inventory adjustments (discrepancies, corrections)
-- ============================================================================
CREATE TABLE InventoryAdjustment (
    AdjustmentID INT PRIMARY KEY AUTO_INCREMENT,
    InventoryID INT NOT NULL,
    AdjustmentDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    AdjustmentType ENUM('Physical Count', 'Correction', 'Loss', 'Damage', 'Expiry', 'Return', 'Other') DEFAULT 'Correction',
    QuantityAdjusted DECIMAL(12, 2) NOT NULL,
    ReasonCode VARCHAR(50),
    Description TEXT,
    AdjustedBy VARCHAR(100) NOT NULL,
    Approved BOOLEAN DEFAULT FALSE,
    ApprovedBy VARCHAR(100),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (InventoryID) REFERENCES RawMaterialInventory(InventoryID) ON DELETE RESTRICT,
    INDEX idx_adjustment_date (AdjustmentDate),
    INDEX idx_adjustment_type (AdjustmentType),
    INDEX idx_adjustment_inventory (InventoryID)
);

-- ============================================================================
-- Table: InventoryAuditLog
-- Description: Complete audit trail of inventory changes
-- ============================================================================
CREATE TABLE InventoryAuditLog (
    AuditLogID INT PRIMARY KEY AUTO_INCREMENT,
    InventoryID INT NOT NULL,
    RawMaterialID INT NOT NULL,
    PreviousQuantity DECIMAL(12, 2),
    NewQuantity DECIMAL(12, 2),
    ChangeType ENUM('Received', 'Used', 'Adjusted', 'Inspected') DEFAULT 'Adjusted',
    ChangedBy VARCHAR(100),
    ChangeReason VARCHAR(255),
    ChangedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (InventoryID) REFERENCES RawMaterialInventory(InventoryID) ON DELETE CASCADE,
    FOREIGN KEY (RawMaterialID) REFERENCES RawMaterial(RawMaterialID) ON DELETE RESTRICT,
    INDEX idx_audit_date (ChangedAt),
    INDEX idx_audit_inventory (InventoryID),
    INDEX idx_audit_material (RawMaterialID),
    INDEX idx_audit_change_type (ChangeType)
);

-- ============================================================================
-- 6. PRICING & SUPPLIER TABLES
-- ============================================================================

-- ============================================================================
-- Table: SupplierMaterialPrice
-- Description: Track pricing history and quotes from suppliers
-- ============================================================================
CREATE TABLE SupplierMaterialPrice (
    PriceID INT PRIMARY KEY AUTO_INCREMENT,
    SupplierID INT NOT NULL,
    RawMaterialID INT NOT NULL,
    UnitPrice DECIMAL(10, 4) NOT NULL,
    MinimumOrderQuantity DECIMAL(12, 2) DEFAULT 1,
    EffectiveDate DATE NOT NULL,
    ExpiryDate DATE,
    CurrencyCode VARCHAR(3) DEFAULT 'USD',
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID) ON DELETE CASCADE,
    FOREIGN KEY (RawMaterialID) REFERENCES RawMaterial(RawMaterialID) ON DELETE CASCADE,
    INDEX idx_supplier_material (SupplierID, RawMaterialID),
    INDEX idx_effective_date (EffectiveDate),
    INDEX idx_price_active (IsActive)
);

-- ============================================================================
-- 7. REORDER MANAGEMENT
-- ============================================================================

-- ============================================================================
-- Table: ReorderPoint
-- Description: Automatic reorder thresholds and configurations
-- ============================================================================
CREATE TABLE ReorderPoint (
    ReorderID INT PRIMARY KEY AUTO_INCREMENT,
    RawMaterialID INT NOT NULL,
    MinimumStockLevel DECIMAL(12, 2) NOT NULL,
    MaximumStockLevel DECIMAL(12, 2),
    ReorderQuantity DECIMAL(12, 2) NOT NULL,
    SafetyStock DECIMAL(12, 2) DEFAULT 0,
    LeadTimeDays INT DEFAULT 7,
    LastReorderDate DATETIME,
    EffectiveDate DATE DEFAULT CURDATE(),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (RawMaterialID) REFERENCES RawMaterial(RawMaterialID) ON DELETE CASCADE,
    UNIQUE KEY uk_material_reorder (RawMaterialID),
    INDEX idx_material_reorder (RawMaterialID)
);

-- ============================================================================
-- 8. VIEWS (Optional but Useful)
-- ============================================================================

-- ============================================================================
-- View: CurrentInventoryStatus
-- Description: Current inventory levels across all locations
-- ============================================================================
CREATE OR REPLACE VIEW CurrentInventoryStatus AS
SELECT 
    rm.RawMaterialID,
    rm.MaterialName,
    rmc.CategoryName,
    l.LocationName,
    rmi.QuantityOnHand,
    rm.UnitOfMeasure,
    rm.ReorderLevel,
    CASE 
        WHEN rmi.QuantityOnHand <= rm.ReorderLevel THEN 'REORDER_NEEDED'
        WHEN rmi.QuantityOnHand <= rm.SafetyStockLevel THEN 'LOW_STOCK'
        ELSE 'NORMAL'
    END AS StockStatus,
    rmi.ExpiryDate,
    DATEDIFF(rmi.ExpiryDate, CURDATE()) AS DaysUntilExpiry,
    rmi.LotNumber,
    rmi.Condition,
    rmi.LastUpdated
FROM RawMaterialInventory rmi
JOIN RawMaterial rm ON rmi.RawMaterialID = rm.RawMaterialID
JOIN RawMaterialCategory rmc ON rm.CategoryID = rmc.CategoryID
JOIN Location l ON rmi.LocationID = l.LocationID
WHERE rm.IsActive = TRUE;

-- ============================================================================
-- View: PendingOrders
-- Description: All pending purchase orders with details
-- ============================================================================
CREATE OR REPLACE VIEW PendingOrders AS
SELECT 
    rmo.OrderID,
    rmo.OrderNumber,
    s.SupplierName,
    rm.MaterialName,
    romd.QuantityOrdered,
    romd.QuantityReceived,
    (romd.QuantityOrdered - romd.QuantityReceived) AS QuantityPending,
    romd.UnitPrice,
    rmo.ExpectedDeliveryDate,
    rmo.Status,
    DATEDIFF(rmo.ExpectedDeliveryDate, CURDATE()) AS DaysUntilDelivery
FROM RawMaterialOrder rmo
JOIN Supplier s ON rmo.SupplierID = s.SupplierID
JOIN RawMaterialOrderDetail romd ON rmo.OrderID = romd.OrderID
JOIN RawMaterial rm ON romd.RawMaterialID = rm.RawMaterialID
WHERE rmo.Status NOT IN ('Delivered', 'Cancelled', 'Rejected')
ORDER BY rmo.ExpectedDeliveryDate ASC;

-- ============================================================================
-- View: MaterialUsageSummary
-- Description: Monthly usage statistics by material
-- ============================================================================
CREATE OR REPLACE VIEW MaterialUsageSummary AS
SELECT 
    DATE_TRUNC(rmu.UsageDate, MONTH) AS UsageMonth,
    rm.MaterialName,
    SUM(rmu.QuantityUsed) AS TotalQuantityUsed,
    rm.UnitOfMeasure,
    COUNT(rmu.UsageID) AS UsageCount,
    rmu.Purpose,
    AVG(rmu.QuantityUsed) AS AvgQuantityPerUsage
FROM RawMaterialUsage rmu
JOIN RawMaterial rm ON rmu.RawMaterialID = rm.RawMaterialID
GROUP BY UsageMonth, rm.RawMaterialID, rmu.Purpose
ORDER BY UsageMonth DESC, rm.MaterialName ASC;

-- ============================================================================
-- 9. INDEXES FOR PERFORMANCE
-- ============================================================================

CREATE INDEX idx_material_category_active ON RawMaterial(CategoryID, IsActive);
CREATE INDEX idx_inventory_expiry_condition ON RawMaterialInventory(ExpiryDate, Condition);
CREATE INDEX idx_order_supplier_date ON RawMaterialOrder(SupplierID, OrderDate);
CREATE INDEX idx_receiving_material ON RawMaterialReceiving(OrderID, ReceivingDate);
CREATE INDEX idx_usage_project ON RawMaterialUsage(ProjectID, UsageDate);
CREATE INDEX idx_adjustment_approval ON InventoryAdjustment(Approved, ApprovedBy);

-- ============================================================================
-- 10. SAMPLE DATA (Optional - for testing)
-- ============================================================================

-- Insert Categories
INSERT INTO RawMaterialCategory (CategoryName, Description) VALUES
('Metals', 'Steel, Aluminum, Copper, etc.'),
('Chemicals', 'Polymers, Resins, Solvents'),
('Textiles', 'Fabrics, Threads, Fibers'),
('Plastics', 'Raw plastic pellets and compounds');

-- Insert Suppliers
INSERT INTO Supplier (SupplierName, ContactPerson, ContactEmail, PhoneNumber, Address, LeadTimeDays, PaymentTerms) VALUES
('Steel Industries Ltd', 'John Doe', 'john@steelindustries.com', '+1-555-0001', '123 Industrial Ave', 7, 'Net 30'),
('Chemical Solutions', 'Jane Smith', 'jane@chemsol.com', '+1-555-0002', '456 Chemical Park', 5, 'Net 15');

-- Insert Materials
INSERT INTO RawMaterial (MaterialName, Description, CategoryID, UnitOfMeasure, ReorderLevel, SafetyStockLevel, PreferredSupplierID) VALUES
('Steel Bar', 'High-grade steel bar', 1, 'kg', 100, 50, 1),
('Plastic Resin', 'Polyethylene resin', 4, 'kg', 200, 75, 2);

-- Insert Locations
INSERT INTO Location (LocationName, LocationDescription, WarehouseSection, Capacity, StorageType) VALUES
('Main Storage A', 'Primary storage rack', 'Section A', 5000, 'Rack'),
('Cold Storage B', 'Temperature controlled', 'Section B', 2000, 'Freezer');

-- ============================================================================
-- END OF DDL SCRIPT
-- ============================================================================