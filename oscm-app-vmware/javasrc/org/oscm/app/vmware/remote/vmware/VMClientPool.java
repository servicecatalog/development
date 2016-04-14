package org.oscm.app.vmware.remote.vmware;

import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

public class VMClientPool {

        private KeyedObjectPool<String, VMwareClient> pool;

        private static class SingletonHolder {
                public static final VMClientPool INSTANCE = new VMClientPool();
        }

        public static VMClientPool getInstance() {
                return SingletonHolder.INSTANCE;
        }

        private VMClientPool()
        {
                startPool();
        }

        /**
         * 
         * @return the org.apache.commons.pool.KeyedObjectPool class
         */
        public KeyedObjectPool<String, VMwareClient> getPool() {
                return pool;
        }

        /**
         * 
         * @return the org.apache.commons.pool.KeyedObjectPool class
         */
        public void startPool() {
                pool = new GenericKeyedObjectPool<String, VMwareClient>(new VMClientFactory());
                ((GenericKeyedObjectPool)pool).setMaxIdlePerKey(10);
                ((GenericKeyedObjectPool)pool).setMaxTotal(20);
                ((GenericKeyedObjectPool)pool).setMaxTotalPerKey(20);
//                ((GenericKeyedObjectPool)pool).setMinIdlePerKey(1);
//                ((GenericKeyedObjectPool)pool).setMinEvictableIdleTimeMillis(5000);
//                ((GenericKeyedObjectPool)pool).setTimeBetweenEvictionRunsMillis(30000);
//                ((GenericKeyedObjectPool)pool).setBlockWhenExhausted(true);
                ((GenericKeyedObjectPool)pool).setTestOnBorrow(true);
//                ((GenericKeyedObjectPool)pool).setTestOnCreate(false);
//                ((GenericKeyedObjectPool)pool).setTestOnReturn(false);
//                ((GenericKeyedObjectPool)pool).setTestWhileIdle(true);
//                ((GenericKeyedObjectPool)pool).setNumTestsPerEvictionRun(1);

//                maxIdle: The maximum number of sleeping instances in the pool, without extra objects being released.
//                minIdle: The minimum number of sleeping instances in the pool, without extra objects being created.
//                maxActive: The maximum number of active instances in the pool.
//                timeBetweenEvictionRunsMillis: The number of milliseconds to sleep between runs of the idle-object evictor thread. When negative, no idle-object evictor thread will run. Use this parameter only when you want the evictor thread to run.
//                minEvictableIdleTimeMillis: The minimum amount of time an object, if active, may sit idle in the pool before it is eligible for eviction by the idle-object evictor. If a negative value is supplied, no objects are evicted due to idle time alone.
//                testOnBorrow: When "true," objects are validated. If the object fails validation, it will be dropped from the pool, and the pool will attempt to borrow another.
//                
        }
}