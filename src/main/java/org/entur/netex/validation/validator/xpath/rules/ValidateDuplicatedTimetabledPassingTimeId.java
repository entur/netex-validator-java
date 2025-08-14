package org.entur.netex.validation.validator.xpath.rules;

import static org.entur.netex.validation.validator.Severity.ERROR;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.saxon.s9api.*;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;

/**
 * Validate that there is no duplicated TimetabledPassingTimes NeTEx id within a Line file.
 * For most NeTEx entities, the NeTEx XSD verifies that IDs are unique, but no such check exists for
 * TimetabledPassingTimes.
 */
public class ValidateDuplicatedTimetabledPassingTimeId
  extends AbstractXPathValidationRule {

  static final ValidationRule RULE = new ValidationRule(
    "SERVICE_JOURNEY_17",
    "Non-unique NeTEx id for TimetabledPassingTime",
    ERROR
  );

  @Override
  public List<ValidationIssue> validate(XPathRuleValidationContext validationContext) {
    try {
      XPathSelector selector = validationContext
        .getNetexXMLParser()
        .getXPathCompiler()
        .compile("vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[@id]")
        .load();
      selector.setContextItem(validationContext.getXmlNode());
      XdmValue nodes = selector.evaluate();
      Set<String> foundIds = new HashSet<>();
      List<ValidationIssue> validationIssues = new ArrayList<>();
      for (XdmItem item : nodes) {
        XdmNode xdmNode = (XdmNode) item;
        String id = xdmNode.getAttributeValue(new QName("id"));
        if (foundIds.contains(id)) {
          DataLocation dataLocation = getXdmNodeLocation(
            validationContext.getFileName(),
            xdmNode
          );
          validationIssues.add(new ValidationIssue(RULE, dataLocation));
        } else {
          foundIds.add(id);
        }
      }
      return validationIssues;
    } catch (SaxonApiException e) {
      throw new NetexValidationException("Error while validating rule", e);
    }
  }

  @Override
  public ValidationRule rule() {
    return RULE;
  }
}
