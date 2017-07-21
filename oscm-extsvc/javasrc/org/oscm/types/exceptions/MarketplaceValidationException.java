package org.oscm.types.exceptions;

/**
 * Created by PLGrubskiM on 2017-07-21.
 */
public class MarketplaceValidationException extends SaaSApplicationException{

    public MarketplaceValidationException() {
        super();
    }

    public MarketplaceValidationException(String message) {
        super(message);
    }
}
