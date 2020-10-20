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
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.hod.fields.IndexFieldsService;

import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of ResourceMapper
 */
public class ResourceMapperImpl extends AbstractResourceMapper {

    public ResourceMapperImpl(final IndexFieldsService indexFieldsService) {
        super(indexFieldsService);
    }

    @Override
    public Set<Database> map(final TokenProxy<?, TokenType.Simple> tokenProxy, final Set<Resource> resources, final String domain) throws HodErrorException {
        final Set<Database> databases = new HashSet<>();

        for (final Resource resource : resources) {
            databases.add(databaseForResource(tokenProxy, resource, domain));
        }

        return databases;
    }
}
