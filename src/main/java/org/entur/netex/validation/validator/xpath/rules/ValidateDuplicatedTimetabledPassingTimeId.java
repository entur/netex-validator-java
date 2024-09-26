package org.entur.netex.validation.validator.xpath.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.saxon.s9api.*;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.validator.xpath.XPathValidationReportEntry;

/**
 * Validate that there is no duplicated TimetabledPassingTimes NeTEx id within a Line file.
 * For most NeTEx entities, the NeTEx XSD verifies that IDs are unique, but no such check exists for
 * TimetabledPassingTimes.
 */
public class ValidateDuplicatedTimetabledPassingTimeId
  extends AbstractXPathValidationRule {

  private static final String MESSAGE =
    "Non-unique NeTEx id for TimetabledPassingTime";
  public static final String RULE_NAME = "SERVICE_JOURNEY_17";
  private final String context;

  public ValidateDuplicatedTimetabledPassingTimeId(String context) {
    this.context = context;
  }

  @Override
  public List<XPathValidationReportEntry> validate(
    XPathRuleValidationContext validationContext
  ) {
    try {
      XPathSelector selector = validationContext
        .getNetexXMLParser()
        .getXPathCompiler()
        .compile(
          context +
          "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[@id]"
        )
        .load();
      selector.setContextItem(validationContext.getXmlNode());
      XdmValue nodes = selector.evaluate();
      Set<String> foundIds = new HashSet<>();
      List<XPathValidationReportEntry> validationReportEntries =
        new ArrayList<>();
      for (XdmItem item : nodes) {
        XdmNode xdmNode = (XdmNode) item;
        String id = xdmNode.getAttributeValue(new QName("id"));
        if (foundIds.contains(id)) {
          DataLocation dataLocation = getXdmNodeLocation(
            validationContext.getFileName(),
            xdmNode
          );
          validationReportEntries.add(
            new XPathValidationReportEntry(MESSAGE, RULE_NAME, dataLocation)
          );
        } else {
          foundIds.add(id);
        }
      }
      return validationReportEntries;
    } catch (SaxonApiException e) {
      throw new NetexValidationException("Error while validating rule", e);
    }
  }

  @Override
  public String getMessage() {
    return MESSAGE;
  }

  @Override
  public String getCode() {
    return RULE_NAME;
  }
}
