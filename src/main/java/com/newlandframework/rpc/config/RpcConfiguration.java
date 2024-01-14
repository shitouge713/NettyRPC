package com.newlandframework.rpc.config;

import com.newlandframework.rpc.core.DefaultModular;
import com.newlandframework.rpc.filter.ChainFilter;
import com.newlandframework.rpc.filter.ModuleFilterChainWrapper;
import com.newlandframework.rpc.filter.support.ClassLoaderChainFilter;
import com.newlandframework.rpc.filter.support.EchoChainFilter;
import com.newlandframework.rpc.listener.ModuleListener;
import com.newlandframework.rpc.listener.ModuleListenerChainWrapper;
import com.newlandframework.rpc.listener.support.ModuleListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RpcConfiguration {

    @Bean(name = "modular")
    public ModuleFilterChainWrapper newModuleFilterChainWrapper(ModuleListenerChainWrapper listeners,
                                                                ChainFilter classLoaderChainFilter,
                                                                ChainFilter echoChainFilter) {
        ModuleFilterChainWrapper modular = new ModuleFilterChainWrapper(listeners);
        List<ChainFilter> chainFilters = new ArrayList<>();
        chainFilters.add(classLoaderChainFilter);
        chainFilters.add(echoChainFilter);
        modular.setFilters(chainFilters);
        return modular;
    }

    @Bean(name = "listeners")
    public ModuleListenerChainWrapper newModuleListenerChainWrapper(ModuleListenerAdapter listenerAdapter) {
        ModuleListenerChainWrapper listeners = new ModuleListenerChainWrapper(new DefaultModular());
        List<ModuleListener> list = new ArrayList<>();
        list.add(listenerAdapter);
        listeners.setListeners(list);
        return listeners;
    }

    @Bean(name = "listenerAdapter")
    public ModuleListenerAdapter newModuleListenerAdapter() {
        return new ModuleListenerAdapter();
    }

    @Bean(name = "classLoaderChainFilter")
    public ClassLoaderChainFilter newClassLoaderChainFilter() {
        return new ClassLoaderChainFilter();
    }

    @Bean(name = "echoChainFilter")
    public EchoChainFilter newEchoChainFilter() {
        return new EchoChainFilter();
    }
}
