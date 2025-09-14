package com.mastermindcms.license.services;

import com.mastermindcms.license.config.LicenseConfiguration;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@Slf4j
@Service
public class LicenseValidationServiceImpl implements LicenseValidationService {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private LicenseConfiguration licenseConfiguration;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        performLicenseValidation();
    }

    @Override
    public boolean validateLicenseKey() {
        String licenseKey = licenseConfiguration.getLicenseKey();
        String licenseValidationUrl = licenseConfiguration.getLicenseValidationUrl();
        if(!licenseKey.isEmpty()){
            JSONObject body = new JSONObject(ofEntries(
                    entry("meta", ofEntries(
                            entry("key", licenseKey)
                    ))
            ));

            HttpResponse<JsonNode> res = Unirest.post(licenseValidationUrl)
                    .header("Content-Type", "application/vnd.api+json")
                    .header("Accept", "application/vnd.api+json")
                    .body(body)
                    .asJson();

            JSONObject data = res.getBody().getObject();
            JSONObject meta = data.getJSONObject("meta");

            try {
                InetAddress ip = InetAddress.getLocalHost();
                String hostname = ip.getHostName();
                JSONObject metadata = data.getJSONObject("data")
                        .getJSONObject("attributes").getJSONObject("metadata");
                String ipLicense = metadata.getString("ip");
                String ipCurrent = ip.getHostAddress();
                if (meta.getBoolean("valid") && ipLicense.equals(ipCurrent)) {
                    log.info("IP address from license: " + ipLicense);
                    log.info("Your current IP address : " + ipCurrent);
                    log.info("Your current Hostname : " + hostname);
                    return true;
                } else {
                    log.error("License key not valid");
                    return false;
                }
            } catch (UnknownHostException e) {
                log.error(e.getMessage());
            }
        } else {
            log.error("Licence key is not assigned");
            return false;
        }
        return false;
    }

    @Override
    public void performLicenseValidation() {
        boolean isValid = validateLicenseKey();
        if(!isValid){
            ((ConfigurableApplicationContext) context).close();
            System.exit(0);
        }
    }
}
