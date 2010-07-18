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
package org.kaleidofoundry.core.config;

import java.io.IOException;
import java.net.URI;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.store.SingleResourceStore;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.system.OsEnvironment;

/**
 * Operating system environment variable configuration implementation (read only)<br/>
 * It will reference all operating system environment variable defined via set command
 * 
 * @author Jerome RADUGET
 */
@Declare(ConfigurationConstants.OsEnvConfigurationPluginName)
public class OsEnvConfiguration extends AbstractConfiguration implements Configuration {

   /**
    * @param identifier
    * @param resourceUri ignored
    * @param context
    * @throws StoreException
    */
   public OsEnvConfiguration(final String identifier, final URI resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(identifier, URI.create("memory:/internal/" + identifier + ".osenv"), context);
   }

   /**
    * @param identifier
    * @param resourceUri ignored
    * @param context
    * @throws StoreException
    */
   public OsEnvConfiguration(final String identifier, final String resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(identifier, "memory:/internal/" + identifier + ".osenv", context);
   }

   /**
    * @param identifier
    * @param context
    * @throws StoreException
    */
   public OsEnvConfiguration(final String identifier, final RuntimeContext<Configuration> context) throws StoreException {
	this(identifier, (String) null, context);
   }

   @Override
   protected Cache<String, String> loadProperties(final ResourceHandler resourceHandler, final Cache<String, String> properties) throws StoreException,
	   ConfigurationException {
	try {
	   OsEnvironment environment = new OsEnvironment();
	   for (String key : environment.stringPropertyNames()) {
		String value = environment.getProperty(key);
		properties.put(key, value != null ? value : "");
	   }
	   return properties;
	} catch (IOException ioe) {
	   throw new StoreException(ioe);
	}
   }

   @Override
   protected Cache<String, String> storeProperties(final Cache<String, String> cacheProperties, final SingleResourceStore resourceStore) throws StoreException,
	   ConfigurationException {
	return cacheProperties; // never called
   }

   @Override
   public boolean isReadOnly() {
	return true;
   }

}
