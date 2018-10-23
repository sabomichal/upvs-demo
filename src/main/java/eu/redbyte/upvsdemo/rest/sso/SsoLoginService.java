package eu.redbyte.upvsdemo.rest.sso;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.saml.sso.SSOConstants;
import org.apache.cxf.rs.security.saml.sso.state.ResponseState;
import org.apache.cxf.rs.security.saml.sso.state.SPStateManager;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.saml.OpenSAMLUtil;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Michal Sabo
 */
@Path("/")
@Service
public class SsoLoginService {

    private static final Logger log = LoggerFactory.getLogger(SsoLoginService.class);

    @Autowired
    private SPStateManager stateProvider;

    @Context
    private MessageContext mc;

    @Context
    private HttpServletRequest request;

    @GET
    public String process(@CookieParam(SSOConstants.SECURITY_CONTEXT_TOKEN) String securityContextKey) {
        log.info("UPVS SAML WEB SSO assertion retrieved successfully");
        try {
            var actorId = retrieveAttribute(securityContextKey, UpvsSamlAttributes.ACTORID_ATTR);
            log.info("Attribute actorId retrieved: {}", actorId);
            return actorId;
        } catch (Exception e) {
            throw new InternalServerErrorException("Error has occured", e);
        }
    }

    private String retrieveAttribute(String securityContextKey, String attributeName) throws XMLStreamException, WSSecurityException {
        String assertionString = getAssertion(securityContextKey);
        ByteArrayInputStream tokenStream = new ByteArrayInputStream(assertionString.getBytes(StandardCharsets.UTF_8));
        Document responseDoc = StaxUtils.read(new InputStreamReader(tokenStream, StandardCharsets.UTF_8));
        Assertion assertion = (Assertion) OpenSAMLUtil.fromDom(responseDoc.getDocumentElement());

        if (assertion.getAttributeStatements().isEmpty()) {
            throw new BadRequestException();
        }

        AttributeStatement attributeStatement = assertion.getAttributeStatements().get(0);
        ImmutableMap<String, Attribute> attributeMap = Maps.uniqueIndex(attributeStatement.getAttributes(), Attribute::getName);
        Attribute upvsIdAttr = attributeMap.get(attributeName);
        if (upvsIdAttr == null || upvsIdAttr.getAttributeValues().size() != 1) {
            throw new BadRequestException();
        }
        return upvsIdAttr.getAttributeValues().get(0).getDOM().getTextContent();
    }

    private String getAssertion(String securityContextKey) {
        ResponseState responseState = stateProvider.getResponseState(securityContextKey);
        return responseState.getAssertion();
    }
}
