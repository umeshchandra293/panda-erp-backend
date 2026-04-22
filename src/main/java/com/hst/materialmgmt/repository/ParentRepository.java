package com.hst.materialmgmt.repository;

import java.util.List;

import com.hst.materialmgmt.entity.BaseEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ParentRepository {
  public Mono<BaseEntity> create(BaseEntity entity);

  public Mono<Object> findById(String id);

  public Flux<Object> findAll();

  public Mono<Long> update(String id, BaseEntity entity);

  public Mono<Long> deleteById(String id);

  public Mono<Long> deleteMultiple(List<String> ids);

  public Flux<Object> findAllByParent(BaseEntity parentEntity);

  public Mono<Long> deleteAllByParent(BaseEntity parentEntity);

  public Mono<BaseEntity> saveLink(BaseEntity entity);
}
