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

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.sql.DataSource;

import org.kaleidofoundry.core.lang.annotation.Stateless;

/**
 * Resource store manager interface (stateless store), used to handle persistence R instances, identify by its
 * resourceBinding B :
 * <ul>
 * <li>load
 * <li>exists
 * <li>store
 * <li>remove
 * </ul>
 * a given resource R, is identify by its resource binding (like a path)
 * 
 * @author Jerome RADUGET
 * @param <B> type of the resource access {@link URI}, {@link URL}, {@link String}, {@link DataSource} path...
 * @param <R> type of the resource to store
 */
@Stateless
public interface Store<B, R> {

   /**
    * load a given resource <br/>
    * 
    * @param resourceBinding resource binding informations to access the resource<br/>
    *           <p>
    *           you can use {@link URI}, {@link URL}, {@link String} if R is a {@link File} or {@link InputStream},<br/>
    *           you can use {@link DataSource} to access a database table<br/>
    *           ...<br/>
    *           </p>
    * @return bind resource or null if not exists
    * @throws ResourceNotFoundException if resource can't be found
    * @throws StoreException other kind of error
    */
   R get(B resourceBinding) throws StoreException;

   /**
    * store updates on current R instance<br/>
    * 
    * @param resourceBinding resource binding which have to be persist
    * @param resource resource to store
    * @return current instance of the store
    * @throws ResourceNotFoundException if resource can't be found
    * @throws StoreException other kind of error
    */
   Store<B, R> store(B resourceBinding, R resource) throws StoreException;

   /**
    * remove resource identify by its resource binding<br/>
    * 
    * @param resourceBinding resource binding which have to be removed
    * @return current instance of the store
    * @throws StoreException if resource can't be found or for other kind of error
    */
   Store<B, R> remove(B resourceBinding) throws StoreException;

   /**
    * @param resourceBinding
    * @return does the resource exists <code>true / false</code>
    * @throws StoreException other kind of error
    */
   boolean exists(B resourceBinding) throws StoreException;

}
