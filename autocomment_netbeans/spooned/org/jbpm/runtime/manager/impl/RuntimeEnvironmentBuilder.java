/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.runtime.manager.impl;

import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorManager;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorMerger;
import javax.persistence.EntityManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import java.util.HashMap;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.KieBase;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.runtime.KieContainer;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.kie.api.KieServices;
import java.util.List;
import java.util.Map;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.scanner.MavenRepository;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.ObjectModelResolver;
import org.kie.internal.runtime.conf.ObjectModelResolverProvider;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilderFactory;
import org.drools.core.util.StringUtils;
import org.kie.api.task.UserGroupCallback;

/**
 * A builder implementation that follows the fluent approach to build <code>RuntimeEnvironments</code>.
 * Comes with short cut methods to get predefined configurations of the <code>RuntimeEnvironment</code>:
 * <ul>
 *  <li>getDefault() - returns preconfigured environment with enabled persistence</li>
 *  <li>getDefaultInMemory() - returns preconfigured environment with disabled persistence for runtime engine</li>
 *  <li>getDefault(ReleaseId) - returns preconfigured environment with enabled persistence that is tailored for kjar</li>
 *  <li>getDefault(ReleaseId, String, String) - returns preconfigured environment with enabled persistence that is tailored for kjar and allows to specify kbase and ksession name</li>
 *  <li>getDefault(String, String, String) - returns preconfigured environment with enabled persistence that is tailored for kjar</li>
 *  <li>getDefault(String, String, String, String, String) - returns preconfigured environment with enabled persistence that is tailored for kjar and allows to specify kbase and ksession name</li>
 *  <li>getEmpty() - completely empty environment for self configuration</li>
 *  <li>getClasspathKModuleDefault() - returns preconfigured environment with enabled persistence based on classpath kiecontainer</li>
 *  <li>getClasspathKModuleDefault(String, String) - returns preconfigured environment with enabled persistence based on classpath kiecontainer</li>
 * </ul>
 */
public class RuntimeEnvironmentBuilder implements org.kie.api.runtime.manager.RuntimeEnvironmentBuilder , RuntimeEnvironmentBuilderFactory {
    private static final String DEFAULT_KBASE_NAME = "defaultKieBase";

    private SimpleRuntimeEnvironment runtimeEnvironment;

    public RuntimeEnvironmentBuilder() {
        RuntimeEnvironmentBuilder.this.runtimeEnvironment = new SimpleRuntimeEnvironment();
    }

    private RuntimeEnvironmentBuilder(SimpleRuntimeEnvironment runtimeEnvironment) {
        RuntimeEnvironmentBuilder.this.runtimeEnvironment = runtimeEnvironment;
    }

    /**
     * Provides completely empty <code>RuntimeEnvironmentBuilder</code> instance that allows to manually
     * set all required components instead of relying on any defaults.
     * @return new instance of <code>RuntimeEnvironmentBuilder</code>
     */
    public static RuntimeEnvironmentBuilder getEmpty() {
        return new RuntimeEnvironmentBuilder();
    }

    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * @see DefaultRuntimeEnvironment
     */
    public static RuntimeEnvironmentBuilder getDefault() {
        return new RuntimeEnvironmentBuilder(new DefaultRuntimeEnvironment());
    }

    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * but it does not have persistence for process engine configured so it will only store process instances in memory
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * @see DefaultRuntimeEnvironment
     */
    public static RuntimeEnvironmentBuilder getDefaultInMemory() {
        RuntimeEnvironmentBuilder builder = new RuntimeEnvironmentBuilder(new DefaultRuntimeEnvironment(null, false));
        builder.addConfiguration("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName()).addConfiguration("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName());
        return builder;
    }

    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * This one is tailored to works smoothly with kjars as the notion of kbase and ksessions
     * @param groupId group id of kjar
     * @param artifactId artifact id of kjar
     * @param version version number of kjar
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * @see DefaultRuntimeEnvironment
     */
    public static RuntimeEnvironmentBuilder getDefault(String groupId, String artifactId, String version) {
        return RuntimeEnvironmentBuilder.getDefault(groupId, artifactId, version, null, null);
    }

    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * This one is tailored to works smoothly with kjars as the notion of kbase and ksessions
     * @param groupId group id of kjar
     * @param artifactId artifact id of kjar
     * @param version version number of kjar
     * @param kbaseName name of the kbase defined in kmodule.xml stored in kjar
     * @param ksessionName name of the ksession define in kmodule.xml stored in kjar
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * @see DefaultRuntimeEnvironment
     */
    public static RuntimeEnvironmentBuilder getDefault(String groupId, String artifactId, String version, String kbaseName, String ksessionName) {
        KieServices ks = KieServices.Factory.get();
        return RuntimeEnvironmentBuilder.getDefault(ks.newReleaseId(groupId, artifactId, version), kbaseName, ksessionName);
    }

    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * This one is tailored to works smoothly with kjars as the notion of kbase and ksessions
     * @param releaseId <code>ReleaseId</code> that described the kjar
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * @see DefaultRuntimeEnvironment
     */
    public static RuntimeEnvironmentBuilder getDefault(ReleaseId releaseId) {
        return RuntimeEnvironmentBuilder.getDefault(releaseId, null, null);
    }

    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * This one is tailored to works smoothly with kjars as the notion of kbase and ksessions
     * @param releaseId <code>ReleaseId</code> that described the kjar
     * @param kbaseName name of the kbase defined in kmodule.xml stored in kjar
     * @param ksessionName name of the ksession define in kmodule.xml stored in kjar
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * @see DefaultRuntimeEnvironment
     */
    public static RuntimeEnvironmentBuilder getDefault(ReleaseId releaseId, String kbaseName, String ksessionName) {
        MavenRepository repository = MavenRepository.getMavenRepository();
        repository.resolveArtifact(releaseId.toExternalForm());
        KieServices ks = KieServices.Factory.get();
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        DeploymentDescriptorManager descriptorManager = new DeploymentDescriptorManager();
        List<DeploymentDescriptor> descriptorHierarchy = descriptorManager.getDeploymentDescriptorHierarchy(kieContainer);
        DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
        DeploymentDescriptor descriptor = merger.merge(descriptorHierarchy, MergeMode.MERGE_COLLECTIONS);
        if (StringUtils.isEmpty(kbaseName)) {
            KieBaseModel defaultKBaseModel = ((KieContainerImpl) (kieContainer)).getKieProject().getDefaultKieBaseModel();
            if (defaultKBaseModel != null) {
                kbaseName = defaultKBaseModel.getName();
            } else {
                kbaseName = RuntimeEnvironmentBuilder.DEFAULT_KBASE_NAME;
            }
        } 
        InternalKieModule module = ((InternalKieModule) (((KieContainerImpl) (kieContainer)).getKieModuleForKBase(kbaseName)));
        if (module == null) {
            throw new IllegalStateException("Cannot find kbase, either it does not exist or there are multiple default kbases in kmodule.xml");
        } 
        KieBase kbase = kieContainer.getKieBase(kbaseName);
        RuntimeEnvironmentBuilder builder = null;
        if ((descriptor.getPersistenceMode()) == (PersistenceMode.NONE)) {
            builder = RuntimeEnvironmentBuilder.getDefaultInMemory();
        } else {
            builder = RuntimeEnvironmentBuilder.getDefault();
        }
        Map<String, Object> contaxtParams = new HashMap<String, Object>();
        contaxtParams.put("classLoader", kieContainer.getClassLoader());
        // populate various properties of the builder
        if ((descriptor.getPersistenceUnit()) != null) {
            EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(descriptor.getPersistenceUnit());
            builder.entityManagerFactory(emf);
            contaxtParams.put("entityManagerFactory", emf);
        } 
        // process object models that are globally configured (environment entries, session configuration)
        for (NamedObjectModel model : descriptor.getEnvironmentEntries()) {
            Object entry = RuntimeEnvironmentBuilder.getInstanceFromModel(model, kieContainer, contaxtParams);
            builder.addEnvironmentEntry(model.getName(), entry);
        }
        for (NamedObjectModel model : descriptor.getConfiguration()) {
            Object entry = RuntimeEnvironmentBuilder.getInstanceFromModel(model, kieContainer, contaxtParams);
            builder.addConfiguration(model.getName(), ((String) (entry)));
        }
        ObjectMarshallingStrategy[] mStrategies = new ObjectMarshallingStrategy[(descriptor.getMarshallingStrategies().size()) + 1];
        int index = 0;
        for (ObjectModel model : descriptor.getMarshallingStrategies()) {
            Object strategy = RuntimeEnvironmentBuilder.getInstanceFromModel(model, kieContainer, contaxtParams);
            mStrategies[index] = ((ObjectMarshallingStrategy) (strategy));
            index++;
        }
        // lastly add the main default strategy
        mStrategies[index] = new org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);
        builder.addEnvironmentEntry(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, mStrategies);
        builder.addEnvironmentEntry("KieDeploymentDescriptor", descriptor).knowledgeBase(kbase).classLoader(kieContainer.getClassLoader()).registerableItemsFactory(new KModuleRegisterableItemsFactory(kieContainer, ksessionName));
        return builder;
    }

    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * It relies on KieClasspathContainer that requires to have kmodule.xml present in META-INF folder which
     * defines the kjar itself.
     * Expects to use default kbase and ksession from kmodule.
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * @see DefaultRuntimeEnvironment
     */
    public static RuntimeEnvironmentBuilder getClasspathKmoduleDefault() {
        return RuntimeEnvironmentBuilder.getClasspathKmoduleDefault(null, null);
    }

    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * It relies on KieClasspathContainer that requires to have kmodule.xml present in META-INF folder which
     * defines the kjar itself.
     * @param kbaseName name of the kbase defined in kmodule.xml
     * @param ksessionName name of the ksession define in kmodule.xml
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * @see DefaultRuntimeEnvironment
     */
    public static RuntimeEnvironmentBuilder getClasspathKmoduleDefault(String kbaseName, String ksessionName) {
        return RuntimeEnvironmentBuilder.setupClasspathKmoduleBuilder(KieServices.Factory.get().getKieClasspathContainer(), kbaseName, ksessionName);
    }

    private static RuntimeEnvironmentBuilder setupClasspathKmoduleBuilder(KieContainer kieContainer, String kbaseName, String ksessionName) {
        if (StringUtils.isEmpty(kbaseName)) {
            KieBaseModel defaultKBaseModel = ((KieContainerImpl) (kieContainer)).getKieProject().getDefaultKieBaseModel();
            if (defaultKBaseModel != null) {
                kbaseName = defaultKBaseModel.getName();
            } else {
                kbaseName = RuntimeEnvironmentBuilder.DEFAULT_KBASE_NAME;
            }
        } 
        InternalKieModule module = ((InternalKieModule) (((KieContainerImpl) (kieContainer)).getKieModuleForKBase(kbaseName)));
        if (module == null) {
            throw new IllegalStateException(("Cannot find kbase with name " + kbaseName));
        } 
        KieBase kbase = kieContainer.getKieBase(kbaseName);
        return RuntimeEnvironmentBuilder.getDefault().knowledgeBase(kbase).classLoader(kieContainer.getClassLoader()).registerableItemsFactory(new KModuleRegisterableItemsFactory(kieContainer, ksessionName));
    }

    public RuntimeEnvironmentBuilder persistence(boolean persistenceEnabled) {
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.setUsePersistence(persistenceEnabled);
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironmentBuilder entityManagerFactory(Object emf) {
        if (emf == null) {
            return RuntimeEnvironmentBuilder.this;
        } 
        if (!(emf instanceof EntityManagerFactory)) {
            throw new IllegalArgumentException("Argument is not of type EntityManagerFactory");
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.setEmf(((EntityManagerFactory) (emf)));
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironmentBuilder addAsset(Resource asset, ResourceType type) {
        if ((asset == null) || (type == null)) {
            return RuntimeEnvironmentBuilder.this;
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.addAsset(asset, type);
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironmentBuilder addEnvironmentEntry(String name, Object value) {
        if ((name == null) || (value == null)) {
            return RuntimeEnvironmentBuilder.this;
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.addToEnvironment(name, value);
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironmentBuilder addConfiguration(String name, String value) {
        if ((name == null) || (value == null)) {
            return RuntimeEnvironmentBuilder.this;
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.addToConfiguration(name, value);
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironmentBuilder knowledgeBase(KieBase kbase) {
        if (kbase == null) {
            return RuntimeEnvironmentBuilder.this;
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.setKieBase(kbase);
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironmentBuilder userGroupCallback(UserGroupCallback callback) {
        if (callback == null) {
            return RuntimeEnvironmentBuilder.this;
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.setUserGroupCallback(callback);
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironmentBuilder mapper(Mapper mapper) {
        if (mapper == null) {
            return RuntimeEnvironmentBuilder.this;
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.setMapper(mapper);
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironmentBuilder registerableItemsFactory(RegisterableItemsFactory factory) {
        if (factory == null) {
            return RuntimeEnvironmentBuilder.this;
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.setRegisterableItemsFactory(factory);
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironment get() {
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.init();
        return RuntimeEnvironmentBuilder.this.runtimeEnvironment;
    }

    public RuntimeEnvironmentBuilder schedulerService(Object globalScheduler) {
        if (globalScheduler == null) {
            return RuntimeEnvironmentBuilder.this;
        } 
        if (!(globalScheduler instanceof GlobalSchedulerService)) {
            throw new IllegalArgumentException("Argument is not of type GlobalSchedulerService");
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.setSchedulerService(((GlobalSchedulerService) (globalScheduler)));
        return RuntimeEnvironmentBuilder.this;
    }

    public RuntimeEnvironmentBuilder classLoader(ClassLoader cl) {
        if (cl == null) {
            return RuntimeEnvironmentBuilder.this;
        } 
        RuntimeEnvironmentBuilder.this.runtimeEnvironment.setClassLoader(cl);
        return RuntimeEnvironmentBuilder.this;
    }

    @Override
    public org.kie.api.runtime.manager.RuntimeEnvironmentBuilder newEmptyBuilder() {
        return RuntimeEnvironmentBuilder.getEmpty();
    }

    @Override
    public org.kie.api.runtime.manager.RuntimeEnvironmentBuilder newDefaultBuilder() {
        return RuntimeEnvironmentBuilder.getDefault();
    }

    @Override
    public org.kie.api.runtime.manager.RuntimeEnvironmentBuilder newDefaultInMemoryBuilder() {
        return RuntimeEnvironmentBuilder.getDefaultInMemory();
    }

    @Override
    public org.kie.api.runtime.manager.RuntimeEnvironmentBuilder newDefaultBuilder(String groupId, String artifactId, String version) {
        return RuntimeEnvironmentBuilder.getDefault(groupId, artifactId, version);
    }

    @Override
    public org.kie.api.runtime.manager.RuntimeEnvironmentBuilder newDefaultBuilder(String groupId, String artifactId, String version, String kbaseName, String ksessionName) {
        return RuntimeEnvironmentBuilder.getDefault(groupId, artifactId, version, kbaseName, ksessionName);
    }

    @Override
    public org.kie.api.runtime.manager.RuntimeEnvironmentBuilder newDefaultBuilder(ReleaseId releaseId) {
        return RuntimeEnvironmentBuilder.getDefault(releaseId);
    }

    @Override
    public org.kie.api.runtime.manager.RuntimeEnvironmentBuilder newDefaultBuilder(ReleaseId releaseId, String kbaseName, String ksessionName) {
        return RuntimeEnvironmentBuilder.getDefault(releaseId, kbaseName, ksessionName);
    }

    @Override
    public org.kie.api.runtime.manager.RuntimeEnvironmentBuilder newClasspathKmoduleDefaultBuilder() {
        return newClasspathKmoduleDefaultBuilder(null, null);
    }

    @Override
    public org.kie.api.runtime.manager.RuntimeEnvironmentBuilder newClasspathKmoduleDefaultBuilder(String kbaseName, String ksessionName) {
        return RuntimeEnvironmentBuilder.setupClasspathKmoduleBuilder(KieServices.Factory.get().newKieClasspathContainer(), kbaseName, ksessionName);
    }

    protected static Object getInstanceFromModel(ObjectModel model, KieContainer kieContainer, Map<String, Object> contaxtParams) {
        ObjectModelResolver resolver = ObjectModelResolverProvider.get(model.getResolver());
        return resolver.getInstance(model, kieContainer.getClassLoader(), contaxtParams);
    }
}

