package org.gproman.ui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import org.gproman.db.DataService;
import org.gproman.model.UserConfiguration;
import org.gproman.util.ConfigurationManager;

/**
 * A top level interface for all UI plugins, enabling easy development of 
 * new functionality by isolating each UI plugin.
 */
public abstract class UIPluginBase extends JPanel implements UIPlugin  {

    private static final long serialVersionUID = 1432737465462500043L;
    
    private static final String UIPLUGIN_BASE = "uiplugin."; 

    protected final GPROManFrame gproManFrame;
    protected final DataService db;

    private boolean dirty = false;
    private boolean enabled;

    private String propertyName;

    public UIPluginBase( GPROManFrame gproManFrame,
                         DataService db ) {
        this.gproManFrame = gproManFrame;
        this.db = db;
        this.propertyName = UIPLUGIN_BASE+getId().trim()+".enabled";
        this.enabled = isEnabledAtStart();
    }
    
    public void setDirty( boolean dirty ) {
        this.dirty  = dirty;
    }
    
    @Override
    public boolean isDirty() {
        return this.dirty;
    }
    
    @Override
    public JComponent getComponent() {
        return this;
    }
    
    @Override
    public boolean requiresScrollPane() {
        return true;
    }
    
    @Override
    public boolean requiresConfiguration() {
        return false;
    }
    
    @Override
    public UIConfPlugin getConfigurationPlugin() {
        return null;
    }
    
    @Override
    public ChangeListener getChangeListener(Component component) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        UserConfiguration c = this.gproManFrame.getConfiguration();
        c.setProperty(propertyName, String.valueOf(enabled));
        ConfigurationManager.saveConfiguration(c);
        this.enabled = enabled;
    }
    
    @Override
    public String getId() {
        return getClass().getCanonicalName().substring(getClass().getCanonicalName().lastIndexOf('.')+1).toLowerCase();
    }
    
    public boolean isEnabledAtStart() {
        UserConfiguration c = this.gproManFrame.getConfiguration();
        String e = c.getProperty(propertyName);
        if( e != null ) {
            return Boolean.valueOf(e);
        } else {
            setEnabled(isEnabledByDefault());
            return isEnabled();
        }
    }
    
    @Override
    public boolean hasContext() {
        return false;
    }
    
    @Override
    public UIPluginContext getContext() {
        return null;
    }
    
    @Override
    public void setContext(UIPluginContext ctx) {
    }
    
}