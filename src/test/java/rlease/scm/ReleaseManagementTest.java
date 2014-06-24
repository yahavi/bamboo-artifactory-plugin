package rlease.scm;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.IOException;

/**
 * @author Lior Hasson
 */
public class ReleaseManagementTest extends AbstractScmTest {
    /*private GitTestRepository srcRepo;
    private File srcDir;
    private Ref tagMaster1;
    

    @BeforeClass
    void setUpTest() throws IOException, GitAPIException {
        //srcDir = createTempDirectory();
        srcRepo = new GitTestRepository(srcDir);
        String initial = srcRepo.commitFileContents("Initial contents").name();
        tagMaster1 = srcRepo.git.tag().setName("master1").call();
    }

    @AfterClass
    public void tearDown() throws Exception
    {
        srcRepo.close();
    }*/
}
