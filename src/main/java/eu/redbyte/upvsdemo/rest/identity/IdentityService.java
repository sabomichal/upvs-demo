package eu.redbyte.upvsdemo.rest.identity;

import eu.redbyte.upvsdemo.service.UpvsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Michal Sabo
 */
@Path("/")
@Service
public class IdentityService {

    private static final Logger log = LoggerFactory.getLogger(IdentityService.class);

    @Autowired
    private UpvsService upvsService;

    @GET
    @Path("status/{ico}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public IdentityResult status(@PathParam("ico") String ico) {
        try {
            String result = upvsService.getEdeskStatusByIco(ico);
            log.info("Status schranky pre ICO={}: {}", ico, result);
            return new IdentityResult(ico, result);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error has occured", e);
        }
    }
}
