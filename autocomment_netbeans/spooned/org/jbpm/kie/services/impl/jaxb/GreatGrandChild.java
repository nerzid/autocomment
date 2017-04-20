

package org.jbpm.kie.services.impl.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.PROPERTY)
public class GreatGrandChild {
    public String song;

    public String og;

    private String getSong() {
        return song;
    }

    private void setSong(String song) {
        this.song = song;
    }
}

