

package org.jbpm.persistence.scripts.quartzmockentities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "qrtz_locks")
public class QrtzLocks {
    @Id
    private Long id;

    public QrtzLocks() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

