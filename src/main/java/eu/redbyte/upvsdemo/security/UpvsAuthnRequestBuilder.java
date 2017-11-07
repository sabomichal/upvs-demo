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

	// UPVS podporuje len POST binding
	public static final String PROTOCOL_BINDING_POST = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
	public static final String PROTOCOL_BINDING_REDIRECT = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
	public static final String NAMEID_FORMAT_TRANSIENT = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
	public static final String NAMEID_FORMAT_PERSISTENT = "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent";

	private boolean forceAuthn;
	private boolean isPassive;
	private AuthnContextComparisonTypeEnumeration authnContextComparisonType;
	private String protocolBinding;
	private String nameIDFormat;

	public UpvsAuthnRequestBuilder(boolean forceAuthn, boolean isPassive, AuthnContextComparisonTypeEnumeration authnContextComparisonType, String protocolBinding, String nameIDFormat) {
		this.forceAuthn = forceAuthn;
		this.isPassive = isPassive;
		this.authnContextComparisonType = authnContextComparisonType;
		this.protocolBinding = protocolBinding;
		this.nameIDFormat = nameIDFormat;
	}

	@Override
	public AuthnRequest createAuthnRequest(Message message, String issuerId, String assertionConsumerServiceAddress) throws Exception {
		Issuer issuer = SamlpRequestComponentBuilder.createIssuer(issuerId);
		NameIDPolicy nameIDPolicy = SamlpRequestComponentBuilder.createNameIDPolicy(
						// pokial nie je nameIDFormat transient, nevratia sa nam ziadne assertion atributy
						true, nameIDFormat, issuerId);
		AuthnContextClassRef authnCtxClassRefPwd = SamlpRequestComponentBuilder.createAuthnCtxClassRef(
						"urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
		AuthnContextClassRef authnCtxClassRefPki = SamlpRequestComponentBuilder.createAuthnCtxClassRef(
				"urn:oasis:names:tc:SAML:2.0:ac:classes:SmartcardPKI");
		RequestedAuthnContext authnCtx = SamlpRequestComponentBuilder.createRequestedAuthnCtxPolicy(authnContextComparisonType,
				Arrays.asList(authnCtxClassRefPwd, authnCtxClassRefPki), null);

		return SamlpRequestComponentBuilder.createAuthnRequest(
				assertionConsumerServiceAddress,
				forceAuthn,
				isPassive,
				protocolBinding,
				SAMLVersion.VERSION_20,
				issuer,
				nameIDPolicy,
				authnCtx
		);
	}
}
