/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.chemistry.opencmis.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringDefinitionImpl;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.inmemory.server.InMemoryObjectServiceImpl;
import org.apache.chemistry.opencmis.inmemory.server.InMemoryRepositoryServiceImpl;
import org.apache.chemistry.opencmis.inmemory.server.InMemoryServiceFactoryImpl;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoreManager;
import org.apache.chemistry.opencmis.inmemory.types.InMemoryDocumentTypeDefinition;
import org.apache.chemistry.opencmis.inmemory.types.PropertyCreationHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Temporary test class until CMIS 1.1 bindings are completed. Until then
 * we use a special setup procedure to directly connect to the repository
 * service implementation of InMemory.
 * 
 * @author Jens
 */
public class RepositoryServiceMutabilityTest extends AbstractServiceTest {

    private static final Log log = LogFactory.getLog(RepositoryServiceTest.class);
    private static final String REPOSITORY_ID = "UnitTestRepository";
    private static final String TYPE_ID_MUTABILITY = "BookTypeAddedLater";

    private InMemoryRepositoryServiceImpl repSvc;
    private InMemoryObjectServiceImpl objSvc;

    @Override
    @Before
    public void setUp() {
        super.setTypeCreatorClass(UnitTestTypeSystemCreator.class.getName());
        super.setUp();
        
        Map<String, String> parameters = new HashMap<String, String>();

        // attach repository info to the session:
        parameters.put(ConfigConstants.TYPE_CREATOR_CLASS, getTypeCreatorClass());
        parameters.put(ConfigConstants.REPOSITORY_ID, REPOSITORY_ID);
        
        InMemoryServiceFactoryImpl factory = new InMemoryServiceFactoryImpl();
        factory.init(parameters);
        StoreManager storeManager = factory.getStoreManger();
        repSvc = new InMemoryRepositoryServiceImpl(storeManager);
        objSvc = new InMemoryObjectServiceImpl(storeManager);
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testRepositoryInfo() {
        log.info("starting testRepositoryInfo() ...");
        List<RepositoryInfo> repositories = repSvc.getRepositoryInfos(fTestCallContext, null);
        assertNotNull(repositories);
        assertFalse(repositories.isEmpty());

        log.info("geRepositoryInfo(), found " + repositories.size() + " repository/repositories).");

        for (RepositoryInfo repository : repositories) {
            RepositoryInfo repository2 = repSvc.getRepositoryInfo(fTestCallContext, repository.getId(), null);
            assertNotNull(repository2);
            assertEquals(repository.getId(), repository2.getId());
            log.info("found repository" + repository2.getId());
        }

        log.info("... testRepositoryInfo() finished.");
    }
    

    @Test
    public void testTypeMutabilityCreation() throws Exception {
        log.info("");
        log.info("starting testTypeMutabilityCreation() ...");
        TypeDefinition typeDefRef = getTypeForAddingAtRuntime();
        String repositoryId = getRepositoryId();
        // add type.
        repSvc.createTypeDefinition(repositoryId, new Holder<TypeDefinition>(typeDefRef), null);
        TypeDefinition type = repSvc.getTypeDefinition(fTestCallContext, repositoryId, typeDefRef.getId(), null);
        assertEquals(typeDefRef.getId(), type.getId());
        assertEquals(typeDefRef.getDescription(), type.getDescription());
        assertEquals(typeDefRef.getDisplayName(), type.getDisplayName());
        assertEquals(typeDefRef.getLocalName(), type.getLocalName());
        assertEquals(typeDefRef.getLocalNamespace(), type.getLocalNamespace());
        RepositoryServiceTest.containsAllBasePropertyDefinitions(type);
        log.info("... testTypeMutabilityCreation() finished.");
    }

    @Test
    public void testTypeMutabilityUpdate() throws Exception {
        log.info("");
        log.info("starting testTypeMutabilityUpdate() ...");
        TypeDefinition typeDefRef = getTypeForAddingAtRuntime();
        String repositoryId = getRepositoryId();
        repSvc.createTypeDefinition(repositoryId, new Holder<TypeDefinition>(typeDefRef), null);
        // update type.
        try {
            repSvc.updateTypeDefinition(repositoryId, new Holder<TypeDefinition>(typeDefRef), null);
            fail("updating a type should throw exception.");
        } catch (Exception e) {
            assert(e instanceof CmisNotSupportedException);
        }
        log.info("... testTypeMutabilityUpdate() finished.");
    }
    @Test
    public void testTypeMutabilityDeletion() throws Exception {
        log.info("");
        log.info("starting testTypeMutabilityDeletion() ...");
        TypeDefinition typeDefRef = getTypeForAddingAtRuntime();
        String repositoryId = getRepositoryId();
        repSvc.createTypeDefinition(repositoryId, new Holder<TypeDefinition>(typeDefRef), null);
        
        String docId = createDoc("Book1", getRootFolderId(REPOSITORY_ID), TYPE_ID_MUTABILITY);
        
        // try deleting type, should fail, because in use.
        try {
            repSvc.deleteTypeDefinition(repositoryId, TYPE_ID_MUTABILITY, null);
            fail("deleting a type which is in use should throw exception.");
        } catch (Exception e) {
            assert(e instanceof CmisInvalidArgumentException);
        }

        objSvc.deleteObject(fTestCallContext, fRepositoryId, docId, true, null);
        
        try {
            repSvc.deleteTypeDefinition(repositoryId, TYPE_ID_MUTABILITY, null);
        } catch (Exception e) {
            fail("deleting a type which is in not in use should not throw exception! Exception is: " + e);
        }
        
        try {
            repSvc.getTypeDefinition(fTestCallContext, repositoryId, TYPE_ID_MUTABILITY, null);
            fail("getting a type after it was deleted should fail.");
        } catch (Exception e) {
        }

        try {
            repSvc.deleteTypeDefinition(repositoryId, BaseTypeId.CMIS_DOCUMENT.name(), null);
            fail("deleting a CMIS base type throw exception.");
        } catch (Exception e) {
            assert(e instanceof CmisInvalidArgumentException);
        }
        try {
            repSvc.deleteTypeDefinition(repositoryId, BaseTypeId.CMIS_FOLDER.name(), null);
            fail("deleting a CMIS base type throw exception.");
        } catch (Exception e) {
            assert(e instanceof CmisInvalidArgumentException);
        }

        log.info("... testTypeMutabilityDeletion() finished.");
    }

    private String getRepositoryId() {
        List<RepositoryInfo> repositories = repSvc.getRepositoryInfos(fTestCallContext, null);
        RepositoryInfo repository = repositories.get(0);
        assertNotNull(repository);
        return repository.getId();
    }

    private String getRootFolderId(String repositoryId) {
        RepositoryInfo repository = repSvc.getRepositoryInfo(fTestCallContext, repositoryId, null);
        assertNotNull(repository);
        return repository.getRootFolderId();
    }

    private TypeDefinition getTypeForAddingAtRuntime() {
        
        InMemoryDocumentTypeDefinition cmisLaterType = new InMemoryDocumentTypeDefinition(TYPE_ID_MUTABILITY,
                "Type with two properties", InMemoryDocumentTypeDefinition.getRootDocumentType());

        Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

        PropertyIntegerDefinitionImpl prop1 = PropertyCreationHelper.createIntegerDefinition("Number",
                "Sample Int Property", Updatability.READWRITE);
        propertyDefinitions.put(prop1.getId(), prop1);

        PropertyStringDefinitionImpl prop2 = PropertyCreationHelper.createStringDefinition("Title",
                "Sample String Property", Updatability.READWRITE);
        propertyDefinitions.put(prop2.getId(), prop2);
        
        cmisLaterType.addCustomPropertyDefinitions(propertyDefinitions);
        
        return cmisLaterType;
    }

    String createDoc(String name, String folderId, String typeId) {
        ContentStream contentStream = null;
        List<String> policies = null;
        ExtensionsData extension = null;

        Properties props = createDocumentProperties(name, typeId);

        String id = objSvc.createDocument(fTestCallContext, fRepositoryId, props, folderId, contentStream,
                VersioningState.NONE, policies, null, null, extension);
        return id;        
    }
}
