package pl.lingaro.od.workshop.security.config;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
class GlobalDefaultExceptionHandler {
    private static final Logger LOG = Logger.getLogger(GlobalDefaultExceptionHandler.class.getName());
    private final EntityManager entityManager;

    GlobalDefaultExceptionHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @ExceptionHandler(value = Exception.class)
    @Transactional
    public ModelAndView defaultErrorHandler(Exception e, Principal principal)
            throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        final String id = UUID.randomUUID().toString();
        LOG.log(Level.SEVERE, id, e);

        return new ModelAndView("/error", Collections.singletonMap("id",id));
    }

}