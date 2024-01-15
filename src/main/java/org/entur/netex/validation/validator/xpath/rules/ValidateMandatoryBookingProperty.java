package org.entur.netex.validation.validator.xpath.rules;

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
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.validator.xpath.XPathValidationReportEntry;

/**
 * Validate the booking properties against the Nordic NeTEx profile.
 */
public class ValidateMandatoryBookingProperty
  extends AbstractXPathValidationRule {

  private static final String MESSAGE_FORMAT =
    "Mandatory booking property %s not specified on FlexibleServiceProperties, FlexibleLine or on all StopPointInJourneyPatterns";
  public static final String RULE_NAME = "BOOKING_4";

  private final String bookingProperty;
  private final String context;

  public ValidateMandatoryBookingProperty(
    String bookingProperty,
    String context
  ) {
    this.bookingProperty = Objects.requireNonNull(bookingProperty);
    this.context = Objects.requireNonNull(context);
  }

  @Override
  public List<XPathValidationReportEntry> validate(
    XPathValidationContext validationContext
  ) {
    try {
      List<XdmValue> errorNodes = new ArrayList<>();
      XPathSelector missingFieldSelector = validationContext
        .getNetexXMLParser()
        .getXPathCompiler()
        .compile(
          context +
          "ServiceFrame/lines/FlexibleLine and " +
          context +
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
            context +
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
                context +
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
      List<XPathValidationReportEntry> validationReportEntries =
        new ArrayList<>();

      for (XdmValue errorNode : errorNodes) {
        for (XdmItem item : errorNode) {
          XdmNode xdmNode = (XdmNode) item;
          DataLocation dataLocation = getXdmNodeLocation(
            validationContext.getFileName(),
            xdmNode
          );
          String message = String.format(MESSAGE_FORMAT, bookingProperty);
          validationReportEntries.add(
            new XPathValidationReportEntry(message, RULE_NAME, dataLocation)
          );
        }
      }
      return validationReportEntries;
    } catch (SaxonApiException e) {
      throw new NetexValidationException("Error while validating rule", e);
    }
  }

  @Override
  public String getMessage() {
    return String.format(MESSAGE_FORMAT, bookingProperty);
  }

  @Override
  public String getCode() {
    return RULE_NAME;
  }
}
