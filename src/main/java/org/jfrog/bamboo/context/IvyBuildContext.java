package org.jfrog.bamboo.context;

import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.TaskContext;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * @author Tomer Cohen
 */
public class IvyBuildContext extends PackageManagersContext {
    public static final String PREFIX = "builder.artifactoryIvyBuilder.";
    public static final String ANT_OPTS_PARAM = "antOpts";
    public static final String TARGET_OPTS_PARAM = "target";
    public static final String BUILD_FILE = "buildFile";
    public static final String DEPLOYABLE_REPO_KEY = "deployableRepo";
    public static final String PUBLISH_ARTIFACTS = "deployArtifacts";
    public static final String EXCLUDE_PATTERN = "deployExcludePatterns";
    public static final String INCLUDE_PATTERN = "deployIncludePatterns";
    public static final String WORKING_SUB_DIRECTORY = "workingSubDirectory";

    public IvyBuildContext(Map<String, String> env) {
        super(PREFIX, env);
    }

    @Override
    public String getPublishingRepo() {
        return env.get(PREFIX + DEPLOYABLE_REPO_KEY);
    }

    public String getAntOpts() {
        return env.get(PREFIX + ANT_OPTS_PARAM);
    }

    public String getBuildFile() {
        return env.get(PREFIX + BUILD_FILE);
    }

    @Override
    public boolean isPublishArtifacts() {
        return Boolean.parseBoolean(env.get(PUBLISH_ARTIFACTS));
    }

    public String getTargets() {
        return env.get(PREFIX + TARGET_OPTS_PARAM);
    }

    public String getWorkingSubDirectory() {
        return env.get(PREFIX + WORKING_SUB_DIRECTORY);
    }

    @Override
    public String getExcludePattern() {
        return env.get(PREFIX + EXCLUDE_PATTERN);
    }

    @Override
    public void resetDeployerContextToDefault() {
        super.resetDeployerContextToDefault();
        env.put(PREFIX + DEPLOYABLE_REPO_KEY, "");
        env.put(PREFIX + INCLUDE_PATTERN, "");
        env.put(PUBLISH_ARTIFACTS, "true");
        env.put(PREFIX + EXCLUDE_PATTERN, "");
        env.put(PREFIX + INCLUDE_PATTERN, "");
    }

    @Override
    public String getIncludePattern() {
        return env.get(PREFIX + INCLUDE_PATTERN);
    }

    public static IvyBuildContext createIvyContextFromMap(Map<String, Object> map) {
        Map<String, String> transformed = Maps.transformValues(map, new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                return input.toString();
            }
        });
        return new IvyBuildContext(transformed);
    }

    public boolean shouldAggregateBuildInfo(@NotNull CommonTaskContext taskContext, long serverId) {
        boolean aggregateBuildInfo = super.shouldAggregateBuildInfo(taskContext);
        if (!aggregateBuildInfo) {
            return false;
        }
        // Value of CAPTURE_BUILD_INFO is 'true' by default.
        // In case of no server-id provided, shouldn't collect build-info even though the value remains 'true'.
        return serverId != -1;
    }

    /**
     * @return Get a set of all the fields to copy while populating the build context for an Ivy build.
     */
    public static Set<String> getFieldsToCopy() {
        return Sets.newHashSet(PREFIX + ANT_OPTS_PARAM, PREFIX + SERVER_ID_PARAM, PREFIX +
                RESOLUTION_REPO_PARAM, PREFIX + DEPLOYABLE_REPO_KEY, PREFIX + DEPLOYER_USERNAME_PARAM,
                PREFIX + DEPLOYER_PASSWORD_PARAM, PUBLISH_BUILD_INFO_PARAM,
                PUBLISH_ARTIFACTS, PREFIX + PUBLISH_MAVEN_DESCRIPTORS_PARAM, PREFIX + BUILD_FILE,
                PREFIX + PUBLISH_IVY_DESCRIPTORS_PARAM, USE_M2_COMPATIBLE_PATTERNS_PARAM,
                PREFIX + IVY_PATTERN_PARAM, PREFIX + TARGET_OPTS_PARAM, PREFIX + JDK,
                PREFIX + ARTIFACT_PATTERN_PARAM, PREFIX + INCLUDE_PATTERN, PREFIX + ENVIRONMENT_VARIABLES,
                PREFIX + FILTER_EXCLUDED_ARTIFACTS_FROM_BUILD_PARAM, BUILD_INFO_AGGREGATION, CAPTURE_BUILD_INFO,
                INCLUDE_ENV_VARS_PARAM, ENV_VARS_EXCLUDE_PATTERNS, ENV_VARS_INCLUDE_PATTERNS,
                PREFIX + EXECUTABLE, PREFIX + EXCLUDE_PATTERN, TEST_CHECKED, PREFIX + TEST_RESULT_DIRECTORY,
                TEST_DIRECTORY_OPTION, PREFIX + WORKING_SUB_DIRECTORY, BUILD_NAME, BUILD_NUMBER,
                RESOLVER_OVERRIDE_CREDENTIALS_CHOICE, RESOLVER_SHARED_CREDENTIALS,
                DEPLOYER_OVERRIDE_CREDENTIALS_CHOICE, DEPLOYER_SHARED_CREDENTIALS);
    }
}
