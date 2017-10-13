package org.gproman.ui;

import javax.swing.JFrame;

import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;

/**
 * A top level interface for all UI plugins, enabling easy development of 
 * new functionality by isolating each UI plugin.
 */
public abstract class UIConfPluginBase 
        implements
        UIConfPlugin {

    private static final long         serialVersionUID = 1432737465462500043L;

    private boolean                   dirty            = false;

    protected JFrame                  parent;
    protected final UserCredentials   credentials;
    protected final UserConfiguration conf;

    public UIConfPluginBase(UserCredentials credentials,
                            UserConfiguration conf) {
        this.credentials = credentials;
        this.conf = conf;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public boolean requiresScrollPane() {
        return true;
    }

    public void setParent(JFrame parent) {
        this.parent = parent;
    }
    
    public JFrame getParent() {
        return this.parent;
    }
}