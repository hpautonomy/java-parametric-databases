/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.hod.databases;

import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.hod.fields.IndexFieldsService;

import java.util.Set;

/**
 * Abstract base class for {@link ResourceMapper}
 */
public abstract class AbstractResourceMapper implements ResourceMapper {

    private final IndexFieldsService indexFieldsService;

    protected AbstractResourceMapper(final IndexFieldsService indexFieldsService) {
        this.indexFieldsService = indexFieldsService;
    }

    /**
     * Converts the given resource name to a database
     * @param tokenProxy The token proxy to use to retrieve parametric fields
     * @param resource The resource
     * @param domain The domain of the resource
     * @return A database representation of the resource
     * @throws HodErrorException
     */
    protected Database databaseForResource(final TokenProxy<?, TokenType.Simple> tokenProxy, final Resource resource, final String domain) throws HodErrorException {
        final ResourceIdentifier resourceIdentifier = new ResourceIdentifier(domain, resource.getResource());
        final Set<String> parametricFields;

        if (tokenProxy == null) {
            parametricFields = indexFieldsService.getParametricFields(resourceIdentifier);
        }
        else {
            parametricFields = indexFieldsService.getParametricFields(tokenProxy, resourceIdentifier);
        }

        return new Database.Builder()
            .setName(resource.getResource())
            .setDisplayName(resource.getDisplayName())
            .setIsPublic(ResourceIdentifier.PUBLIC_INDEXES_DOMAIN.equals(domain))
            .setDomain(domain)
            .setIndexFields(parametricFields)
            .build();
    }

}
