/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.merchant.exc.handler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;


@Deprecated
public class RestControllerExcHdl extends RepositoryRestExceptionHandler  {
    
    private final Log log = LogFactory.getLog(RestControllerExcHdl.class);

    public RestControllerExcHdl(MessageSource messageSource) {
        super(messageSource);
    }
    
}
