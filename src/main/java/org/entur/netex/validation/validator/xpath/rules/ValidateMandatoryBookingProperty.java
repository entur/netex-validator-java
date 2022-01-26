package org.entur.netex.validation.validator.xpath.rules;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntrySeverity;
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate the booking properties against the Nordic NeTEx profile.
 */
public class ValidateMandatoryBookingProperty extends AbstractXPathValidationRule {

    private static final String MESSAGE_FORMAT = "Mandatory booking property %s not specified on FlexibleServiceProperties, FlexibleLine or on all StopPointInJourneyPatterns";
    public static final ValidationReportEntrySeverity SEVERITY = ValidationReportEntrySeverity.ERROR;
    public static final String RULE_NAME = "BOOKING_4";

    private final String bookingProperty;

    public ValidateMandatoryBookingProperty(String bookingProperty) {
        this.bookingProperty = bookingProperty;
    }

    @Override
    public List<ValidationReportEntry> validate(XPathValidationContext validationContext) {
        try {
            List<XdmValue> errorNodes = new ArrayList<>();
            XPathSelector missingFieldSelector = validationContext.getNetexXMLParser().getXPathCompiler().compile("lines/FlexibleLine and lines/FlexibleLine[not(" + bookingProperty + ")]").load();
            missingFieldSelector.setContextItem(validationContext.getXmlNode());
            boolean missingField = missingFieldSelector.effectiveBooleanValue();
            if (missingField) {
                XPathSelector selector = validationContext.getNetexXMLParser().getXPathCompiler().compile("journeyPatterns/*[self::JourneyPattern][pointsInSequence/StopPointInJourneyPattern[not(BookingArrangements/" + bookingProperty + ")]]").load();
                selector.setContextItem(validationContext.getXmlNode());
                XdmValue nodes = selector.evaluate();

                for (XdmValue value : nodes) {
                    if (value instanceof XdmNode) {
                        XdmNode node = (XdmNode) value;
                        String id = node.getAttributeValue(QName.fromEQName("id"));
                        String version = node.getAttributeValue(QName.fromEQName("version"));

                        XPathSelector sjSelector = validationContext.getNetexXMLParser().getXPathCompiler().compile("//vehicleJourneys/ServiceJourney[(not(FlexibleServiceProperties) or not(FlexibleServiceProperties/" + bookingProperty + ")) and JourneyPatternRef/@ref='" + id + "' and @version='" + version + "']").load();
                        sjSelector.setContextItem(validationContext.getXmlNode());
                        XdmValue errorsForJP = sjSelector.evaluate();
                        if (errorsForJP.size() > 0) {
                            errorNodes.add(errorsForJP);
                        }
                    }
                }
            }
            List<ValidationReportEntry> validationReportEntries = new ArrayList<>();

            for (XdmValue errorNode : errorNodes) {
                for (XdmItem item : errorNode) {
                    XdmNode xdmNode = (XdmNode) item;
                    String message = getXdmNodeLocation(xdmNode) + String.format(MESSAGE_FORMAT, bookingProperty);
                    validationReportEntries.add(new ValidationReportEntry(message,
                            RULE_NAME,
                            SEVERITY,
                            validationContext.getFileName()));
                }


            }
            return validationReportEntries;
        } catch (SaxonApiException e) {
            throw new NetexValidationException("Error while validating rule", e);
        }
    }

    @Override
    public String getMessage() {
        return MESSAGE_FORMAT;
    }

    @Override
    public String getName() {
        return RULE_NAME;
    }

    @Override
    public ValidationReportEntrySeverity getSeverity() {
        return SEVERITY;
    }
}
