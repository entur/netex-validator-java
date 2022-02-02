package org.entur.netex.validation.validator.xpath;

/**
 * A validation finding returned by an XPath validation rule.
 */
public class XPathValidationReportEntry {

    private final String code;
    private final String message;
    private final String fileName;

    public XPathValidationReportEntry(String message, String code, String fileName) {
        this.code = code;
        this.message = message;
        this.fileName = fileName;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getFileName() {
        return fileName;
    }
}
