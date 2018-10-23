package eu.redbyte.upvsdemo.rest.identity;

import java.io.Serializable;

/**
 * @author Michal Sabo
 */
public class IdentityResult implements Serializable {
    private String ico;
    private String result;

    public IdentityResult(String ico, String result) {
        this.ico = ico;
        this.result = result;
    }

    public String getIco() {
        return ico;
    }

    public String getResult() {
        return result;
    }
}
