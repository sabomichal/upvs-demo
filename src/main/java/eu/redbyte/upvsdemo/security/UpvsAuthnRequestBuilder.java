package eu.redbyte.upvsdemo.security;

import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.saml.sso.AuthnRequestBuilder;
import org.apache.cxf.rs.security.saml.sso.SamlpRequestComponentBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;

import java.util.Arrays;

/**
 * @author Michal Sabo
 */
public class UpvsAuthnRequestBuilder implements AuthnRequestBuilder {

    private static final String protocolBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    private static final String nameIDFormat = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
    private static final String passwordProtectedTransportClass = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
    private static final String smartCardPkiClass = "urn:oasis:names:tc:SAML:2.0:ac:classes:SmartcardPKI";

    private boolean forceAuthn;
    private boolean isPassive;

    public UpvsAuthnRequestBuilder(boolean forceAuthn, boolean isPassive) {
        this.forceAuthn = forceAuthn;
        this.isPassive = isPassive;
    }

    @Override
    public AuthnRequest createAuthnRequest(Message message, String issuerId, String assertionConsumerServiceAddress) throws Exception {
        Issuer issuer = SamlpRequestComponentBuilder.createIssuer(issuerId);

        // pokial nie je nameIDFormat transient, nevratia sa nam ziadne assertion atributy
        NameIDPolicy nameIDPolicy = SamlpRequestComponentBuilder.createNameIDPolicy(
                true, nameIDFormat, issuerId
        );

        AuthnContextClassRef authnCtxClassRefPwd = SamlpRequestComponentBuilder.createAuthnCtxClassRef(passwordProtectedTransportClass);
        // v produkcii uz je akceptovana len smartcard
        AuthnContextClassRef authnCtxClassRefPki = SamlpRequestComponentBuilder.createAuthnCtxClassRef(smartCardPkiClass);
        RequestedAuthnContext authnCtx = SamlpRequestComponentBuilder.createRequestedAuthnCtxPolicy(
                AuthnContextComparisonTypeEnumeration.MAXIMUM,
                Arrays.asList(authnCtxClassRefPwd, authnCtxClassRefPki), null
        );

        return SamlpRequestComponentBuilder.createAuthnRequest(
                assertionConsumerServiceAddress,
                forceAuthn,
                isPassive,
                // UPVS napriek dokumentacii HTTP-Redirect binding neakceptuje, co nam aj potvrdili
                protocolBinding,
                SAMLVersion.VERSION_20,
                issuer,
                nameIDPolicy,
                authnCtx
        );
    }
}
