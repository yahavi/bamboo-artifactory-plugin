package it.org.jfrog.bamboo.prehook;

import com.atlassian.bamboo.event.ServerStartedEvent;
import com.atlassian.event.api.EventListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static it.org.jfrog.bamboo.Utils.*;
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
        verifyEnvironment(ARTIFACTORY_URL_ENV);
        verifyEnvironment(ARTIFACTORY_USERNAME_ENV);
        verifyEnvironment(ARTIFACTORY_PASSWORD_ENV);
        verifyEnvironment(MAVEN_HOME_ENV);
        verifyEnvironment(GRADLE_HOME_ENV);
    }

    /**
     * Verify a single environment variable.
     *
     * @param envKey - The environment variable key to verify
     */
    private void verifyEnvironment(String envKey) {
        if (StringUtils.isBlank(System.getenv(envKey))) {
            String msg = envKey + " is not set";
            System.err.println(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
