package it.org.jfrog.bamboo.utils;

import java.util.HashMap;
import java.util.Map;

import static com.jfrog.testing.IntegrationTestsHelper.*;
import static org.jfrog.bamboo.configuration.BuildParamsOverrideManager.*;

/**
 * @author yahavi
 */
public class Utils {
    // Environment variables names
    public static final String GRADLE_HOME_ENV = "GRADLE_HOME";
    public static final String MAVEN_HOME_ENV = "MAVEN_HOME";

    // Environment variables values
    public static final String GRADLE_HOME = System.getenv(GRADLE_HOME_ENV);
    public static final String MAVEN_HOME = System.getenv(MAVEN_HOME_ENV);

    /**
     * Create overriding plan variables. The Bamboo Artifactory plugin uses them in order to override
     * configured Artifactory URL, username, password and repositories.
     *
     * @param localRepoKey - Local test repository key
     * @param jcenter      - JCenter repository key
     * @return overriding plan variables
     */
    public static Map<String, String> createOverrideVars(String localRepoKey, String jcenter) {
        return new HashMap<String, String>() {{
            put(OVERRIDE_ARTIFACTORY_DEPLOYER_URL, ARTIFACTORY_URL);
            put(OVERRIDE_ARTIFACTORY_DEPLOYER_USERNAME, ARTIFACTORY_USERNAME);
            put(OVERRIDE_ARTIFACTORY_DEPLOYER_PASSWORD, ARTIFACTORY_PASSWORD);
            put(OVERRIDE_ARTIFACTORY_DEPLOY_REPO, localRepoKey);
            put(OVERRIDE_ARTIFACTORY_RESOLVER_URL, ARTIFACTORY_URL);
            put(OVERRIDE_ARTIFACTORY_RESOLVER_USERNAME, ARTIFACTORY_USERNAME);
            put(OVERRIDE_ARTIFACTORY_RESOLVER_PASSWORD, ARTIFACTORY_PASSWORD);
            put(OVERRIDE_ARTIFACTORY_RESOLVE_REPO, jcenter);
        }};
    }

    /**
     * Create common plan environment variables.
     *
     * @return plan environment variables
     */
    public static Map<String, String> createEnv() {
        return new HashMap<String, String>() {{
            // The collect env methodology should ignore this variable:
            put("DONT_COLLECT", "FOO");

            // The collect env methodology should add this variable:
            put("COLLECT", "BAR");
        }};
    }

}
