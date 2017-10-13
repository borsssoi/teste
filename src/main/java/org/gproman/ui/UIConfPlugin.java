package org.gproman.ui;

import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

public interface UIConfPlugin {

    public abstract boolean isDirty();

    public abstract void setDirty(boolean dirty);

    public abstract String getTitle();

    public abstract ImageIcon getIcon();

    public abstract String getDescription();

    public abstract int getMnemonic();

    public abstract JComponent getPluginComponent();

    public abstract boolean requiresScrollPane();

    public abstract boolean commit();

    public abstract boolean rollback();

    public abstract void setParent(JFrame parent);
    
    public abstract Properties getDefaultConfiguration();

}