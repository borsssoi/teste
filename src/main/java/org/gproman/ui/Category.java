package org.gproman.ui;

import javax.swing.ImageIcon;

public enum Category {
    TOP("", "", "", "" ), 
    SEASON("Temporada ", "Informações sobre a temporada", "Temporada2_32.png", "Temporada2_16.png" ),
    REPORT("Relatórios ", "Ferramentas de relatórios", "reports_32.png", "reports_16.png" ),
    TEAM("Equipe ", "Informações sobre a equipe", "team_32.png", "team_16.png" ),
    TOOLS("Ferramentas ", "Ferramentas de auxílio ao gerente", "tools_32.png", "tools_16.png" ),
    CALC("Calculadoras ", "Calculadoras genéricas", "calculator_32.png", "calculator_16.png" ),
    EVEREST("Evereste ", "Ferramentas do projeto Evereste", "everest_32.png", "everest_16.png" );
    
    private final String label;
    private final String desc;
    private final String smallIconFile;
    private final String largeIconFile;
    private ImageIcon largeIcon;
    private ImageIcon smallIcon;
    
    private Category(String label, String desc, String largeIconFile, String smallIconFile ) {
        this.label = label;
        this.desc = desc;
        this.largeIconFile = largeIconFile;
        this.smallIconFile = smallIconFile;
    }
    
    public ImageIcon getSmallIcon() {
        if ( smallIcon == null ) {
            smallIcon = UIUtils.createImageIcon( "/icons/" + smallIconFile );
        }
        return smallIcon;
    }
    
    public ImageIcon getLargeIcon() {
        if ( largeIcon == null ) {
            largeIcon = UIUtils.createImageIcon( "/icons/" + largeIconFile );
        }
        return largeIcon;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String getDesc() {
        return this.desc;
    }
}