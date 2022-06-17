package org.entur.netex.validation.validator.schema;

import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntrySeverity;
import org.entur.netex.validation.xml.NetexSchemaRepository;
import org.rutebanken.netex.validation.NeTExValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Validate NeTEx files against the NeTEx XML schema.
 */
public class NetexSchemaValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetexSchemaValidator.class);

    private final NetexSchemaRepository netexSchemaRepository;
    private final int maxValidationReportEntries;


    /**
     * @param maxValidationReportEntries the maximum number of entries reported. Additional entries are ignored.
     */
    public NetexSchemaValidator(int maxValidationReportEntries) {
        this.netexSchemaRepository = new NetexSchemaRepository();
        this.maxValidationReportEntries = maxValidationReportEntries;
    }

    public List<ValidationReportEntry> validateSchema(String fileName, byte[] content) {
        LOGGER.debug("Validating file {}", fileName);
        List<ValidationReportEntry> validationReportEntries = new ArrayList<>();
        try {
            NeTExValidator.NetexVersion schemaVersion = NetexSchemaRepository.detectNetexSchemaVersion(content);
            if (schemaVersion == null) {
                schemaVersion = NeTExValidator.LATEST;
                LOGGER.warn("Could not detect schema version for file {}, defaulting to latest ({}})", fileName, schemaVersion);
            }
            Validator validator = netexSchemaRepository.getNetexSchema(schemaVersion).newValidator();
            validator.setErrorHandler(new ErrorHandler() {

                private int errorCount;

                @Override
                public void warning(SAXParseException exception) throws SAXParseException {
                    addValidationReportEntry(fileName, exception, ValidationReportEntrySeverity.WARNING);
                    errorCount++;
                }

                @Override
                public void error(SAXParseException exception) throws SAXParseException {
                    addValidationReportEntry(fileName, exception, ValidationReportEntrySeverity.CRITICAL);
                    errorCount++;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXParseException {
                    error(exception);
                }

                private void addValidationReportEntry(String fileName, SAXParseException saxParseException, ValidationReportEntrySeverity severity) throws SAXParseException {
                    if (errorCount < maxValidationReportEntries) {
                        String message = saxParseException.getMessage();
                        DataLocation dataLocation = getErrorLocation(fileName, saxParseException);
                        validationReportEntries.add(new ValidationReportEntry(message, "NETEX_SCHEMA", severity, dataLocation));
                    } else {
                        LOGGER.warn("File {} has too many schema validation errors (max is {}). Additional errors will not be reported.", fileName, maxValidationReportEntries);
                        throw saxParseException;
                    }

                }

            });

            validator.validate(new StreamSource(new ByteArrayInputStream(content)));
        } catch (IOException e) {
            throw new NetexValidationException(e);
        } catch (SAXException saxException) {
            LOGGER.info("Found schema validation errors");
        }

        return validationReportEntries;
    }

    private DataLocation getErrorLocation(String fileName, SAXParseException saxParseException) {
        return new DataLocation(null, fileName, saxParseException.getLineNumber(), saxParseException.getColumnNumber());
    }


}
