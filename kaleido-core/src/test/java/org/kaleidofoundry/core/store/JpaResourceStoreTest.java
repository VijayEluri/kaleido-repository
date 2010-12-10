/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.store;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.kaleidofoundry.core.store.entity.ResourceStoreEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class JpaResourceStoreTest extends AbstractResourceStoreTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(JpaResourceStoreTest.class);

   private EntityManagerFactory emf;
   private EntityManager em;
   private EntityTransaction transaction;

   @Before
   @Override
   public void setup() throws URISyntaxException {

	try {
	   // jpa entity manager configuration (default kaleido)
	   emf = UnmanagedEntityManagerFactory.getEntityManagerFactory();
	   // emf = UnmanagedEntityManagerFactory.getEntityManagerFactory("kaleido-core-derby-oracle");
	   em = UnmanagedEntityManagerFactory.currentEntityManager();

	   // begin transaction
	   transaction = em.getTransaction();
	   transaction.begin();

	   // create mocked entity test
	   final ResourceStoreEntity entity = new ResourceStoreEntity();
	   final URI resourceUri = URI.create("jpa:/tmp/foo.txt");
	   entity.setUri(resourceUri.getPath());
	   entity.setName(resourceUri.getPath());
	   entity.setPath(resourceUri.getPath());
	   entity.setContent(DEFAULT_RESOURCE_MOCK_TEST.getBytes());

	   em.persist(entity);

	   // resource to test
	   existingResources.put(resourceUri.getPath(), DEFAULT_RESOURCE_MOCK_TEST);
	   nonExistingResources.add("foo");

	   // resource store creation
	   final RuntimeContext<ResourceStore> context = new ResourceContextBuilder().withUriRootPath("jpa:/").build();
	   resourceStore = new JpaResourceStore(context);

	} catch (final RuntimeException rte) {
	   LOGGER.error("setup error", rte);
	   throw rte;
	}

   }

   @After
   @Override
   public void cleanup() {
	try {
	   final URI resourceUri = URI.create("jpa:/tmp/foo.txt");
	   final ResourceStoreEntity resource = em.find(ResourceStoreEntity.class, resourceUri.getPath());
	   if (resource != null) {
		em.remove(resource);
		transaction.commit();
	   }
	} finally {
	   UnmanagedEntityManagerFactory.closeItSilently(em);
	   UnmanagedEntityManagerFactory.closeItSilently(emf);
	}
   }
}
