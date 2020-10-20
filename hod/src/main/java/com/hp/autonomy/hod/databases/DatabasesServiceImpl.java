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
import com.hp.autonomy.hod.client.api.resource.*;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class DatabasesServiceImpl implements DatabasesService {
    private static final Set<ResourceFlavour> CONTENT_FLAVOURS = ResourceFlavour.of(ResourceFlavour.EXPLORER, ResourceFlavour.STANDARD, ResourceFlavour.CUSTOM_FIELDS);

    private final ResourcesService resourcesService;

    private final ResourceMapper resourceMapper;

    public DatabasesServiceImpl(final ResourcesService resourcesService, final ResourceMapper resourceMapper) {
        this.resourcesService = resourcesService;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public Set<Database> getDatabases(final String domain) throws HodErrorException {
        return getDatabases(null, domain);
    }

    @Override
    public Set<Database> getDatabases(final TokenProxy<?, TokenType.Simple> tokenProxy, final String domain) throws HodErrorException {
        final ListResourcesRequestBuilder builder = new ListResourcesRequestBuilder()
            .setTypes(Collections.singleton(ResourceType.CONTENT));

        final Resources resources;

        if (tokenProxy == null) {
            resources = resourcesService.list(builder);
        }
        else {
            resources = resourcesService.list(tokenProxy, builder);
        }

        final Set<Database> databases = new HashSet<>();

        // Private and public indexes can have the same name. You can't do anything with the public index in this case,
        // so we remove the public index duplicates here.
        final Set<String> privateResourceNames = new HashSet<>();

        final Set<Resource> privateResources = new HashSet<>();

        resources.getResources().stream()
                .filter(resource -> CONTENT_FLAVOURS.contains(resource.getFlavour()))
                .forEach(resource -> {
            privateResources.add(resource);
            privateResourceNames.add(resource.getResource());
        });

        databases.addAll(resourceMapper.map(tokenProxy, privateResources, domain));

        final Set<Resource> publicResources = resources.getPublicResources()
                .stream()
                .filter(resource -> !privateResourceNames.contains(resource.getResource()))
                .collect(Collectors.toSet());

        databases.addAll(resourceMapper.map(tokenProxy, publicResources, ResourceIdentifier.PUBLIC_INDEXES_DOMAIN));

        return databases;
    }


}
