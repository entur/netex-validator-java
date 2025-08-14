package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Construct a validation tree builder for ResourceFrames.
 */
public class DefaultResourceFrameValidationTreeFactory implements ValidationTreeFactory {

  public static final String CODE_OPERATOR_1 = "OPERATOR_1";
  public static final String CODE_OPERATOR_2 = "OPERATOR_2";
  public static final String CODE_OPERATOR_3 = "OPERATOR_3";
  public static final String CODE_OPERATOR_4 = "OPERATOR_4";
  public static final String CODE_OPERATOR_5 = "OPERATOR_5";
  public static final String CODE_OPERATOR_6 = "OPERATOR_6";
  public static final String CODE_OPERATOR_7 = "OPERATOR_7";
  public static final String CODE_AUTHORITY_1 = "AUTHORITY_1";
  public static final String CODE_AUTHORITY_2 = "AUTHORITY_2";
  public static final String CODE_AUTHORITY_3 = "AUTHORITY_3";
  public static final String CODE_AUTHORITY_4 = "AUTHORITY_4";
  public static final String CODE_AUTHORITY_5 = "AUTHORITY_5";

  @Override
  public ValidationTreeBuilder builder() {
    return new ValidationTreeBuilder("Resource Frame", "ResourceFrame")
      .withRule(
        new ValidateNotExist(
          "organisations/Operator[not(CompanyNumber) or normalize-space(CompanyNumber) = '']",
          CODE_OPERATOR_1,
          "Operator missing CompanyNumber",
          "Missing CompanyNumber element on Operator",
          Severity.INFO
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Operator[not(Name) or normalize-space(Name) = '']",
          CODE_OPERATOR_2,
          "Operator missing Name",
          "Missing Name on Operator",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Operator[not(LegalName) or normalize-space(LegalName) = '']",
          CODE_OPERATOR_3,
          "Operator missing LegalName",
          "Missing LegalName element on Operator",
          Severity.INFO
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Operator[not(ContactDetails)]",
          CODE_OPERATOR_4,
          "Operator missing ContactDetails",
          "Missing ContactDetails element on Operator",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Operator/ContactDetails[(not(Email) or normalize-space(Email) = '') and (not(Phone) or normalize-space(Phone) = '') and (not(Url) or normalize-space(Url) = '')]",
          CODE_OPERATOR_5,
          "Operator missing Url for ContactDetails",
          "At least one of Url, Phone or Email must be defined for ContactDetails on Operator",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Operator[not(CustomerServiceContactDetails)]",
          CODE_OPERATOR_6,
          "Operator missing CustomerServiceContactDetails",
          "Missing CustomerServiceContactDetails element on Operator",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Operator/CustomerServiceContactDetails[not(Url) or normalize-space(Url) = '']",
          CODE_OPERATOR_7,
          "Operator missing Url for CustomerServiceContactDetails",
          "Missing Url element for CustomerServiceContactDetails on Operator",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Authority[not(CompanyNumber) or normalize-space(CompanyNumber) = '']",
          CODE_AUTHORITY_1,
          "Authority missing CompanyNumber",
          "Missing CompanyNumber element on Authority",
          Severity.INFO
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Authority[not(Name) or normalize-space(Name) = '']",
          CODE_AUTHORITY_2,
          "Authority missing Name",
          "Missing Name element on Authority",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Authority[not(LegalName) or normalize-space(LegalName) = '']",
          CODE_AUTHORITY_3,
          "Authority missing LegalName",
          "Missing LegalName element on Authority",
          Severity.INFO
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Authority[not(ContactDetails)]",
          CODE_AUTHORITY_4,
          "Authority missing ContactDetails",
          "Missing ContactDetails on Authority",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "organisations/Authority/ContactDetails[not(Url) or not(starts-with(Url, 'http://') or (starts-with(Url, 'https://')) )]",
          CODE_AUTHORITY_5,
          "Authority missing Url for ContactDetails",
          "The Url must be defined for ContactDetails on Authority and it must start with 'http://' or 'https://'",
          Severity.ERROR
        )
      );
  }
}
