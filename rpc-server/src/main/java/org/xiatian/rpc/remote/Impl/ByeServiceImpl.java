package org.xiatian.rpc.remote.Impl;

import org.xiatian.rpc.common.annotation.Service;
import org.xiatian.rpc.remote.ByeService;

@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
