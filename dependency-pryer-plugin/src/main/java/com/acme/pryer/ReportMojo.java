package com.acme.pryer;

import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.util.Locale;

/**
 * @author: cdchenmingxuan
 * @date: 2019/7/1 10:40
 * @description: maven-spyer-plugin
 */
public class ReportMojo extends AbstractMavenReport {
    protected void executeReport(Locale locale) throws MavenReportException {

    }

    public String getOutputName() {
        return null;
    }

    public String getName(Locale locale) {
        return null;
    }

    public String getDescription(Locale locale) {
        return null;
    }
}
