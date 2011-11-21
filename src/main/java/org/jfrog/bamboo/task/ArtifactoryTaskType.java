package org.jfrog.bamboo.task;

import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.utils.process.ExternalProcess;
import com.google.common.collect.Maps;
import org.jfrog.bamboo.context.AbstractBuildContext;

import java.util.Map;

/**
 * Common super type for all tasks
 *
 * @author Tomer Cohen
 */
public abstract class ArtifactoryTaskType implements TaskType {

    private final TestCollationService testCollationService;

    protected ArtifactoryTaskType(TestCollationService testCollationService) {
        this.testCollationService = testCollationService;
    }

    protected TaskResult collectTestResults(AbstractBuildContext buildContext, TaskContext taskContext,
            ExternalProcess process) {
        TaskResultBuilder builder = TaskResultBuilder.create(taskContext).checkReturnCode(process);
        if (buildContext.isTestChecked() && buildContext.getTestDirectory() != null) {
            testCollationService.collateTestResults(taskContext, buildContext.getTestDirectory());
            builder.checkTestFailures();
        }
        return builder.build();
    }

    protected Map<String, String> getCombinedBuildDataMap(TaskContext taskContext) {
        Map<String, String> combinedMap = Maps.newHashMap(taskContext.getConfigurationMap());
        BuildContext buildContext = taskContext.getBuildContext();
        combinedMap.putAll(buildContext.getBuildResult().getCustomBuildData());
        BuildContext parentBuildContext = buildContext.getParentBuildContext();
        if (parentBuildContext != null) {
            Map<String, String> customBuildData = parentBuildContext.getBuildResult().getCustomBuildData();
            combinedMap.putAll(customBuildData);
        }
        return combinedMap;
    }
}
