package eu.redbyte.upvsdemo.security;


import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * @author Michal Sabo
 */
public class PasswordCallbackHandler implements CallbackHandler {

    private String username;
    private String password;

    public PasswordCallbackHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            WSPasswordCallback pwcb = (WSPasswordCallback) callback;
            String id = pwcb.getIdentifier();
            int usage = pwcb.getUsage();
            if (usage == WSPasswordCallback.DECRYPT || usage == WSPasswordCallback.SIGNATURE || usage == WSPasswordCallback.SECRET_KEY) {
                // used to retrieve password for private key
                if (username.equals(id)) {
                    pwcb.setPassword(password);
                }
            }
        }
    }
}