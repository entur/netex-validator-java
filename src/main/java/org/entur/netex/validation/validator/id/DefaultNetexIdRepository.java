package org.entur.netex.validation.validator.id;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Hashmap-based implementation of the NeTEX ids repository.
 */
public class DefaultNetexIdRepository implements NetexIdRepository {

  private final Map<String, Set<String>> commonIdsCache;
  private final Map<String, Set<String>> accumulatedNetexIdsMap;

  public DefaultNetexIdRepository() {
    this.commonIdsCache = new ConcurrentHashMap<>();
    this.accumulatedNetexIdsMap = new ConcurrentHashMap<>();
  }

  @Override
  public synchronized Set<String> getDuplicateNetexIds(
    String reportId,
    String filename,
    Set<String> localIds
  ) {
    Set<String> accumulatedNetexIds = accumulatedNetexIdsMap.computeIfAbsent(
      reportId,
      s -> new HashSet<>()
    );
    Set<String> duplicates = new HashSet<>(localIds);
    duplicates.retainAll(accumulatedNetexIds);
    accumulatedNetexIds.addAll(localIds);
    return duplicates;
  }

  @Override
  public Set<String> getSharedNetexIds(String reportId) {
    Set<String> commonIds = commonIdsCache.get(reportId);
    return Objects.requireNonNullElse(commonIds, Collections.emptySet());
  }

  @Override
  public synchronized void addSharedNetexIds(
    String reportId,
    Set<IdVersion> commonIdVersions
  ) {
    Set<String> commonIds = commonIdVersions
      .stream()
      .map(IdVersion::getId)
      .collect(Collectors.toSet());
    commonIdsCache.computeIfPresent(
      reportId,
      (key, value) -> {
        value.addAll(commonIds);
        return value;
      }
    );
    commonIdsCache.computeIfAbsent(reportId, key -> commonIds);
  }

  @Override
  public void cleanUp(String reportId) {
    commonIdsCache.remove(reportId);
    accumulatedNetexIdsMap.remove(reportId);
  }
}
