package org.oscm.internal.intf;

import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.ObjectNotFoundException;

import javax.ejb.Local;
import java.util.Collection;

/**
 * Interface for full text subsription search.
 */
@Local
public interface SubscriptionSearchService {
    Collection<Long> searchSubscriptions(String searchPhrase)
            throws InvalidPhraseException, ObjectNotFoundException;
}
