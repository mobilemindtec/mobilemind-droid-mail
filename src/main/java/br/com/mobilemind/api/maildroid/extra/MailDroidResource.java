package br.com.mobilemind.api.maildroid.extra;

/*
 * #%L
 * Mobile Mind - Mail Droid
 * %%
 * Copyright (C) 2012 Mobile Mind Empresa de Tecnologia
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */


import br.com.mobilemind.api.utils.log.MMLogger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Resource Util
 *
 * @author Ricardo Bocchi
 */
public class MailDroidResource {

    private static Properties PROPERTIES = new Properties();

    static {
        load();
    }

    private static void load() {
        InputStream in = null;
        try {
            in = MailDroidResource.class.getClassLoader().getResourceAsStream("mail_conf.properties");
            if (in != null) {
                PROPERTIES.load(in);
                if (MMLogger.isLogable()) {
                    MMLogger.log(Level.INFO, MailDroidResource.class, "mail_conf.properties loaded...");
                }
            } else {
                MMLogger.log(Level.WARNING, MailDroidResource.class, "mail_conf.properties not fount... loading resources_default.properties.");
            }
        } catch (IOException ex) {
            MMLogger.log(Level.SEVERE, MailDroidResource.class, ex.getMessage(), ex);
        }

        if (in == null) {
            try {
                in = MailDroidResource.class.getClassLoader().getResourceAsStream("mail_conf_default.properties");
                if (in != null) {
                    PROPERTIES.load(in);
                    if (MMLogger.isLogable()) {
                        MMLogger.log(Level.INFO, MailDroidResource.class, "mail_conf_default.properties loaded...");
                    }
                } else {
                    MMLogger.log(Level.SEVERE, MailDroidResource.class, "mail_conf_default.properties not found");
                }
            } catch (IOException ex) {
                MMLogger.log(Level.SEVERE, MailDroidResource.class, ex);
            }
        }
    }

    /**
     * get string in resources.properties
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        if (PROPERTIES.containsKey(key)) {
            return PROPERTIES.getProperty(key);
        }
        return "";
    }
}
