package org.gproman.model.everest;

import org.gproman.model.PersistentEntity;

public class EverestStatus extends PersistentEntity {

    private Integer schemaVersion;

    public EverestStatus() {
    }

    public EverestStatus(Integer id,
            Integer schemaVersion) {
        super(id);
        this.schemaVersion = schemaVersion != null ? schemaVersion : 1;
    }

    public Integer getSchemaVersion() {
        return schemaVersion;
    }

    public EverestStatus setSchemaVersion(Integer schemaVersion) {
        this.schemaVersion = schemaVersion;
        return this;
    }
}
