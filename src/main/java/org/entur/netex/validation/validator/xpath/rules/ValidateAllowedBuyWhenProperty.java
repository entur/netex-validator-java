package org.entur.netex.validation.validator.xpath.rules;

import org.rutebanken.netex.model.PurchaseMomentEnumeration;

/**
 * Validate the "buy when" access properties against the Nordic NeTEx profile.
 */
public class ValidateAllowedBuyWhenProperty extends ValidateNotExist {

    private static final String VALID_BUY_WHEN_PROPERTIES = "'" + String.join("','",
            PurchaseMomentEnumeration.ON_RESERVATION.value(),
            PurchaseMomentEnumeration.BEFORE_BOARDING.value(),
            PurchaseMomentEnumeration.AFTER_BOARDING.value(),
            PurchaseMomentEnumeration.ON_CHECK_OUT.value())
            + "'";

    private static final String MESSAGE = "Illegal value for BuyWhen";

    public ValidateAllowedBuyWhenProperty(String context) {
        super(context + "/BuyWhen[tokenize(.,' ')[not(. = (" + VALID_BUY_WHEN_PROPERTIES + "))]]", MESSAGE, "BUY_WHEN_1");
    }
}
