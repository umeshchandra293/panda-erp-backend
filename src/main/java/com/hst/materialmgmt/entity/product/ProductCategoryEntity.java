package com.hst.materialmgmt.entity.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.hst.materialmgmt.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
@SuperBuilder
@Table(value = "product_tbl", schema = "rm_material_schema")
public class ProductCategoryEntity extends BaseEntity {

    @Id 
    @Column("category_id")
    private String categoryId;

    @Column("parent_id")
    private String parentId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Override
    public String getId() {
        return categoryId;
    }

}
