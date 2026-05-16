# hst-material-service
Raw Material Management Service 


Key Business Flows:
1. Procurement: Order → Receive → Inspect → Accept → Add to Inventory <BR>
2. Consumption: Issue from Inventory → Track Usage → Reduce Stock <BR>
3. Monitoring: Track reorder levels → Expiry dates → Quality certifications <BR>

Core Entities & Structure
-------------------------
1. RawMaterial <BR>
	RawMaterialID (Primary Key) <BR>
	Name - Material name (e.g., "Steel Bar", "Plastic Resin") <BR>
	Description - Detailed description <BR>
	UnitOfMeasure - Measurement unit (kg, liters, pieces, etc.) <BR>
	CategoryID (Foreign Key) - Links to category <BR>
	ReorderLevel - Minimum stock threshold for reordering <BR>
	SafetyStockLevel - Buffer stock to prevent <BR>
	Supplier Preferred - Default supplier for this material <BR>

2. Supplier <BR>
	SupplierID (Primary Key) <BR>
	Name - Supplier company name <BR>
	ContactInfo - Phone/email <BR>
	Address - Full address <BR>
	LeadTimeDays - Delivery lead time <BR>
	PaymentTerms - Credit terms <BR>
	IsActive - Active/inactive status <BR>

3. RawMaterialCategory <BR>
	CategoryID (Primary Key) <BR>
	Name - Category name (e.g., "Metals", "Chemicals", "Textiles") <BR>
	Description <BR>

4. RawMaterialInventory <BR>
	InventoryID (Primary Key) <BR>
	RawMaterialID (Foreign Key) <BR>
	LocationID (Foreign Key) - Warehouse/storage location <BR>
	QuantityOnHand - Current stock quantity <BR>
	LotNumber - Batch/lot identifier for traceability <BR>
	ExpiryDate - Expiration or shelf-life date (if applicable) <BR>
	DateReceived - When received <BR>
	Condition - Quality status (New, Aged, Damaged, etc.) <BR>

5. RawMaterialOrder (Procurement) <BR>
	OrderID (Primary Key) <BR>
	SupplierID (Foreign Key) <BR>
	OrderDate - Date order was placed <BR>
	ExpectedDeliveryDate <BR>
	ActualDeliveryDate <BR>
	Status - Draft, Submitted, Confirmed, Delivered, Cancelled <BR>
	TotalAmount - Total order cost <BR>
	CreatedBy - User who created order <BR>

6. RawMaterialOrderDetail <BR>
	OrderDetailID (Primary Key) <BR>
	OrderID (Foreign Key) <BR>
	RawMaterialID (Foreign Key) <BR>
	QuantityOrdered <BR>
	UnitPrice <BR>
	QuantityReceived <BR>
	LineTotal - (QuantityOrdered × UnitPrice) <BR>

7. RawMaterialReceiving <BR>
	ReceivingID (Primary Key) <BR>
	OrderID (Foreign Key) <BR>
	ReceivedDate <BR>
	ReceivedBy - Employee who received <BR>
	QuantityReceived <BR>
	Status - Complete, Partial, Over-received, Damaged <BR>
	Notes - Any discrepancies or observations <BR>

8. RawMaterialUsage <BR>
	UsageID (Primary Key) <BR>
	RawMaterialID (Foreign Key) <BR>
	DateUsed <BR>
	QuantityUsed <BR>
	Department - Where material was used <BR>
	UsedBy - Employee name <BR>
	Purpose - What it was used for (Production, Maintenance, Testing) <BR>
	ProjectID - If applicable <BR>

9. Location (Storage/Warehouse) <BR>
	LocationID (Primary Key) <BR>
	Name - Warehouse/bin name <BR>
	Description <BR>
	Capacity - Max storage capacity <BR>
	StorageType - Shelf, Rack, Freezer, etc. <BR>

10. QualityInspection <BR>
	InspectionID (Primary Key) <BR>
	RawMaterialID (Foreign Key) <BR>
	DateInspected <BR>
	InspectedBy - QA employee <BR>
	Result - Pass, Fail, Conditional Pass <BR>
	Remarks - Inspection notes <BR>
	CertificateNumber - COA or certificate reference (if applicable) <BR>