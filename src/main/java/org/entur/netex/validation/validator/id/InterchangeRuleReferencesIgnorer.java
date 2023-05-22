package org.entur.netex.validation.validator.id;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class InterchangeRuleReferencesIgnorer implements ExternalReferenceValidator {
    public InterchangeRuleReferencesIgnorer() {
    }

    public Set<IdVersion> validateReferenceIds(Set<IdVersion> externalIdsToValidate) {
        Objects.requireNonNull(externalIdsToValidate);
        return externalIdsToValidate.stream().filter(InterchangeRuleReferencesIgnorer::isIgnorableReferenceFromInterchangeRule).collect(Collectors.toUnmodifiableSet());
    }

    private static boolean isIgnorableReferenceFromInterchangeRule(IdVersion ref) {
        return "LineRef".equals(ref.getElementName()) && ref.getParentElementNames().contains("InterchangeRule");
    }
}
