package it.org.jfrog.bamboo.prehook;

import com.atlassian.bamboo.event.ServerStartedEvent;
import com.atlassian.event.api.EventListener;
import com.jfrog.testing.IntegrationTestsHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static it.org.jfrog.bamboo.Utils.GRADLE_HOME_ENV;
import static it.org.jfrog.bamboo.Utils.MAVEN_HOME_ENV;
import static it.org.jfrog.bamboo.prehook.RemoteAgent.startAgent;
import static it.org.jfrog.bamboo.prehook.RepositoriesHandler.createTestRepositories;

/**
 * Contains the ServerStarted listener event.
 *
 * @author yahavi
 **/
public class ServerListener {

    /**
     * Run after Tomcat server started with the Bamboo CI.
     *
     * @param buildStarted - Exist just to filter out event types other than ServerStartedEvent.
     */
    @SuppressWarnings("unused")
    @EventListener
    public void serverStarted(ServerStartedEvent buildStarted) {
        try {
            verifyEnvironment();
            createTestRepositories();
            startAgent();
        } catch (Exception e) {
            String msg = "Bamboo Artifactory plugin tests: An error occurred";
            System.err.println(msg + ": " + ExceptionUtils.getRootCauseMessage(e));
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Verify required environment variables for the tests.
     */
    private void verifyEnvironment() {
        IntegrationTestsHelper.verifyEnvironment(MAVEN_HOME_ENV);
        IntegrationTestsHelper.verifyEnvironment(GRADLE_HOME_ENV);
    }
}
