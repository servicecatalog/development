/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 15.04.2011                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.oscm.ui.common.JSFUtils;
import org.oscm.validator.ADMValidator;
import org.oscm.internal.types.exception.ValidationException;
import org.oscm.internal.types.exception.ValidationException.ReasonEnum;

/**
 * Abstract base class for all id validators. The general validation is similar
 * for all ID fields. The specific attributes of the validation are defined by
 * the subclasses (e.g. the maximum allowd length).
 * 
 * @author Florian Walker
 * 
 */
public abstract class IdValidator implements Validator {

    /**
     * Abstract method which must be overwritten by the subclasses to indicate
     * the maximum length of the individual ID char field.
     * 
     * @return the maximum length of the ID.
     */
    protected abstract int getMaxLength();

    /**
     * Validates that the given value contains only valid characters @see
     * org.oscm.validator#containsOnlyValidIdChars(String).
     * 
     * @param context
     *            FacesContext for the request we are processing
     * @param component
     *            UIComponent we are checking for correctness
     * @param value
     *            the value to validate
     * @throws ValidatorException
     *             if validation fails
     */
    public void validate(FacesContext facesContext, UIComponent component,
            Object value) throws ValidatorException {
        if (value == null) {
            return;
        }
        String id = value.toString();
        if (id.length() == 0) {
            return;
        }
        if (id.length() > getMaxLength()) {
            String label = JSFUtils.getLabel(component);
            Object[] args = new Object[] { label,
                    String.valueOf(getMaxLength()) };
            ValidationException e = new ValidationException(ReasonEnum.LENGTH,
                    label, null);
            String text = JSFUtils.getText(e.getMessageKey(), args,
                    facesContext);
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, text, null));
        }
        if (!ADMValidator.containsOnlyValidIdChars(id)) {
            String label = JSFUtils.getLabel(component);
            Object[] args = new Object[] { label };
            ValidationException e = new ValidationException(ReasonEnum.ID_CHAR,
                    label, null);
            String text = JSFUtils.getText(e.getMessageKey(), args,
                    facesContext);
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, text, null));
        }
    }

}
