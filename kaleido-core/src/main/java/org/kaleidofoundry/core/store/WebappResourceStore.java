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

import static org.kaleidofoundry.core.store.ResourceStoreConstants.WebappStorePluginName;

import java.io.InputStream;
import java.net.URI;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.web.ServletContextProvider;

/**
 * Webapp resource store
 * 
 * @author Jerome RADUGET
 */
@Declare(WebappStorePluginName)
public class WebappResourceStore extends AbstractResourceStore implements ResourceStore {

   /**
    * @param context
    */
   public WebappResourceStore(@NotNull final RuntimeContext<ResourceStore> context) {
	super(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#getStoreType()
    */
   @Override
   public ResourceStoreType[] getStoreType() {
	return new ResourceStoreType[] { ResourceStoreTypeEnum.webapp };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doLoad(java.net.URI)
    */
   @Override
   protected ResourceHandler doGet(final URI resourceUri) throws StoreException {
	String localName = FileHelper.buildCustomPath(resourceUri.getPath(), FileHelper.WEBAPP_SEPARATOR);
	InputStream input = ServletContextProvider.getServletContext().getResourceAsStream(localName);

	if (input == null) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	} else {
	   return new ResourceHandlerBean(input);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceUri) throws StoreException {
	throw new IllegalStateException("Can't remove a resource from webapp classpath. WebappResourceStore is for a readonly use");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doStore(java.net.URI,
    * org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws StoreException {
	throw new IllegalStateException("Can't store a resource from webapp classpath. WebappResourceStore is for a readonly use");
   }

}