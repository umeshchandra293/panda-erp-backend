{
    "Id": "",
    "VendorId": "vendo01",
    "FiscalYear": "2025",
    "InvoiceDate": "10-10-2025",
    "PostingDate": "10-12-2025",
    "DocumentType": "PDF",
    "Currency": "Rupee",
    "ExchangeRate": "1",
    "TotalAmount": "2000",
    "ReferenceDoc": "Original",
    "Status": "Draft"
    "InvoiceItems": [{
            "Id": "01",
            "PoNumber":"001",
            "PoItem": "poItem01",
            "GlAccount":"1000",
            "CostCenter":"Manufactering",
            "MaterialId":"Mat01",
            "Description":"Purchage of raw materials",
            "Quantity":"100",
            "UnitPrice":"10",
            "LineTotal":"1000",
            "TaxCode":"tx001",
            "Status":"Draft"
        },
        {
            "Id": "02",
            "PoNumber":"002",
            "PoItem": "poItem02",
            "GlAccount":"2000",
            "CostCenter":"Manufactering",
            "MaterialId":"Mat02",
            "Description":"Purchage of raw materials",
            "Quantity":"50",
            "UnitPrice":"20",
            "LineTotal":"1000",
            "TaxCode":"tx001",
            "Status":"Draft",

        }
    ]
}


CREATE TABLE "erp_finance_schema".VendorInvoiceItem_Tbl (
    Id                  VARCHAR(40) NOT NULL PRIMARY KEY,
    Invoice_Id          VARCHAR(40) NOT NULL,
    Po_Number           VARCHAR(20),
    Po_Item             INT,
    Gl_Account          VARCHAR(20),
    Cost_Center         VARCHAR(20),
    Material_Id         VARCHAR(20),
    Description         VARCHAR(255),
    Quantity            DECIMAL(15,3),
    Unit_Price          DECIMAL(15,2),
    Line_Total          DECIMAL(15,2),
    Tax_Code            VARCHAR(10),
    Status              VARCHAR(20) DEFAULT 'Open',
    Created_Timestamp 	timestamp NOT NULL,
    Created_By 			varchar(100) NOT NULL,
    Modified_Timestamp 	timestamp NOT NULL,
    Modified_By 		varchar(100) NOT NULL,
    FOREIGN KEY (Invoice_Id) REFERENCES "erp_finance_schema".VendorInvoiceHeader_Tbl(Id) ON DELETE CASCADE
);