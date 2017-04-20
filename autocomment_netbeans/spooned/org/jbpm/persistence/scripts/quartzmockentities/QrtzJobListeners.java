

package org.jbpm.persistence.scripts.quartzmockentities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "qrtz_job_listeners")
public class QrtzJobListeners {
    @Id
    private Long id;

    public QrtzJobListeners() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

