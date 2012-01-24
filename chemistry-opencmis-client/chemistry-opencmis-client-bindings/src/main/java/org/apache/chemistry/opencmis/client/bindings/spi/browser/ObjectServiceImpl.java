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
package org.apache.chemistry.opencmis.client.bindings.spi.browser;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RenditionData;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.Constants;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.commons.spi.ObjectService;

/**
 * Object Service Browser Binding client.
 */
public class ObjectServiceImpl extends AbstractBrowserBindingService implements ObjectService {

    /**
     * Constructor.
     */
    public ObjectServiceImpl(BindingSession session) {
        setSession(session);
    }

    public String createDocument(String repositoryId, Properties properties, String folderId,
            ContentStream contentStream, VersioningState versioningState, List<String> policies, Acl addAces,
            Acl removeAces, ExtensionsData extension) {
        // build URL
        UrlBuilder url = (folderId != null ? getObjectUrl(repositoryId, folderId) : getRepositoryUrl(repositoryId));

        // prepare form data
        final FormDataWriter formData = new FormDataWriter(Constants.CMISACTION_CREATE_DOCUMENT, contentStream);
        formData.addPropertiesParameters(properties);
        formData.addParameter(Constants.PARAM_VERSIONIG_STATE, versioningState);
        formData.addPoliciesParameters(policies);
        formData.addAddAcesParameters(addAces);
        formData.addRemoveAcesParameters(removeAces);

        // send and parse
        HttpUtils.Response resp = post(url, formData.getContentType(), new HttpUtils.Output() {
            public void write(OutputStream out) throws Exception {
                formData.write(out);
            }
        });

        Map<String, Object> json = parseObject(resp.getStream(), resp.getCharset());
        ObjectData newObj = JSONConverter.convertObject(json);

        return (newObj == null ? null : newObj.getId());
    }

    public String createDocumentFromSource(String repositoryId, String sourceId, Properties properties,
            String folderId, VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces,
            ExtensionsData extension) {
        // build URL
        UrlBuilder url = (folderId != null ? getObjectUrl(repositoryId, folderId) : getRepositoryUrl(repositoryId));

        // prepare form data
        final FormDataWriter formData = new FormDataWriter(Constants.CMISACTION_CREATE_DOCUMENT_FROM_SOURCE);
        formData.addParameter(Constants.PARAM_SOURCE_ID, sourceId);
        formData.addPropertiesParameters(properties);
        formData.addParameter(Constants.PARAM_VERSIONIG_STATE, versioningState);
        formData.addPoliciesParameters(policies);
        formData.addAddAcesParameters(addAces);
        formData.addRemoveAcesParameters(removeAces);

        // send and parse
        HttpUtils.Response resp = post(url, formData.getContentType(), new HttpUtils.Output() {
            public void write(OutputStream out) throws Exception {
                formData.write(out);
            }
        });

        Map<String, Object> json = parseObject(resp.getStream(), resp.getCharset());
        ObjectData newObj = JSONConverter.convertObject(json);

        return (newObj == null ? null : newObj.getId());
    }

    public String createFolder(String repositoryId, Properties properties, String folderId, List<String> policies,
            Acl addAces, Acl removeAces, ExtensionsData extension) {
        // build URL
        UrlBuilder url = getObjectUrl(repositoryId, folderId);

        // prepare form data
        final FormDataWriter formData = new FormDataWriter(Constants.CMISACTION_CREATE_FOLDER);
        formData.addPropertiesParameters(properties);
        formData.addPoliciesParameters(policies);
        formData.addAddAcesParameters(addAces);
        formData.addRemoveAcesParameters(removeAces);

        // send and parse
        HttpUtils.Response resp = post(url, formData.getContentType(), new HttpUtils.Output() {
            public void write(OutputStream out) throws Exception {
                formData.write(out);
            }
        });

        Map<String, Object> json = parseObject(resp.getStream(), resp.getCharset());
        ObjectData newObj = JSONConverter.convertObject(json);

        return (newObj == null ? null : newObj.getId());
    }

    public String createRelationship(String repositoryId, Properties properties, List<String> policies, Acl addAces,
            Acl removeAces, ExtensionsData extension) {
        // build URL
        UrlBuilder url = getRepositoryUrl(repositoryId);

        // prepare form data
        final FormDataWriter formData = new FormDataWriter(Constants.CMISACTION_CREATE_RELATIONSHIP);
        formData.addPropertiesParameters(properties);
        formData.addPoliciesParameters(policies);
        formData.addAddAcesParameters(addAces);
        formData.addRemoveAcesParameters(removeAces);

        // send and parse
        HttpUtils.Response resp = post(url, formData.getContentType(), new HttpUtils.Output() {
            public void write(OutputStream out) throws Exception {
                formData.write(out);
            }
        });

        Map<String, Object> json = parseObject(resp.getStream(), resp.getCharset());
        ObjectData newObj = JSONConverter.convertObject(json);

        return (newObj == null ? null : newObj.getId());
    }

    public String createPolicy(String repositoryId, Properties properties, String folderId, List<String> policies,
            Acl addAces, Acl removeAces, ExtensionsData extension) {
        // build URL
        UrlBuilder url = (folderId != null ? getObjectUrl(repositoryId, folderId) : getRepositoryUrl(repositoryId));

        // prepare form data
        final FormDataWriter formData = new FormDataWriter(Constants.CMISACTION_CREATE_POLICY);
        formData.addPropertiesParameters(properties);
        formData.addPoliciesParameters(policies);
        formData.addAddAcesParameters(addAces);
        formData.addRemoveAcesParameters(removeAces);

        // send and parse
        HttpUtils.Response resp = post(url, formData.getContentType(), new HttpUtils.Output() {
            public void write(OutputStream out) throws Exception {
                formData.write(out);
            }
        });

        Map<String, Object> json = parseObject(resp.getStream(), resp.getCharset());
        ObjectData newObj = JSONConverter.convertObject(json);

        return (newObj == null ? null : newObj.getId());
    }

    public AllowableActions getAllowableActions(String repositoryId, String objectId, ExtensionsData extension) {
        // build URL
        UrlBuilder url = getObjectUrl(repositoryId, objectId, Constants.SELECTOR_ALLOWABLEACTIONS);

        // read and parse
        HttpUtils.Response resp = read(url);
        Map<String, Object> json = parseObject(resp.getStream(), resp.getCharset());

        return JSONConverter.convertAllowableActions(json);
    }

    public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
            IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
            Boolean includeAcl, ExtensionsData extension) {
        // build URL
        UrlBuilder url = getObjectUrl(repositoryId, objectId, Constants.SELECTOR_OBJECT);
        url.addParameter(Constants.PARAM_FILTER, filter);
        url.addParameter(Constants.PARAM_ALLOWABLE_ACTIONS, includeAllowableActions);
        url.addParameter(Constants.PARAM_RELATIONSHIPS, includeRelationships);
        url.addParameter(Constants.PARAM_RENDITION_FILTER, renditionFilter);
        url.addParameter(Constants.PARAM_POLICY_IDS, includePolicyIds);
        url.addParameter(Constants.PARAM_ACL, includeAcl);

        // read and parse
        HttpUtils.Response resp = read(url);
        Map<String, Object> json = parseObject(resp.getStream(), resp.getCharset());

        return JSONConverter.convertObject(json);
    }

    public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
            IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
            Boolean includeAcl, ExtensionsData extension) {
        // build URL
        UrlBuilder url = getPathUrl(repositoryId, path, Constants.SELECTOR_OBJECT);
        url.addParameter(Constants.PARAM_FILTER, filter);
        url.addParameter(Constants.PARAM_ALLOWABLE_ACTIONS, includeAllowableActions);
        url.addParameter(Constants.PARAM_RELATIONSHIPS, includeRelationships);
        url.addParameter(Constants.PARAM_RENDITION_FILTER, renditionFilter);
        url.addParameter(Constants.PARAM_POLICY_IDS, includePolicyIds);
        url.addParameter(Constants.PARAM_ACL, includeAcl);

        // read and parse
        HttpUtils.Response resp = read(url);
        Map<String, Object> json = parseObject(resp.getStream(), resp.getCharset());

        return JSONConverter.convertObject(json);
    }

    public Properties getProperties(String repositoryId, String objectId, String filter, ExtensionsData extension) {
        // build URL
        UrlBuilder url = getObjectUrl(repositoryId, objectId, Constants.SELECTOR_PROPERTIES);
        url.addParameter(Constants.PARAM_FILTER, filter);

        // read and parse
        HttpUtils.Response resp = read(url);
        Map<String, Object> json = parseObject(resp.getStream(), resp.getCharset());

        return JSONConverter.convertProperties(json);
    }

    public List<RenditionData> getRenditions(String repositoryId, String objectId, String renditionFilter,
            BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
        // build URL
        UrlBuilder url = getObjectUrl(repositoryId, objectId, Constants.SELECTOR_RENDITIONS);
        url.addParameter(Constants.PARAM_RENDITION_FILTER, renditionFilter);
        url.addParameter(Constants.PARAM_MAX_ITEMS, maxItems);
        url.addParameter(Constants.PARAM_SKIP_COUNT, skipCount);

        // read and parse
        HttpUtils.Response resp = read(url);
        List<Object> json = parseArray(resp.getStream(), resp.getCharset());

        return JSONConverter.convertRenditions(json);
    }

    public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset,
            BigInteger length, ExtensionsData extension) {
        ContentStreamImpl result = new ContentStreamImpl();

        // build URL
        UrlBuilder url = getObjectUrl(repositoryId, objectId, Constants.SELECTOR_CONTENT);
        url.addParameter(Constants.PARAM_STREAM_ID, streamId);

        // get the content
        HttpUtils.Response resp = HttpUtils.invokeGET(url, getSession(), offset, length);

        // check response code
        if ((resp.getResponseCode() != 200) && (resp.getResponseCode() != 206)) {
            throw convertStatusCode(resp.getResponseCode(), resp.getResponseMessage(), resp.getErrorContent(), null);
        }

        result.setFileName(null);
        result.setLength(resp.getContentLength());
        result.setMimeType(resp.getContentTypeHeader());
        result.setStream(resp.getStream());

        return result;
    }

    public void updateProperties(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
            Properties properties, ExtensionsData extension) {
        // TODO Auto-generated method stub

    }

    public void moveObject(String repositoryId, Holder<String> objectId, String targetFolderId, String sourceFolderId,
            ExtensionsData extension) {
        // TODO Auto-generated method stub

    }

    public void deleteObject(String repositoryId, String objectId, Boolean allVersions, ExtensionsData extension) {
        // build URL
        UrlBuilder url = getObjectUrl(repositoryId, objectId);

        // prepare form data
        final FormDataWriter formData = new FormDataWriter(Constants.CMISACTION_DELETE);
        formData.addParameter(Constants.PARAM_ALL_VERSIONS, allVersions);

        // send
        post(url, formData.getContentType(), new HttpUtils.Output() {
            public void write(OutputStream out) throws Exception {
                formData.write(out);
            }
        });
    }

    public FailedToDeleteData deleteTree(String repositoryId, String folderId, Boolean allVersions,
            UnfileObject unfileObjects, Boolean continueOnFailure, ExtensionsData extension) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setContentStream(String repositoryId, Holder<String> objectId, Boolean overwriteFlag,
            Holder<String> changeToken, ContentStream contentStream, ExtensionsData extension) {
        // TODO Auto-generated method stub

    }

    public void deleteContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
            ExtensionsData extension) {
        // TODO Auto-generated method stub

    }

}