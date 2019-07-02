package com.acme.pryer.plugin.dto;

/**
 * @author: cdchenmingxuan
 * @date: 2019/7/2 14:22
 * @description: dependency-pryer
 */
public class DependencyDto extends ArtifactInfoDto {
    private String scope;

    public DependencyDto() {
    }

    public DependencyDto(String groupId, String artifactId, String version, String scope) {
        super(groupId, artifactId, version);
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
