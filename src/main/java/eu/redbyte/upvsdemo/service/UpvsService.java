package eu.redbyte.upvsdemo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.gov.schemas.identity.identitydata._1.IdentityData;
import sk.gov.schemas.identity.service._1.CorporateBodyRequestType;
import sk.gov.schemas.identity.service._1.GetEdeskInfoRequest;
import sk.gov.schemas.identity.service._1.GetEdeskInfoResponse;
import sk.gov.schemas.identity.service._1.IdentityRequestType;
import sk.gov.schemas.identity.service._1_7.IdentityServices;

/**
 * @author Michal Sabo
 */
@Component
public class UpvsService {

    private static Logger log = LoggerFactory.getLogger(UpvsService.class);

    @Autowired
    private IdentityServices identityServices;

    public String getEdeskStatusByIco(String ico) {
        try {
            GetEdeskInfoRequest request = new GetEdeskInfoRequest();
            IdentityRequestType requestType = new IdentityRequestType();
            CorporateBodyRequestType cbrt = new CorporateBodyRequestType();
            cbrt.setCompanyRegistrationNumber(ico);
            requestType.setCorporateBody(cbrt);
            request.setIdentityRequest(requestType);

            GetEdeskInfoResponse response = identityServices.getEdeskInfo(request);

            if (response.getIdentityData().size() > 0) {
                IdentityData identityData = response.getIdentityData().iterator().next();
                if (identityData.getUPVSAttributes() != null) {
                    return identityData.getUPVSAttributes().getEDeskStatus().name();
                }
            }
        } catch (Exception e) {
            log.error("Neocakavana chyba", e);
            throw new RuntimeException("Neocakavana chyba", e);
        }

        return "UNKNOWN";
    }
}
