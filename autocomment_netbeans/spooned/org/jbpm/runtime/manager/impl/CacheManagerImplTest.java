

package org.jbpm.runtime.manager.impl;

import org.junit.Assert;
import org.kie.internal.runtime.manager.CacheManager;
import org.kie.internal.runtime.Cacheable;
import org.mockito.Mockito;
import org.junit.Test;

public class CacheManagerImplTest {
    @Test
    public void testDispose() throws Exception {
        CacheManager cacheManager = new CacheManagerImpl();
        Cacheable cacheable = Mockito.mock(Cacheable.class);
        Cacheable otherCacheable = Mockito.mock(Cacheable.class);
        Object cached = new Object();
        cacheManager.add("cacheable", cacheable);
        cacheManager.add("other_cacheable", otherCacheable);
        cacheManager.add("cached", cached);
        // verify that objects have been added correctly
        Assert.assertEquals(cacheable, cacheManager.get("cacheable"));
        Assert.assertEquals(otherCacheable, cacheManager.get("other_cacheable"));
        Assert.assertEquals(cached, cacheManager.get("cached"));
        cacheManager.dispose();
        // cache should be empty after dispose
        Assert.assertNull(cacheManager.get("cacheable"));
        Assert.assertNull(cacheManager.get("other_cacheable"));
        Assert.assertNull(cacheManager.get("cached"));
        // close() method has been called on cached objects which implement Cacheable
        Mockito.verify(cacheable).close();
        Mockito.verify(otherCacheable).close();
    }
}

