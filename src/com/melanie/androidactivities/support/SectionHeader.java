package com.melanie.androidactivities.support;

import java.io.Serializable;

public class SectionHeader implements Serializable{

    private static final long serialVersionUID = -7539641282828977852L;

    private String sectionText;

    public SectionHeader(String sectionText) {
        super();
        this.sectionText = sectionText;
    }

    public String getSectionText() {
        return sectionText;
    }

    public void setSectionText(String sectionText) {
        this.sectionText = sectionText;
    }

}
