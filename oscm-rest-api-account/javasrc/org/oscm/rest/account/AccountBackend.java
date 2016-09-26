package org.oscm.rest.account;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.oscm.internal.intf.AccountService;
import org.oscm.internal.intf.OperatorService;
import org.oscm.internal.types.exception.DomainObjectException.ClassEnum;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOBillingContact;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.internal.vo.VOPaymentInfo;
import org.oscm.rest.account.data.AccountRepresentation;
import org.oscm.rest.account.data.BillingContactRepresentation;
import org.oscm.rest.account.data.OrganizationRepresentation;
import org.oscm.rest.account.data.PaymentInfoRepresentation;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;

@Stateless
public class AccountBackend {

    @EJB
    AccountService as;

    @EJB
    OperatorService os;

    public RestBackend.Post<BillingContactRepresentation, AccountParameters> postBillingContact() {
        return new RestBackend.Post<BillingContactRepresentation, AccountParameters>() {

            @Override
            public Object post(BillingContactRepresentation content, AccountParameters params) throws Exception {
                VOBillingContact vo = content.getVO();
                return String.valueOf(as.saveBillingContact(vo).getKey());
            }
        };
    }

    public RestBackend.GetCollection<BillingContactRepresentation, AccountParameters> getBillingContactCollection() {
        return new RestBackend.GetCollection<BillingContactRepresentation, AccountParameters>() {

            @Override
            public RepresentationCollection<BillingContactRepresentation> getCollection(AccountParameters params)
                    throws Exception {
                Collection<BillingContactRepresentation> list = BillingContactRepresentation.convert(as
                        .getBillingContacts());
                return new RepresentationCollection<BillingContactRepresentation>(list);
            }
        };
    }

    public RestBackend.Get<BillingContactRepresentation, AccountParameters> getBillingContact() {
        return new RestBackend.Get<BillingContactRepresentation, AccountParameters>() {

            @Override
            public BillingContactRepresentation get(AccountParameters params) throws Exception {
                BillingContactRepresentation item = null;
                List<VOBillingContact> list = as.getBillingContacts();
                for (VOBillingContact bc : list) {
                    if (bc.getKey() == params.getId().longValue()) {
                        item = new BillingContactRepresentation(bc);
                        break;
                    }
                }
                if (item == null) {
                    throw new ObjectNotFoundException(ClassEnum.BILLING_CONTACT, String.valueOf(params.getId()
                            .longValue()));
                }
                return item;
            }
        };
    }

    public RestBackend.Put<BillingContactRepresentation, AccountParameters> putBillingContact() {
        return new RestBackend.Put<BillingContactRepresentation, AccountParameters>() {

            @Override
            public boolean put(BillingContactRepresentation content, AccountParameters params) throws Exception {
                as.saveBillingContact(content.getVO());
                return true;
            }

        };
    }

    public RestBackend.Delete<AccountParameters> deleteBillingContact() {
        return new RestBackend.Delete<AccountParameters>() {

            @Override
            public boolean delete(AccountParameters params) throws Exception {
                VOBillingContact bc = new VOBillingContact();
                bc.setKey(params.getId().longValue());
                as.deleteBillingContact(bc);
                return true;
            }
        };
    }

    public RestBackend.GetCollection<PaymentInfoRepresentation, AccountParameters> getPaymentInfoCollection() {
        return new RestBackend.GetCollection<PaymentInfoRepresentation, AccountParameters>() {

            @Override
            public RepresentationCollection<PaymentInfoRepresentation> getCollection(AccountParameters params)
                    throws Exception {
                Collection<PaymentInfoRepresentation> list = PaymentInfoRepresentation.convert(as.getPaymentInfos());
                return new RepresentationCollection<PaymentInfoRepresentation>(list);
            }
        };
    }

    public RestBackend.Put<PaymentInfoRepresentation, AccountParameters> putPaymentInfo() {
        return new RestBackend.Put<PaymentInfoRepresentation, AccountParameters>() {

            @Override
            public boolean put(PaymentInfoRepresentation content, AccountParameters params) throws Exception {
                as.savePaymentInfo(content.getVO());
                return true;
            }
        };
    }

    public RestBackend.Get<PaymentInfoRepresentation, AccountParameters> getPaymentInfo() {
        return new RestBackend.Get<PaymentInfoRepresentation, AccountParameters>() {

            @Override
            public PaymentInfoRepresentation get(AccountParameters params) throws Exception {
                PaymentInfoRepresentation item = null;
                List<VOPaymentInfo> list = as.getPaymentInfos();
                for (VOPaymentInfo pi : list) {
                    if (pi.getKey() == params.getId().longValue()) {
                        item = new PaymentInfoRepresentation(pi);
                        break;
                    }
                }
                if (item == null) {
                    throw new ObjectNotFoundException(ClassEnum.PAYMENT_INFO,
                            String.valueOf(params.getId().longValue()));
                }
                return item;
            }
        };
    }

    public RestBackend.Delete<AccountParameters> deletePaymentInfo() {
        return new RestBackend.Delete<AccountParameters>() {

            @Override
            public boolean delete(AccountParameters params) throws Exception {
                VOPaymentInfo pi = new VOPaymentInfo();
                pi.setKey(params.getId().longValue());
                as.deletePaymentInfo(pi);
                return true;
            }
        };
    }

    public RestBackend.Post<AccountRepresentation, AccountParameters> postOrganization() {
        return new RestBackend.Post<AccountRepresentation, AccountParameters>() {

            @Override
            public Object post(AccountRepresentation content, AccountParameters params) throws Exception {
                VOOrganization org;
                if (content.isSelfRegistration()) {
                    // TODO: this is available public
                    org = as.registerCustomer(content.getOrganization().getVO(), content.getUser().getVO(),
                            content.getPassword(), content.getServiceKey(), params.getMarketplaceId(),
                            content.getSellerId());
                } else if (content.isCustomerRegistration()) {
                    org = as.registerKnownCustomer(content.getOrganization().getVO(), content.getUser().getVO(),
                            content.getProps(), params.getMarketplaceId());
                } else {
                    org = os.registerOrganization(content.getOrganization().getVO(), null, content.getUser().getVO(),
                            content.getProps(), params.getMarketplaceId(), content.getOrganizationRoles());
                }
                if (org == null) {
                    // registration of a known customer has a suspending trigger
                    // active
                    return null;
                }
                return org.getOrganizationId();
            }
        };
    }

    public RestBackend.Get<OrganizationRepresentation, AccountParameters> getOrganization() {
        return new RestBackend.Get<OrganizationRepresentation, AccountParameters>() {

            @Override
            public OrganizationRepresentation get(AccountParameters params) throws Exception {
                VOOrganization org = as.getOrganizationData();
                OrganizationRepresentation item;
                if (org.getOrganizationId().equals(params.getOrgId())) {
                    item = new OrganizationRepresentation(org);
                } else {
                    item = new OrganizationRepresentation(os.getOrganization(params.getOrgId()));
                }
                return item;
            }

        };
    }
}
