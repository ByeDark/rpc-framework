package org.xiatian.rpc.common.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {

    public static AtomicInteger index = new AtomicInteger(0);

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(index.getAndIncrement() % instances.size());
    }

}
