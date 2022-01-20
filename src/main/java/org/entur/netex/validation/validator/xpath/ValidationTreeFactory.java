package org.entur.netex.validation.validator.xpath;

/**
 * Build a tree of XPath validation rules.
 */
public interface ValidationTreeFactory {

    /**
     * Build a tree of XPath validation rules.
     * @return a tree of XPath validation rules.
     */
    ValidationTree buildValidationTree();
}
