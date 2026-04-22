package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class ParentBaseServiceImpl implements BaseService {

  @Autowired TransactionalOperator transactionalOperator;

  protected abstract BaseMapper getMapper();

  protected abstract ParentRepositoryImpl getParentRepository();

  public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
    return parentMono
        .flatMap(
            parentObject -> {
              BaseEntity parentEntity = getMapper().toEntity(parentObject, null, true);
              // Save parentEntity, then map back to model
              return getParentRepository()
                  .create(parentEntity)
                  .map(savedEntity -> getMapper().toModel(savedEntity));
            })
        .as(transactionalOperator::transactional);
  }

  public Mono<Object> findByIdFullHierarchy(String parentId) {
    if (parentId == null) {
      return Mono.error(new RuntimeException("Can't find an Object with empty id"));
    }

    return getParentRepository().findById(parentId).map(entity -> getMapper().toModel(entity));
  }

  public Flux<Object> findAllFullHierarchy() {
    return getParentRepository().findAll().map(baseEntity -> getMapper().toModel(baseEntity));
  }

  public Mono<Object> updateFullHierarchy(String parentId, Mono<Object> parentMono) {
    if (parentId == null || parentId.isEmpty()) {
      return Mono.error(new IllegalArgumentException("Object key cannot be null or empty"));
    }

    if (parentMono == null) {
      return Mono.error(new IllegalArgumentException("Updated Object cannot be null"));
    }

    return findByIdFullHierarchy(parentId)
        .switchIfEmpty(Mono.error(new RuntimeException("Object not found for id: " + parentId)))
        .flatMap(
            existing ->
                parentMono.flatMap(
                    parentObject -> {
                      BaseEntity newEntity = getMapper().toEntity(parentObject, null, true);

                      return getParentRepository()
                          .update(parentId, newEntity)
                          .flatMap(
                              rowsUpdated -> {
                                if (rowsUpdated == null || rowsUpdated <= 0) {
                                  return Mono.error(
                                      new RuntimeException(
                                          "Failed to update parent with id: " + parentId));
                                }
                                // Success: return the updated entity
                                return Mono.just(getMapper().toModel(newEntity));
                              });
                    }))
        .as(transactionalOperator::transactional);
  }

  public Mono<Long> deleteFullHierarchy(String parentId) {
    if (parentId == null || parentId.isEmpty()) {
      return Mono.error(new IllegalArgumentException("Item key cannot be null or empty"));
    }

    return findByIdFullHierarchy(parentId)
        .switchIfEmpty(Mono.error(new RuntimeException("Object not found for id: " + parentId)))
        .flatMap(
            existing -> getParentRepository().deleteById(((BaseEntity) existing).getId())
            // .flatMap(null)
            )
        .as(transactionalOperator::transactional)
        .onErrorResume(
            ex -> Mono.error(new RuntimeException("Failed to delete parent and all children", ex)));
  }
}
