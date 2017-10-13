package org.gproman.model;

public abstract class PersistentEntity {
    
    private Integer id;
    
    public PersistentEntity() {}
    
    public PersistentEntity( Integer id ) {
        this.id = id;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId( Integer id ) {
        this.id = id;
    }

}
