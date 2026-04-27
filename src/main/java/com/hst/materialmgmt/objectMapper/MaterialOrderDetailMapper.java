package com.hst.materialmgmt.objectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hst.api.model.MaterialOrderDetail;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.MaterialOrderDetailEntity;

public class MaterialOrderDetailMapper {
	public static BaseEntity toEntity(MaterialOrderDetail orderDetail) {
		orderDetail.setOrderDetailId(BaseMapper.populateId(orderDetail.getOrderDetailId()));
	    return MaterialOrderDetailEntity.builder()
	    	.orderDetailId(orderDetail.getOrderDetailId())
	    	.materialId(orderDetail.getMaterialId())
			.lineTotal(orderDetail.getLineTotal())
			//.orderQuantity(orderDetail.getOrderQuantity())
			//.qtyReceived(orderDetail.getQtyReceived())
			.unitPrice(orderDetail.getUnitPrice())
	        .build();
	  }

	  public static MaterialOrderDetail toModel(MaterialOrderDetailEntity entity) {
	    return new MaterialOrderDetail()
	    	.orderDetailId(entity.getOrderDetailId())
	    	.materialId(entity.getMaterialId())
	    		
	    		
	        ;
	  }

	  public static List<MaterialOrderDetail> toModels(List<BaseEntity> entities) {
	    if (entities == null) return new ArrayList<>();
	    return entities.stream()
	        .map(entity -> toModel((MaterialOrderDetailEntity) entity))
	        .collect(Collectors.toList());
	  }

	  public static List<BaseEntity> toEntities(List<MaterialOrderDetail> models) {
	    if (models == null) return new ArrayList<>();
	    return models.stream().map(model -> toEntity((MaterialOrderDetail) model)).collect(Collectors.toList());
	  }
}
