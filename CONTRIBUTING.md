# Contributing code to Bamboo Artifactory plugin
We welcome code contributions through pull requests. 
Before submitting a pull request, please make sure your code is well covered by tests.
Here are instructions for [building](#building-the-code) and [testing](#testing-the-code) the code.

## Building the Code
The code is built with Maven and JDK 8.
To build the plugin, please follow these steps:
1. Clone the code from GitHub.
2. Build and create the Bamboo Artifactory plugin jar by running the following Maven command:
```shell script
mvn clean package
```
After the build finished, you'll find the bamboo-artifactory-plugin-<version>.jar file in the *target* directory. 
This jar file can be loaded into Bamboo. 

## Testing the code
### Preconditions
1. [Install the Atlassian SDK](https://developer.atlassian.com/server/framework/atlassian-sdk/install-the-atlassian-sdk-on-a-linux-or-mac-system/).
For example, to install with Homebrew run:
```shell script
brew tap atlassian/tap
brew install atlassian/tap/atlassian-plugin-sdk
```
2. Configure the following environment variables:
* ARTIFACTORY_URL
* ARTIFACTORY_USERNAME
* ARTIFACTORY_PASSWORD
* MAVEN_HOME
* GRADLE_HOME

### Running the integration tests
To run integration tests, the plugin use the Atlassian Wired tests. You can read more about it [here](https://developer.atlassian.com/server/framework/atlassian-sdk/run-wired-tests-with-the-plugin-test-console).

To run the integration tests, in Bamboo Artifactory plugin source dir, execute the following command:
```shell script
atlas-clean && atlas-integration-test
```

### Running a single test
#### Step 1: Start the Bamboo server
The integration tests store a Bamboo home instance in a zip file `src/test/resources/bamboo-home.zip`. 
To start the Bamboo server with the tests configuration run the following command:
```shell script
atlas-debug
```
Running the above command will start a Tomcat server with the Bamboo CI server, and the Bamboo Artifactory plugin installed.
After the server is started, navigate to http://localhost:6990/bamboo. The credentials are `admin:admin`.

#### Step 2: Run a single test
The plugin's integration tests run the jobs under the *Integration Tests* project. 
To run a test, open the *Developer Toolbar* by clicking on the arrow in the lower left corner of your browser.
Click Toolbox > Plugin Test Console.
The test console appears. In this console you can run a specific test.

### Creating an integration test
#### Introduction
The integration test should include 2 parts:
1. A job in the tests Bamboo home
2. Java code in `src/test/java/it/org/jfrog/bamboo/<testname>Test.java`

| Tip: During the development of the test, you'll find yourself changing Java code and start the server again and again - make sure to NOT clean the code (using *atlas-clean*) because it will remove the temporary Bamboo Home environment.
| --- |

1. create a new class extending IntegrationTestsBase, under `src/test/java/it/org/jfrog/bamboo` - see current existing tests for reference.
2. Start the Bamboo server as instructed in the previous section. Make sure the new test appear in the *Test Console*.
3. Temporary, configure real Artifactory credentials in: http://localhost:6990/bamboo/admin/jfrogConfig.action
4. Create a new plan under *Integration Tests* project. Make sure the plan key is same as in the Java code. 
The plan must capture the build info, include environment variables and finalized by the Artifactory Publish Build Info task.
5. Run the test as instructed in the section above.
6. Once the test passed, change restore fake Artifactory credentials configured in the UI.
7. Stop the server with ctrl+c and run [./scripts/createBambooHome.sh](./scripts/createBambooHome.sh).