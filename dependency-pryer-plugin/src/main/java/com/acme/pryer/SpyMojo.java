package com.acme.pryer;

import com.alibaba.fastjson.JSON;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.util.List;
import java.util.Set;

/**
 * 插件启动类
 *
 * @author: cdchenmingxuan
 * @date: 2019/7/1 10:15
 * @description: maven-spyer-plugin
 */
@Mojo(name = "spy")
public class SpyMojo extends AbstractMojo {

    @Parameter(property = "spy.group", defaultValue = "")
    private String groupExp;

    @Parameter(property = "spy.artifact", defaultValue = "")
    private String artifactExp;

    @Parameter(property = "spy.filterModels")
    private List<String> filterModels;

    @Parameter(
            defaultValue = "${project}",
            readonly = true,
            required = true
    )
    protected MavenProject project;

    @Parameter( defaultValue = "${reactorProjects}", readonly = true, required = true )
    private List<MavenProject> reactorProjects;

    @Component( hint = "default" )
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;

    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> innerModels = project.getModules();
        getLog().info("filterModels：" + JSON.toJSONString(innerModels));

        getLog().info("reactorProjects：" + JSON.toJSONString(reactorProjects));

        ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        buildingRequest.setProject(project);

        // non-verbose mode use dependency graph component, which gives consistent results with Maven version
        // running
        try {
            DependencyNode rootNode = dependencyGraphBuilder.buildDependencyGraph(buildingRequest, null, reactorProjects);
            getLog().info("rootNode：" + JSON.toJSONString(rootNode));
        } catch (Exception e) {
        }

        Set dependencyArtifacts = project.getDependencyArtifacts();
        getLog().info("dependencyArtifacts：" + JSON.toJSONString(dependencyArtifacts));

        Set<Artifact> artifacts = project.getArtifacts();
        getLog().info("artifacts：" + JSON.toJSONString(artifacts));
    }
}
