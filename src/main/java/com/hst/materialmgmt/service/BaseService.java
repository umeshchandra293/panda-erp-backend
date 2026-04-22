package com.hst.materialmgmt.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BaseService {
  public Mono<Object> createFullHierarchy(Mono<Object> parentMono);

  public Mono<Object> findByIdFullHierarchy(String parentId);

  public Flux<Object> findAllFullHierarchy();

  public Mono<Object> updateFullHierarchy(String parentId, Mono<Object> parentMono);

  public Mono<Long> deleteFullHierarchy(String parentId);
}
