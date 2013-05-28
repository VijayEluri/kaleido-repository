/*
 *  Copyright 2008-2011 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.store;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


/**
 * rest exception mapper for {@link FileStoreNotFoundException}
 * 
 * @author Jerome RADUGET
 */
@Provider
public class FileStoreNotFoundMapper implements ExceptionMapper<FileStoreNotFoundException> {

   public Response toResponse(final FileStoreNotFoundException ex) {
	return Response.status(404).entity(ex.getMessage()).type("text/plain").build();
   }

}