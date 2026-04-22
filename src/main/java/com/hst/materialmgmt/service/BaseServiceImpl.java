package com.hst.materialmgmt.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Generic parent/child service implementation that: - creates parent + recursive children and link
 * rows - reads parent with full subtree - updates parent + replaces subtree - deletes parent and
 * subtree recursively
 *
 * <p>Subclasses must implement abstract helpers to provide repositories, link objects and attach
 * logic.
 */
public abstract class BaseServiceImpl implements BaseService {

  @Autowired protected TransactionalOperator transactionalOperator;

  protected abstract BaseMapper getMapper();

  protected abstract ParentRepositoryImpl getMasterRepository();

  protected abstract Map<ParentRepositoryImpl, List<BaseEntity>> getChildReposAndEntities(
      BaseEntity parentEntity);

  protected abstract BaseEntity getParentChildLinkObject(Object parentObject, Object childObject);

  protected abstract List<ParentRepositoryImpl> getChildRepositoryList(BaseEntity parentEntity);

  protected abstract void attachChildrenToParent(Object parentEntity, List<Object> children);

  protected abstract ParentRepositoryImpl getRepositoryForEntity(BaseEntity entity);

  /**
   * Create a parent object along with its entire hierarchy of children. The input parentMono should
   * contain the full hierarchy in its structure. Returns the saved parent object with all children
   * populated.
   */
  public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
    if (parentMono == null) {
      return Mono.error(new IllegalArgumentException("Parent object cannot be null"));
    }

    return parentMono
        // Convert parent model to entity
        .map(parent -> getMapper().toEntity(parent, null, true))

        // Save parent first
        .flatMap(
            parentEntity ->
                getMasterRepository()
                    .create(parentEntity)
                    .flatMap(
                        savedParent ->
                            // Recursively save all children linked to this parent
                            saveChildrenRecursively(savedParent)
                                // After children are saved, return parent
                                .thenReturn(savedParent)))

        // Map back to model
        .map(savedParent -> getMapper().toModel(savedParent))

        // Ensure everything runs in a transaction
        .as(transactionalOperator::transactional)

        // Add error mapping
        .onErrorMap(ex -> new RuntimeException("Failed to create full hierarchy", ex));
  }

  /**
   * Recursively saves all children of the given entity, creating link rows as needed.
   *
   * @param entity
   * @return
   */
  private Mono<BaseEntity> saveChildrenRecursively(BaseEntity entity) {
    if (entity == null) {
      return Mono.error(new IllegalArgumentException("Entity cannot be null"));
    }

    Map<ParentRepositoryImpl, List<BaseEntity>> childrenMap = getChildReposAndEntities(entity);
    if (childrenMap == null || childrenMap.isEmpty()) {
      return Mono.just(entity);
    }

    // save children recursively and create link rows
    Mono<Void> childOps =
        Flux.fromIterable(childrenMap.entrySet())
            .flatMap(
                entry -> {
                  ParentRepositoryImpl childRepo = entry.getKey();
                  List<BaseEntity> childEntities = entry.getValue();
                  if (childEntities == null || childEntities.isEmpty()) {
                    return Mono.empty();
                  }

                  return Flux.fromIterable(childEntities)
                      .flatMap(
                          child ->
                              saveRecursiveEntity(child, childRepo)
                                  .flatMap(
                                      savedChild -> {
                                        BaseEntity parentChildLink =
                                            getParentChildLinkObject(entity, savedChild);

                                        if (parentChildLink == null) {
                                          return Mono.empty();
                                        }

                                        // Save link row
                                        return childRepo.saveLink(parentChildLink).then();
                                      }));
                })
            .then(); // Ensures Mono<Void>

    return childOps.then(Mono.just(entity));
  }

  /**
   * Saves the given entity using the specified repository, then recursively saves its children.
   *
   * @param entity
   * @param repo
   * @return
   */
  private Mono<BaseEntity> saveRecursiveEntity(BaseEntity entity, ParentRepositoryImpl repo) {
    return repo.create(entity).flatMap(savedEntity -> saveChildrenRecursively(savedEntity));
  }

  /** ---- READ ---- */
  public Mono<Object> findByIdFullHierarchy(String parentId) {
    if (parentId == null) {
      return Mono.error(new RuntimeException("Can't find an Object with empty id"));
    }

    return getMasterRepository()
        .findById(parentId)
        .flatMap(
            parentEntity ->
                fetchAndAttachChildren((BaseEntity) parentEntity)
                    .then(Mono.fromCallable(() -> getMapper().toModel(parentEntity))))
        .as(transactionalOperator::transactional);
  }

  /**
   * Recursively fetch and attach all children to the given parentEntity.
   *
   * @param parentEntity
   * @return
   */
  private Mono<Void> fetchAndAttachChildren(BaseEntity parentEntity) {
    if (parentEntity == null) {
      return Mono.empty();
    }

    List<ParentRepositoryImpl> childRepos = getChildRepositoryList(parentEntity);
    if (childRepos == null || childRepos.isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(childRepos)
        .flatMap(
            childRepo ->
                childRepo
                    .findAllByParent(parentEntity)
                    .collectList()
                    .flatMap(
                        childObjs -> {
                          if (childObjs == null || childObjs.isEmpty()) {
                            return Mono.empty();
                          }

                          attachChildrenToParent(parentEntity, childObjs);
                          return Flux.fromIterable(childObjs)
                              .flatMap(childObj -> fetchAndAttachChildren((BaseEntity) childObj))
                              .then();
                        }))
        .then();
  }

  /**
   * Fetch all parent objects with their full hierarchy of children. Returns a Flux of parent models
   * with children populated.
   */
  public Flux<Object> findAllFullHierarchy() {
    return getMasterRepository()
        .findAll()
        .flatMap(
            parentObj -> {
              BaseEntity parentEntity = (BaseEntity) parentObj;
              return fetchAndAttachChildren(parentEntity)
                  .then(Mono.fromCallable(() -> getMapper().toModel(parentEntity)));
            })
        .as(transactionalOperator::transactional);
  }

  /** ---- UPDATE ---- */
  public Mono<Object> updateFullHierarchy(String parentId, Mono<Object> parentMono) {
    if (parentId == null || parentId.isEmpty()) {
      return Mono.error(new IllegalArgumentException("Object key cannot be null or empty"));
    }

    if (parentMono == null) {
      return Mono.error(new IllegalArgumentException("Updated Object cannot be null"));
    }

    return getMasterRepository()
        .findById(parentId) // Step 0: find existing parent
        .switchIfEmpty(Mono.error(new RuntimeException("Object not found for id: " + parentId)))
        .flatMap(
            existingEntity ->
                parentMono.flatMap(
                    modifiedParent -> {
                      if (modifiedParent == null) {
                        return Mono.error(
                            new IllegalArgumentException("Updated Object cannot be null"));
                      }

                      // Convert DTO/Model to Entity
                      BaseEntity parentEntity =
                          getMapper()
                              .toEntity(
                                  modifiedParent, (BaseEntity) existingEntity, true);

                      // Step 1: Update the parent row
                      return getMasterRepository()
                          .update(parentId, parentEntity)
                          .flatMap(
                              rowsUpdated -> {
                                if (rowsUpdated == 0) {
                                  return Mono.error(
                                      new RuntimeException(
                                          "Parent update failed: no rows updated for id="
                                              + parentEntity.getId()));
                                }

                                // Step 2: delete all old children recursively
                                return deleteChildrenRecursively((BaseEntity) existingEntity)
                                    // Step 3: save children recursively (without explicit
                                    // iteration)
                                    .then(saveChildrenRecursively(parentEntity))
                                    // Step 4: return updated parent
                                    .then(Mono.just(parentEntity));
                              });
                    }))
        // Step 5: Map back to API model
        .map(updatedEntity -> getMapper().toModel(updatedEntity))
        // Step 6: Run in a transaction
        .as(transactionalOperator::transactional);
  }

  private Mono<Long> deleteChildrenRecursively(BaseEntity parentEntity) {
    // 1. Handle the base case: No entity or no configured children
    if (parentEntity == null) {
      return Mono.just(0L);
    }

    List<ParentRepositoryImpl> childRepositories = getChildRepositoryList(parentEntity);

    if (childRepositories == null || childRepositories.isEmpty()) {
      return Mono.just(0L); // no children = nothing deleted
    }

    // 2. Process children across all repositories
    return Flux.fromIterable(childRepositories)
        .flatMap(
            childRepo ->
                // Find all children for the current parent
                childRepo
                    .findAllByParent(parentEntity)
                    .cast(BaseEntity.class)
                    // Use flatMap for concurrent recursion if order between siblings doesn't matter
                    .flatMap(
                        childEntity ->
                            // a) Recurse into grandchildren first (Bottom-up flow)
                            deleteChildrenRecursively(childEntity)
                                .flatMap(
                                    grandChildrenDeleted ->
                                        // b) Delete the current child entity
                                        childRepo
                                            .deleteById(childEntity.getId())
                                            // Map the deletion result (usually Mono<Void> or
                                            // Mono<Integer>) to a count
                                            .thenReturn(
                                                1L) // If deleteById completes, assume 1 row was
                                            // deleted
                                            .onErrorResume(
                                                e -> {
                                                  // Log error if needed, but return 0L to allow
                                                  // transaction to continue
                                                  // if this specific delete fails (usually handled
                                                  // by global transaction rollback)
                                                  System.err.println(
                                                      "Error deleting child "
                                                          + childEntity.getId()
                                                          + ": "
                                                          + e.getMessage());
                                                  return Mono.just(0L);
                                                })
                                            // c) Sum the counts: grandchildren + current child
                                            .map(
                                                childDeletedCount ->
                                                    grandChildrenDeleted + childDeletedCount))))
        .reduce(0L, Long::sum); // Sum deletions across all child repos
  }

  /**
   * ---- DELETE ---- Recursively delete the parent and all its children. Returns the total number
   * of rows deleted (parent + all children).
   */
  public Mono<Long> deleteFullHierarchy(String parentId) {
    if (parentId == null || parentId.isEmpty()) {
      return Mono.error(new IllegalArgumentException("Parent id cannot be null or empty"));
    }

    return getMasterRepository()
        .findById(parentId)
        .switchIfEmpty(Mono.error(new RuntimeException("Parent not found for id: " + parentId)))
        .flatMap(
            parentObj -> {
              BaseEntity parentEntity = (BaseEntity) parentObj;

              // First delete all children, then delete the parent
              return deleteHierarchy(parentEntity)
                  .flatMap(
                      childrenDeletedCount ->
                          getMasterRepository()
                              .deleteById(parentId)
                              .map(
                                  parentDeletedCount ->
                                      childrenDeletedCount
                                          + (parentDeletedCount == null
                                              ? 0L
                                              : parentDeletedCount)));
            })
        .as(transactionalOperator::transactional)
        .onErrorMap(
            ex ->
                new RuntimeException(
                    "Failed to delete parent and all children for id=" + parentId, ex));
  }

  /**
   * Recursively delete all children of the given parentEntity. Does NOT delete the parent itself.
   * Returns the total number of child rows deleted.
   */
  private Mono<Long> deleteHierarchy(BaseEntity parentEntity) {
    if (parentEntity == null) {
      return Mono.just(0L);
    }
    List<ParentRepositoryImpl> childRepositories = getChildRepositoryList(parentEntity);

    if (childRepositories == null || childRepositories.isEmpty()) {
      // no children: nothing to delete here
      return Mono.just(0L);
    }

    return Flux.fromIterable(childRepositories)
        .flatMap(
            childRepo ->
                childRepo
                    .findAllByParent(parentEntity)
                    .map(obj -> (BaseEntity) obj)
                    .flatMap(
                        childEntity ->
                            deleteHierarchy(childEntity)
                                .flatMap(
                                    childrenDeletedCount ->
                                        deleteEntity(childEntity)
                                            .map(
                                                childDeletedCount ->
                                                    childrenDeletedCount
                                                        + (childDeletedCount == null
                                                            ? 0L
                                                            : childDeletedCount)))))
        .reduce(0L, Long::sum); // sum across all child repos
  }

  /**
   * Deletes the given entity using its repository. Subclasses should override this if custom delete
   * logic is needed.
   */
  protected Mono<Long> deleteEntity(BaseEntity entity) {
    if (entity == null) {
      return Mono.just(0L);
    }
    ParentRepositoryImpl repo = getRepositoryForEntity(entity);
    if (repo == null) {
      return Mono.just(0L);
    }
    return repo.deleteById(entity.getId());
  }

  protected String getIdFromEntityObject(Object entityObject) {
    String id = null;
    if (entityObject != null) {
      BaseEntity baseEntity = (BaseEntity) entityObject;
      if (baseEntity.getId() != null && !baseEntity.getId().isEmpty()) {
        id = baseEntity.getId().trim();
      }
    }
    return id;
  }
}
