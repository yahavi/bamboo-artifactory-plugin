package it.org.jfrog.bamboo;

import com.jfrog.testing.TestRepository;

public enum TestRepositories {
    LOCAL_REPO("local", TestRepository.RepoType.LOCAL),
    JCENTER_REMOTE_REPO("jcenter", TestRepository.RepoType.REMOTE);

    private final TestRepository testRepository;

    TestRepositories(String repoName, TestRepository.RepoType repoType) {
        this.testRepository = new TestRepository(repoName, repoType);
    }

    public TestRepository getTestRepository() {
        return testRepository;
    }
}
