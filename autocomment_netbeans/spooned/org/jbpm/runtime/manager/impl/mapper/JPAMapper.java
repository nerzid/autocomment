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


package org.jbpm.runtime.manager.impl.mapper;

import org.kie.internal.process.CorrelationKey;
import java.util.ArrayList;
import EnvironmentName.CMD_SCOPED_ENTITY_MANAGER;
import org.kie.api.runtime.manager.Context;
import org.jbpm.runtime.manager.impl.jpa.ContextMappingInfo;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.internal.process.CorrelationProperty;
import EnvironmentName.ENTITY_MANAGER_FACTORY;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.NoResultException;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import javax.persistence.Query;
import org.kie.api.runtime.Environment;

/**
 * Database based mapper implementation backed by JPA to store
 * the context to <code>KieSession</code> id mapping. It uses the <code>ContextMappingInfo</code>
 * entity for persistence.
 *
 * @see ContextMappingInfo
 */
@SuppressWarnings(value = "rawtypes")
public class JPAMapper extends InternalMapper {
    private EntityManagerFactory emf;

    public JPAMapper(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void saveMapping(Context context, Long ksessionId, String ownerId) {
        JPAMapper.EntityManagerInfo info = getEntityManager(context);
        EntityManager em = info.getEntityManager();
        // persist ContextMappingInfo{new org.jbpm.runtime.manager.impl.jpa.ContextMappingInfo(resolveContext(context, em).getContextId().toString(), ksessionId, ownerId)} to EntityManager{em}
        em.persist(new ContextMappingInfo(resolveContext(context, em).getContextId().toString(), ksessionId, ownerId));
        if (!(info.isShared())) {
            em.close();
        }
    }

    @Override
    public Long findMapping(Context context, String ownerId) {
        JPAMapper.EntityManagerInfo info = getEntityManager(context);
        EntityManager em = info.getEntityManager();
        try {
            ContextMappingInfo contextMapping = findContextByContextId(resolveContext(context, em), ownerId, em);
            if (contextMapping != null) {
                return contextMapping.getKsessionId();
            }
            return null;
        } finally {
            if (!(info.isShared())) {
                em.close();
            }
        }
    }

    @Override
    public void removeMapping(Context context, String ownerId) {
        JPAMapper.EntityManagerInfo info = getEntityManager(context);
        EntityManager em = info.getEntityManager();
        ContextMappingInfo contextMapping = findContextByContextId(resolveContext(context, em), ownerId, em);
        if (contextMapping != null) {
            em.remove(contextMapping);
        }
        if (!(info.isShared())) {
            em.close();
        }
    }

    protected Context resolveContext(Context orig, EntityManager em) {
        if (orig instanceof CorrelationKeyContext) {
            return getProcessInstanceByCorrelationKey(((CorrelationKey) (orig.getContextId())), em);
        }
        return orig;
    }

    protected ContextMappingInfo findContextByContextId(Context context, String ownerId, EntityManager em) {
        try {
            if ((context.getContextId()) == null) {
                return null;
            }
            Query findQuery = em.createNamedQuery("FindContextMapingByContextId").setParameter("contextId", context.getContextId().toString()).setParameter("ownerId", ownerId);
            ContextMappingInfo contextMapping = ((ContextMappingInfo) (findQuery.getSingleResult()));
            return contextMapping;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            return null;
        }
    }

    public Context getProcessInstanceByCorrelationKey(CorrelationKey correlationKey, EntityManager em) {
        Query processInstancesForEvent = em.createNamedQuery("GetProcessInstanceIdByCorrelation");
        // set parameter String{"elem_count"} to Query{processInstancesForEvent}
        processInstancesForEvent.setParameter("elem_count", new Long(correlationKey.getProperties().size()));
        List<Object> properties = new ArrayList<Object>();
        for (CorrelationProperty<?> property : correlationKey.getProperties()) {
            properties.add(property.getValue());
        }
        // set parameter String{"properties"} to Query{processInstancesForEvent}
        processInstancesForEvent.setParameter("properties", properties);
        try {
            return ProcessInstanceIdContext.get(((Long) (processInstancesForEvent.getSingleResult())));
        } catch (NonUniqueResultException e) {
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Object findContextId(Long ksessionId, String ownerId) {
        JPAMapper.EntityManagerInfo info = getEntityManager(null);
        EntityManager em = info.getEntityManager();
        try {
            Query findQuery = em.createNamedQuery("FindContextMapingByKSessionId").setParameter("ksessionId", ksessionId).setParameter("ownerId", ownerId);
            @SuppressWarnings(value = "unchecked")
            List<ContextMappingInfo> contextMapping = findQuery.getResultList();
            if (contextMapping.isEmpty()) {
                return null;
            }else
                if ((contextMapping.size()) == 1) {
                    return contextMapping.get(0).getContextId();
                }else {
                    return contextMapping.stream().map(( cmi) -> cmi.getContextId()).collect(java.util.stream.Collectors.toList());
                }
            
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            return null;
        } finally {
            if (!(info.isShared())) {
                em.close();
            }
        }
    }

    private JPAMapper.EntityManagerInfo getEntityManager(Context context) {
        Environment env = null;
        if (context instanceof EnvironmentAwareProcessInstanceContext) {
            env = ((EnvironmentAwareProcessInstanceContext) (context)).getEnvironment();
        }
        if (env != null) {
            EntityManager em = ((EntityManager) (env.get(CMD_SCOPED_ENTITY_MANAGER)));
            if (em != null) {
                return new JPAMapper.EntityManagerInfo(em, true);
            }
            EntityManagerFactory emf = ((EntityManagerFactory) (env.get(ENTITY_MANAGER_FACTORY)));
            if (emf != null) {
                return new JPAMapper.EntityManagerInfo(emf.createEntityManager(), false);
            }
        }else {
            return new JPAMapper.EntityManagerInfo(emf.createEntityManager(), false);
        }
        throw new RuntimeException("Could not find EntityManager, both command-scoped EM and EMF in environment are null");
    }

    private class EntityManagerInfo {
        private EntityManager entityManager;

        private boolean shared;

        public EntityManagerInfo(EntityManager entityManager, boolean shared) {
            this.entityManager = entityManager;
            this.shared = shared;
        }

        public EntityManager getEntityManager() {
            return entityManager;
        }

        public boolean isShared() {
            return shared;
        }
    }

    @SuppressWarnings(value = "unchecked")
    public List<Long> findKSessionToInit(String ownerId) {
        EntityManager em = emf.createEntityManager();
        Query findQuery = em.createNamedQuery("FindKSessionToInit").setParameter("ownerId", ownerId);
        return findQuery.getResultList();
    }

    @SuppressWarnings(value = "unchecked")
    public List<String> findContextIdForEvent(String eventType, String ownerId) {
        EntityManager em = emf.createEntityManager();
        Query findQuery = em.createNamedQuery("FindProcessInstanceWaitingForEvent").setParameter("eventType", eventType).setParameter("ownerId", ownerId);
        return findQuery.getResultList();
    }
}

