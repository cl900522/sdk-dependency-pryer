package com.acme.pryer.plugin.dto;

import java.util.List;

/**
 * 项目依赖信息
 *
 * @author: cdchenmingxuan
 * @date: 2019/7/2 14:15
 * @description: dependency-pryer
 */
public class ProjectDto extends ArtifactInfoDto {
    public ProjectDto() {

    }

    public ProjectDto(String groupId, String artifactId, String version) {
        super(groupId, artifactId, version);
        this.time = System.currentTimeMillis();
    }

    private Long time;

    private List<DependencyDto> dependencies;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public List<DependencyDto> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<DependencyDto> dependencies) {
        this.dependencies = dependencies;
    }
}
