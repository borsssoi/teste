package org.gproman.ui;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

public interface UIPlugin {
    
    public abstract String getId();

    public abstract boolean isDirty();
    
    public abstract void setDirty( boolean dirty );

    public abstract void update();

    public abstract String getTitle();

    public abstract ImageIcon getIcon();

    public abstract ImageIcon getSmallIcon();

    public abstract String getDescription();
    
    public abstract int getMnemonic();
    
    public abstract Category getCategory();
    
    public abstract JComponent getComponent();
    
    public abstract boolean requiresScrollPane();

    public abstract boolean isEnabledByDefault();
    
    public abstract boolean requiresConfiguration();
    
    public abstract UIConfPlugin getConfigurationPlugin();
    
    public abstract boolean isEnabled();
    
    public abstract void setEnabled( boolean enabled );

    public abstract ChangeListener getChangeListener(Component container);
    
    public abstract boolean hasContext();

    public abstract UIPluginContext getContext();
    
    public abstract void setContext( UIPluginContext ctx );
    
    public static interface UIPluginContext extends Serializable { 
    }

}