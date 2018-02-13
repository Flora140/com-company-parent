package com.company.config.listener;

import com.company.security.conf.CompanyAuthConfigProvider;
import com.company.security.auth.Authenticator;
import com.company.security.auth.CompanyServerAuthModule;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SamRegistrationListener implements ServletContextListener {

    private static final Logger L = LoggerFactory.getLogger(SamRegistrationListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        L.info("Create Application '{}'", sce.getServletContext().getContextPath());

        try {
            AuthConfigFactory.getFactory().registerConfigProvider(
                    new CompanyAuthConfigProvider(
                            new CompanyServerAuthModule(Authenticator.newInstance())),
                    "HttpServlet",
                    sce.getServletContext().getVirtualServerName() + " " + sce.getServletContext().getContextPath(),
                    "Company authentication config provider");
        } catch (AuthException ex) {
            L.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        L.info("Destroyed '{}'", sce.getServletContext().getContextPath());
    }
}
