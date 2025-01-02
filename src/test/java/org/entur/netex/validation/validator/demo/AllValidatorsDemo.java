package org.entur.netex.validation.validator.demo;

import java.util.List;
import org.entur.netex.validation.validator.NetexValidatorsRunner;
import org.entur.netex.validation.validator.XPathValidator;
import org.entur.netex.validation.validator.id.DefaultNetexIdRepository;
import org.entur.netex.validation.validator.id.NetexIdUniquenessValidator;
import org.entur.netex.validation.validator.id.NetexReferenceValidator;
import org.entur.netex.validation.validator.id.ReferenceToValidEntityTypeValidator;
import org.entur.netex.validation.validator.id.VersionOnLocalNetexIdValidator;
import org.entur.netex.validation.validator.id.VersionOnRefToLocalNetexIdValidator;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathRuleValidator;
import org.entur.netex.validation.validator.xpath.tree.PublicationDeliveryValidationTreeFactory;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * Demonstrate the configuration of all the validators provided by the library.
 */
public class AllValidatorsDemo {

  public static void main(String[] args) {
    // create a NeTEx XML Parser that ignores SiteFrame elements
    NetexXMLParser netexXMLParser = new NetexXMLParser();
    // create a NeTEx schema validator, limit the number of findings to 100
    NetexSchemaValidator netexSchemaValidator = new NetexSchemaValidator(100);

    // Configure all XPath validators
    List<XPathValidator> xPathValidators = List.of(
      new XPathRuleValidator(new PublicationDeliveryValidationTreeFactory()),
      new NetexIdUniquenessValidator(new DefaultNetexIdRepository()),
      new NetexReferenceValidator(new DefaultNetexIdRepository(), List.of()),
      new ReferenceToValidEntityTypeValidator(),
      new VersionOnLocalNetexIdValidator(),
      new VersionOnRefToLocalNetexIdValidator()
    );

    NetexValidatorsRunner netexValidatorsRunner = NetexValidatorsRunner
      .of()
      .withNetexXMLParser(netexXMLParser)
      .withNetexSchemaValidator(netexSchemaValidator)
      .withXPathValidators(xPathValidators)
      .build();

    netexValidatorsRunner
      .getRuleDescriptionByCode()
      .forEach((key, value) -> System.out.println(key + " | " + value));
  }
}
