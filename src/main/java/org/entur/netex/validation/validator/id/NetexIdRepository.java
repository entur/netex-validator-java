package org.entur.netex.validation.validator.id;

import java.util.Set;

/**
 * Repository for NeTEx IDs. Stores NeTEX ids and check uniqueness across lines.
 */
public interface NetexIdRepository {

    /**
     * Return  the set of NeTEx Ids that are present both in the current file and in the set of NeTEX ids already found in other common files or line files.
     *
     * @param reportId id of the current report.
     * @param filename name of the current NeTEx file.
     * @param localIds NeTEx ids in the current file.
     * @return the set of NeTEx ids that are present both in the current file and in previously analyzed files.
     */
    Set<String> getDuplicateNetexIds(String reportId, String filename, Set<String> localIds);

    /**
     * Return the set of NeTEx ids declared in the common files within the dataset.
     * @param reportId unique id of the current validation report.
     * @return the set of NeTEx ids declared in the common files within the dataset.
     */
    Set<String> getSharedNetexIds(String reportId);

    /**
     * Add ids to the set of common ids for the given validation report ids
     * @param reportId unique id of the current validation report.
     * @param commonIds NeTEx ids to be added.
     */
    void addSharedNetexIds(String reportId, Set<IdVersion> commonIds);

    void cleanUp(String reportId);
}
