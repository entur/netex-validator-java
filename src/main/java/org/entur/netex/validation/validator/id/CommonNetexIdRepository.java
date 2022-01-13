package org.entur.netex.validation.validator.id;

import java.util.Set;

/**
 * Repository that stores NeTEx ids declared in the common files within the dataset.
 */
public interface CommonNetexIdRepository {

    /**
     * Return the set of NeTEx ids declared in the common files within the dataset.
     * @param reportId unique id of the current validation report.
     * @return the set of NeTEx ids declared in the common files within the dataset.
     */
    Set<String> getCommonNetexIds(String reportId);

    /**
     * Add ids to the set of common ids for the given validation report ids
     * @param reportId unique id of the current validation report.
     * @param commonIds NeTEx ids to be added.
     */
    void addCommonNetexIds(String reportId, Set<IdVersion> commonIds);

    /**
     * Clean up the repository for the current validation report.
     * All common ids are removed for this report.
     * @param reportId unique id of the current validation report.
     */
    void cleanUp(String reportId);
}
