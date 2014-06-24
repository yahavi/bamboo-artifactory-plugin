package rlease.scm;

import com.atlassian.bamboo.repository.Repository;
import com.atlassian.bamboo.v2.build.BuildContext;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.jfrog.bamboo.release.scm.git.GitManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Lior Hasson
 */
public class AbstractScmTest {

    /*private BuildContext chainBuildContext;

    public AbstractScmTest() {
        ;
        // super(chainBuildContext,new RepositoryDefinitionForTest(null,null), null);

    }

    @Test
    public void test() {
        BuildContext context = mock(BuildContext.class);
        Repository repository = mock(Repository.class);
        //when(repository.getClass().getName()).thenReturn("com.atlassian.bamboo.plugins.git.GitRepository");
        HierarchicalConfiguration config = mock(HierarchicalConfiguration.class);
        when(config.getString("repository.git.username")).thenReturn("");
        when(config.getString("repository.git.password")).thenReturn("");
        when(repository.toConfiguration()).thenReturn(config);
//        BuildContext buildContext = new BuildContextBuilderImpl(null, null, null, null).build();
//        chainBuildContext.s


        //GitManager gitManager = new GitManager(context, repository, null, null, null);
    }*/
}
