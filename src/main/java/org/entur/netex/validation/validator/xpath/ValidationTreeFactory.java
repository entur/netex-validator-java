package org.entur.netex.validation.validator.xpath;

import org.entur.netex.validation.validator.xpath.tree.ValidationTreeBuilder;

/**
 * Build a tree of XPath validation rules.
 */
public interface ValidationTreeFactory {
  /**
   * Build a tree of XPath validation rules.
   * @return a tree of XPath validation rules.
   */
  ValidationTreeBuilder builder();
}
