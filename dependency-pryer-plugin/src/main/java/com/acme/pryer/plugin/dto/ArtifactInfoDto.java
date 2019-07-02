package com.acme.pryer.plugin.dto;

/**
 * 依赖数据
 *
 * @author: cdchenmingxuan
 * @date: 2019/7/2 14:16
 * @description: dependency-pryer
 */
public class ArtifactInfoDto {
    public ArtifactInfoDto() {

    }

    public ArtifactInfoDto(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String artifactId;

    private String version;

}
