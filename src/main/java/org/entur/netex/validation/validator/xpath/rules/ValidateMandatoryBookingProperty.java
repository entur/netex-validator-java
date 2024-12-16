package org.entur.netex.validation.validator.xpath.rules;

import static org.entur.netex.validation.validator.Severity.ERROR;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;

/**
 * Validate the booking properties against the Nordic NeTEx profile.
 */
public class ValidateMandatoryBookingProperty
  extends AbstractXPathValidationRule {

  private final ValidationRule rule;

  private final String bookingProperty;

  public ValidateMandatoryBookingProperty(String bookingProperty) {
    this.bookingProperty = Objects.requireNonNull(bookingProperty);
    rule =
      new ValidationRule(
        "BOOKING_4",
        "Booking property",
        "Mandatory booking property %s not specified on FlexibleServiceProperties, FlexibleLine or on all StopPointInJourneyPatterns".formatted(
            bookingProperty
          ),
        ERROR
      );
  }

  @Override
  public List<ValidationIssue> validate(
    XPathRuleValidationContext validationContext
  ) {
    try {
      List<XdmValue> errorNodes = new ArrayList<>();
      XPathSelector missingFieldSelector = validationContext
        .getNetexXMLParser()
        .getXPathCompiler()
        .compile(
          "ServiceFrame/lines/FlexibleLine and " +
          "ServiceFrame/lines/FlexibleLine[not(" +
          bookingProperty +
          ")]"
        )
        .load();
      missingFieldSelector.setContextItem(validationContext.getXmlNode());
      boolean missingField = missingFieldSelector.effectiveBooleanValue();
      if (missingField) {
        XPathSelector selector = validationContext
          .getNetexXMLParser()
          .getXPathCompiler()
          .compile(
            "ServiceFrame/journeyPatterns/*[self::JourneyPattern][pointsInSequence/StopPointInJourneyPattern[not(BookingArrangements/" +
            bookingProperty +
            ")]]"
          )
          .load();
        selector.setContextItem(validationContext.getXmlNode());
        XdmValue nodes = selector.evaluate();

        for (XdmItem item : nodes) {
          if (item instanceof XdmNode node) {
            String id = node.getAttributeValue(QName.fromEQName("id"));
            String version = node.getAttributeValue(
              QName.fromEQName("version")
            );

            XPathSelector sjSelector = validationContext
              .getNetexXMLParser()
              .getXPathCompiler()
              .compile(
                "TimetableFrame/vehicleJourneys/ServiceJourney[(not(FlexibleServiceProperties) or not(FlexibleServiceProperties/" +
                bookingProperty +
                ")) and JourneyPatternRef/@ref='" +
                id +
                "' and JourneyPatternRef/@version='" +
                version +
                "']"
              )
              .load();
            sjSelector.setContextItem(validationContext.getXmlNode());
            XdmValue errorsForJP = sjSelector.evaluate();
            if (!errorsForJP.isEmpty()) {
              errorNodes.add(errorsForJP);
            }
          }
        }
      }
      List<ValidationIssue> validationIssues = new ArrayList<>();

      for (XdmValue errorNode : errorNodes) {
        for (XdmItem item : errorNode) {
          XdmNode xdmNode = (XdmNode) item;
          DataLocation dataLocation = getXdmNodeLocation(
            validationContext.getFileName(),
            xdmNode
          );
          validationIssues.add(new ValidationIssue(rule, dataLocation));
        }
      }
      return validationIssues;
    } catch (SaxonApiException e) {
      throw new NetexValidationException("Error while validating rule", e);
    }
  }

  @Override
  public ValidationRule rule() {
    return rule;
  }
}
