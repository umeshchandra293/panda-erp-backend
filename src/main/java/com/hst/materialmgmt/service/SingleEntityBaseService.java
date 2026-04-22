package com.hst.materialmgmt.service;

import java.util.List;
import java.util.Map;

import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;

public abstract class SingleEntityBaseService extends BaseServiceImpl {

  @Override
  protected Map<ParentRepositoryImpl, List<BaseEntity>> getChildReposAndEntities(
      BaseEntity parentEntity) {
    return null;
  }

  @Override
  protected BaseEntity getParentChildLinkObject(Object parentObject, Object childObject) {
    return null;
  }

  @Override
  protected List<ParentRepositoryImpl> getChildRepositoryList(BaseEntity parentEntity) {
    return null;
  }

  @Override
  protected void attachChildrenToParent(Object parentEntity, List<Object> children) {
    // No implementation needed for single entity service
  }

  @Override
  protected ParentRepositoryImpl getRepositoryForEntity(BaseEntity entity) {
    return null;
  }
}
