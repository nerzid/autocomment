

package org.jbpm.persistence.scripts.quartzmockentities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "qrtz_triggers")
public class QrtzTriggers {
    @Id
    private Long id;

    public QrtzTriggers() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        QrtzTriggers.this.id = id;
    }
}

