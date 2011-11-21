package org.jfrog.bamboo.task;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.configuration.ConfigurationMapImpl;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import com.atlassian.utils.process.ExternalProcess;
import com.atlassian.utils.process.ProcessHandler;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.jetbrains.annotations.NotNull;
import org.jfrog.bamboo.context.AbstractBuildContext;
import org.mockito.internal.verification.VerificationModeFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Noam Y. Tenne
 */
public class ArtifactoryTaskTypeTest {

    private TestCollationService testCollationServiceMock = mock(TestCollationService.class);
    private ArtifactoryTaskType taskType;
    private AbstractBuildContext abstractBuildContextMock = mock(AbstractBuildContext.class);
    private TaskContext taskContextMock = mock(TaskContext.class);
    private ProcessHandler processHandlerMock = mock(ProcessHandler.class);
    private ExternalProcess externalProcessMock = mock(ExternalProcess.class);

    @BeforeClass
    public void setUp() throws Exception {
        taskType = new ArtifactoryTaskType(testCollationServiceMock) {

            @NotNull
            @Override
            public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException {
                return null;
            }
        };
        when(processHandlerMock.getExitCode()).thenReturn(0);
        when(externalProcessMock.getHandler()).thenReturn(processHandlerMock);
    }

    @Test
    public void testCollectTestResultsWithTestNotChecked() throws Exception {
        reset(abstractBuildContextMock);
        assertNotNull(taskType.collectTestResults(abstractBuildContextMock, taskContextMock, externalProcessMock));
        verify(abstractBuildContextMock).isTestChecked();
        verifyNoMoreInteractions(abstractBuildContextMock);
    }

    @Test
    public void testCollectTestResultsWithNoTestDirectory() throws Exception {
        reset(abstractBuildContextMock);
        when(abstractBuildContextMock.isTestChecked()).thenReturn(true);
        assertNotNull(taskType.collectTestResults(abstractBuildContextMock, taskContextMock, externalProcessMock));
        verify(abstractBuildContextMock).isTestChecked();
        verify(abstractBuildContextMock).getTestDirectory();
        verifyNoMoreInteractions(abstractBuildContextMock);
    }

    @Test
    public void testCollectTestResults() throws Exception {
        reset(abstractBuildContextMock, taskContextMock);
        when(abstractBuildContextMock.isTestChecked()).thenReturn(true);
        when(abstractBuildContextMock.getTestDirectory()).thenReturn(Files.createTempDir().getAbsolutePath());

        BuildContext buildContextMock = mock(BuildContext.class);
        CurrentBuildResult buildResultMock = mock(CurrentBuildResult.class);
        when(buildContextMock.getBuildResult()).thenReturn(buildResultMock);
        when(taskContextMock.getBuildContext()).thenReturn(buildContextMock);
        BuildLogger buildLoggerMock = mock(BuildLogger.class);
        when(taskContextMock.getBuildLogger()).thenReturn(buildLoggerMock);

        assertNotNull(taskType.collectTestResults(abstractBuildContextMock, taskContextMock, externalProcessMock));
        verify(abstractBuildContextMock).isTestChecked();
        verify(abstractBuildContextMock, VerificationModeFactory.times(2)).getTestDirectory();
        verifyNoMoreInteractions(abstractBuildContextMock);
    }

    @Test
    public void testGetCombinedDataMap() throws Exception {
        reset(taskContextMock);

        Map<String, String> configurationMap = Maps.newHashMap();
        configurationMap.put("configurationMapKey", "configurationMapValue");
        when(taskContextMock.getConfigurationMap()).thenReturn(new ConfigurationMapImpl(configurationMap));

        BuildContext buildContextMock = mock(BuildContext.class);
        CurrentBuildResult buildResultMock = mock(CurrentBuildResult.class);
        Map<String, String> customBuildDataMap = Maps.newHashMap();
        customBuildDataMap.put("customBuildDataMapKey", "customBuildDataMapValue");
        when(buildResultMock.getCustomBuildData()).thenReturn(customBuildDataMap);
        when(buildContextMock.getBuildResult()).thenReturn(buildResultMock);
        when(taskContextMock.getBuildContext()).thenReturn(buildContextMock);

        BuildContext parentBuildContextMock = mock(BuildContext.class);
        CurrentBuildResult parentBuildResultMock = mock(CurrentBuildResult.class);
        Map<String, String> parentCustomBuildDataMap = Maps.newHashMap();
        parentCustomBuildDataMap.put("parentCustomBuildDataMapKey", "parentCustomBuildDataMapValue");
        when(parentBuildResultMock.getCustomBuildData()).thenReturn(parentCustomBuildDataMap);
        when(parentBuildContextMock.getBuildResult()).thenReturn(parentBuildResultMock);
        when(buildContextMock.getParentBuildContext()).thenReturn(parentBuildContextMock);

        Map<String, String> combinedBuildDataMap = taskType.getCombinedBuildDataMap(taskContextMock);
        assertEquals(combinedBuildDataMap.get("configurationMapKey"), "configurationMapValue");
        assertEquals(combinedBuildDataMap.get("customBuildDataMapKey"), "customBuildDataMapValue");
        assertEquals(combinedBuildDataMap.get("parentCustomBuildDataMapKey"), "parentCustomBuildDataMapValue");
    }
}
