package org.jfrog.bamboo.util;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Noam Y. Tenne
 */
public class BambooBuildInfoLogTest {

    @Test
    public void testLoggingMethods() throws Exception {
        Logger mockedLogger = mock(Logger.class);
        BambooBuildInfoLog log = new BambooBuildInfoLog(mockedLogger);
        log.debug("1");
        log.error("2");
        Exception e = new Exception();
        log.error("2", e);
        log.info("3");
        log.warn("4");

        verify(mockedLogger).debug("1");
        verify(mockedLogger).error("2");
        verify(mockedLogger).error("2", e);
        verify(mockedLogger).info("3");
        verify(mockedLogger).warn("4");
    }
}
