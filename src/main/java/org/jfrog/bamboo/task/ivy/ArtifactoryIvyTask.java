package org.jfrog.bamboo.task.ivy;


import com.atlassian.bamboo.build.ErrorLogEntry;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.logger.interceptors.ErrorMemorisingInterceptor;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.process.EnvironmentVariableAccessor;
import com.atlassian.bamboo.process.ExternalProcessBuilder;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.v2.build.agent.capability.Capability;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.atlassian.bamboo.v2.build.agent.capability.ReadOnlyCapabilitySet;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.types.Commandline;
import org.jetbrains.annotations.NotNull;
import org.jfrog.bamboo.context.AbstractBuildContext;
import org.jfrog.bamboo.context.IvyBuildContext;
import org.jfrog.bamboo.task.ArtifactoryBuildInfoPropertyHelper;
import org.jfrog.bamboo.task.ArtifactoryTaskType;
import org.jfrog.bamboo.task.BuilderDependencyHelper;
import org.jfrog.bamboo.util.PluginProperties;
import org.jfrog.build.api.BuildInfoConfigProperties;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Invocation of the Ant/Ivy task.
 *
 * @author Tomer Cohen
 */
public class ArtifactoryIvyTask extends ArtifactoryTaskType {
    private static final Logger log = Logger.getLogger(ArtifactoryIvyTask.class);
    private final ProcessService processService;
    private final EnvironmentVariableAccessor environmentVariableAccessor;
    private final CapabilityContext capabilityContext;
    private BuilderDependencyHelper dependencyHelper;
    private String ivyDependenciesDir = "";
    public static final String EXECUTABLE_NAME = SystemUtils.IS_OS_WINDOWS ? "ant.bat" : "ant";
    private static final String IVY_KEY = "system.builder.ivy.";
    private String buildInfoPropertiesFile = "";
    private boolean activateBuildInfoRecording;


    public ArtifactoryIvyTask(final ProcessService processService,
            final EnvironmentVariableAccessor environmentVariableAccessor, final CapabilityContext capabilityContext,
            TestCollationService testCollationService) {
        super(testCollationService);
        this.processService = processService;
        this.environmentVariableAccessor = environmentVariableAccessor;
        this.capabilityContext = capabilityContext;
        dependencyHelper = new BuilderDependencyHelper("artifactoryIvyBuilder");
        ContainerManager.autowireComponent(dependencyHelper);
    }

    @Override
    @NotNull
    public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException {
        BuildLogger logger = taskContext.getBuildLogger();
        final ErrorMemorisingInterceptor errorLines = new ErrorMemorisingInterceptor();
        logger.getInterceptorStack().add(errorLines);
        Map<String, String> combinedMap = getCombinedBuildDataMap(taskContext);
        IvyBuildContext buildContext = new IvyBuildContext(combinedMap);
        File workingDirectory = taskContext.getWorkingDirectory();
        long serverId = buildContext.getArtifactoryServerId();
        try {
            ivyDependenciesDir = extractIvyDependencies(serverId, workingDirectory, buildContext);
            log.info(logger.addBuildLogEntry("Ivy dependency directory found at: " + ivyDependenciesDir));
        } catch (IOException ioe) {
            ivyDependenciesDir = null;
            logger.addBuildLogEntry(new ErrorLogEntry(
                    "Error occurred while preparing Artifactory Ivy Runner dependencies. Build Info support is " +
                            "disabled: " + ioe.getMessage()));
            log.error("Error occurred while preparing Artifactory Ivy Runner dependencies. " +
                    "Build Info support is disabled.", ioe);
        }
        if (ivyDependenciesDir == null) {
            String message = "Ivy dependency directory not found.";
            logger.addErrorLogEntry(message);
            log.error(message);
        }
        String executable = getExecutable(buildContext);
        if (StringUtils.isBlank(executable)) {
            log.error(logger.addErrorLogEntry("Cannot find ivy executable"));
            return TaskResultBuilder.create(taskContext).failed().build();
        }
        Map<String, String> globalEnv = environmentVariableAccessor.getEnvironment();
        Map<String, String> environment = Maps.newHashMap(globalEnv);
        if (StringUtils.isNotBlank(ivyDependenciesDir)) {
            ArtifactoryBuildInfoPropertyHelper propertyHelper = new IvyPropertyHelper();
            propertyHelper.init(taskContext.getBuildContext());
            buildInfoPropertiesFile = propertyHelper.createFileAndGetPath(buildContext, taskContext.getBuildLogger(),
                    environmentVariableAccessor.getEnvironment(taskContext), globalEnv);
            if (StringUtils.isNotBlank(buildInfoPropertiesFile)) {
                activateBuildInfoRecording = true;
                environment.put(BuildInfoConfigProperties.PROP_PROPS_FILE, buildInfoPropertiesFile);
            }
        }
        List<String> command = Lists.newArrayList(executable);
        if (activateBuildInfoRecording) {
            command.add("-lib");
            command.add(Commandline.quoteArgument(ivyDependenciesDir));
            command.add("-listener");
            command.add(Commandline.quoteArgument("org.jfrog.build.extractor.listener.ArtifactoryBuildListener"));
        }
        String buildFile = buildContext.getBuildFile();
        if (StringUtils.isNotBlank(buildFile)) {
            command.addAll(Arrays.asList("-f", buildFile));
        }
        String targets = buildContext.getTargets();
        if (StringUtils.isNotBlank(targets)) {
            String[] targetTokens = StringUtils.split(targets, ' ');
            command.addAll(Arrays.asList(targetTokens));
        }

        String antOpts = buildContext.getAntOpts();
        if (StringUtils.isNotBlank(antOpts)) {
            environment.put("ANT_OPTS", antOpts);
        }
        if (StringUtils.isNotBlank(buildContext.getEnvironmentVariables())) {
            environment.putAll(environmentVariableAccessor
                    .splitEnvironmentAssignments(buildContext.getEnvironmentVariables(), false));
        }
        String subDirectory = buildContext.getWorkingSubDirectory();
        if (StringUtils.isNotBlank(subDirectory)) {
            workingDirectory = new File(workingDirectory, subDirectory);
        }
        ExternalProcessBuilder processBuilder =
                new ExternalProcessBuilder().workingDirectory(workingDirectory).command(command)
                        .env(environment);
        try {
            return TaskResultBuilder.create(taskContext)
                    .checkReturnCode(processService.executeProcess(taskContext, processBuilder)).build();
        } finally {
            taskContext.getBuildContext().getBuildResult().addBuildErrors(errorLines.getErrorStringList());
        }
    }

    /**
     * Extracts the Artifactory Ivy recorder and all the needed to dependencies
     *
     * @return Path of recorder and dependency jar folder if extraction succeeded. Null if not
     */
    private String extractIvyDependencies(long artifactoryServerId, File workingDirectory, IvyBuildContext context)
            throws IOException {

        if (artifactoryServerId == -1) {
            return null;
        }

        return dependencyHelper.downloadDependenciesAndGetPath(workingDirectory, context,
                PluginProperties.IVY_DEPENDENCY_FILENAME_KEY);
    }

    private String getExecutable(AbstractBuildContext buildContext) throws TaskException {
        ReadOnlyCapabilitySet capabilitySet = capabilityContext.getCapabilitySet();
        if (capabilitySet == null) {
            return null;
        }
        Capability capability = capabilitySet.getCapability(IVY_KEY + buildContext.getExecutable());
        if (capability == null) {
            throw new TaskException("Ivy capability: " + buildContext.getExecutable() +
                    " is not defined, please check job configuration");
        }
        final String path = new StringBuilder(capability.getValue())
                .append(File.separator)
                .append("bin")
                .append(File.separator)
                .append(EXECUTABLE_NAME)
                .toString();

        if (!new File(path).exists()) {
            throw new TaskException("Executable '" + EXECUTABLE_NAME + "'  does not exist at path '" + path + "'");
        }

        return path;
    }
}
