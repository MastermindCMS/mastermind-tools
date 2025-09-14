package com.mastermindcms.license.services;

import org.springframework.context.event.ContextRefreshedEvent;

public interface LicenseValidationService {

    /**
     * This method initially call checks and validation for license key
     * when the spring application is started.
      * @param event event handling when an application starts up
     */
    void onApplicationEvent(ContextRefreshedEvent event);
    /**
     * This method does checks and validation for license key.
     *
     * @return version is valid or not valid
     */
    boolean validateLicenseKey();

    /**
     * This method performs a licence key check.
     */
    void performLicenseValidation();
}
