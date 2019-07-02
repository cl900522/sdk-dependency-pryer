package com.acme.pryer.plugin;

import com.acme.pryer.plugin.dto.DependencyDto;
import com.acme.pryer.plugin.dto.ProjectDto;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件启动类
 *
 * @author: cdchenmingxuan
 * @date: 2019/7/1 10:15
 * @description: maven-spyer-plugin
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.PACKAGE)
public class PryerMojo extends AbstractMojo {

    public static final int HTTP_SUCCESS = 200;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
    private List<MavenProject> reactorProjects;

    @Component(hint = "default")
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(property = "reportUrl", defaultValue = "http://pryer.acme.com/accept")
    private String reportUrl;

    @Parameter(property = "token", defaultValue = "")
    private String token;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        buildingRequest.setProject(project);

        try {
            DependencyNode rootNode = dependencyGraphBuilder.buildDependencyGraph(buildingRequest, null, reactorProjects);
            Artifact artifact = rootNode.getArtifact();
            ProjectDto projectDto = new ProjectDto(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());

            //递归依赖项和子依赖项组装后放入dependencyDtos
            List<DependencyDto> dependencyDtos = new ArrayList();
            List<DependencyNode> childrenDependencyNodes = rootNode.getChildren();
            transChildrenDependencyToDtos(childrenDependencyNodes, dependencyDtos);
            projectDto.setDependencies(dependencyDtos);

            getLog().info("Totally has [" + dependencyDtos.size() + "] dependencies.");

            String projectDtoJson = JSON.toJSONString(projectDto);
            getLog().info(projectDtoJson);

            reportDependency(projectDtoJson);
        } catch (Exception e) {
            getLog().error("Dependency tree parse and report to server error!", e);
        }
    }

    /**
     * 上报关联关系
     *
     * @param projectDtoJson
     */
    private void reportDependency(String projectDtoJson) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(reportUrl);
            List<NameValuePair> nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("data", projectDtoJson));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            CloseableHttpResponse response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HTTP_SUCCESS) {
                getLog().info("Success report to [" + reportUrl + "]");
            } else {
                HttpEntity entity = response.getEntity();
                EntityUtils.consume(entity);
                getLog().error("Fail to report to [" + reportUrl + "]");
                getLog().error("Error response: " + EntityUtils.toString(response.getEntity()));
            }
            response.close();
        } catch (Exception e) {
            getLog().error("Fail to report to [" + reportUrl + "]", e);
        } finally {
            try {
                httpclient.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 递归组装依赖包
     *
     * @param childrenDependencyNodes
     * @param dependencyDtos
     */
    private void transChildrenDependencyToDtos(List<DependencyNode> childrenDependencyNodes, List<DependencyDto> dependencyDtos) {
        for (DependencyNode dependencyNode : childrenDependencyNodes) {
            Artifact childArtifact = dependencyNode.getArtifact();
            DependencyDto dependencyDto = new DependencyDto(childArtifact.getGroupId(), childArtifact.getArtifactId(), childArtifact.getVersion(), childArtifact.getScope());
            dependencyDtos.add(dependencyDto);

            if (dependencyNode.getChildren() == null || dependencyNode.getChildren().isEmpty()) {
                continue;
            }

            transChildrenDependencyToDtos(dependencyNode.getChildren(), dependencyDtos);
        }
    }
}
