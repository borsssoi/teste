package org.gproman.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Properties;

public class UserCredentials
        implements
        Serializable,
        Cloneable {

    private static final long serialVersionUID = -3413714118460376915L;

    public static enum UserRole {
        ADMIN("Admin"), ADVANCED("Avançado"), STANDARD("Usuário"), GUEST("Convidado");
        
        public final String portuguese;
        private UserRole(String portuguese) {
            this.portuguese = portuguese;
        }
    }

    private String     gproUser;
    private String     gproPassword;
    private String     gproBrUser;
    private String     gproBrPassword;
    private Calendar   lastAuthentication;
    private UserRole   role;
    private Properties properties;

    public UserCredentials(String username,
            String password,
            String brUser,
            String brPassword,
            UserRole role) {
        this(username, password, brUser, brPassword, role, null, new Properties());
    }

    public UserCredentials(String username,
            String password,
            String brUser,
            String brPassword,
            UserRole role,
            Calendar lastAuthentication) {
        this(username, password, brUser, brPassword, role, null, new Properties());
    }

    public UserCredentials(String gproUser,
            String gproPassword,
            String gproBrUser,
            String gproBrPassword,
            UserRole role,
            Calendar lastAuthentication,
            Properties properties) {
        this.gproUser = gproUser;
        this.gproPassword = gproPassword;
        this.gproBrUser = gproBrUser;
        this.gproBrPassword = gproBrPassword;
        this.role = role;
        this.lastAuthentication = lastAuthentication;
        this.properties = properties;
    }

    public String getGproUser() {
        return gproUser;
    }

    public String getGproPassword() {
        return gproPassword;
    }

    public String getGproBrUser() {
        return gproBrUser;
    }

    public String getGproBrPassword() {
        return gproBrPassword;
    }

    public Calendar getLastAuthentication() {
        return lastAuthentication;
    }

    public void setLastAuthentication(Calendar lastAuthentication) {
        this.lastAuthentication = lastAuthentication;
    }

    public void setGproUser(String username) {
        this.gproUser = username;
    }

    public void setGproPassword(String password) {
        this.gproPassword = password;
    }

    public void setGproBrUser(String brUser) {
        this.gproBrUser = brUser;
    }

    public void setGproBrPassword(String brPassword) {
        this.gproBrPassword = brPassword;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gproBrPassword == null) ? 0 : gproBrPassword.hashCode());
        result = prime * result + ((gproBrUser == null) ? 0 : gproBrUser.hashCode());
        result = prime * result + ((gproPassword == null) ? 0 : gproPassword.hashCode());
        result = prime * result + ((gproUser == null) ? 0 : gproUser.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserCredentials other = (UserCredentials) obj;
        if (gproBrPassword == null) {
            if (other.gproBrPassword != null)
                return false;
        } else if (!gproBrPassword.equals(other.gproBrPassword))
            return false;
        if (gproBrUser == null) {
            if (other.gproBrUser != null)
                return false;
        } else if (!gproBrUser.equals(other.gproBrUser))
            return false;
        if (gproPassword == null) {
            if (other.gproPassword != null)
                return false;
        } else if (!gproPassword.equals(other.gproPassword))
            return false;
        if (gproUser == null) {
            if (other.gproUser != null)
                return false;
        } else if (!gproUser.equals(other.gproUser))
            return false;
        if (role != other.role)
            return false;
        return true;
    }

    @Override
    public UserCredentials clone() {
        return new UserCredentials(gproUser, gproPassword, gproBrUser, gproBrPassword, role, lastAuthentication, properties);
    }

}
