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

package com.hp.autonomy.hod.parametricvalues;

import com.google.common.collect.ImmutableSet;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldNames;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
public class HodParametricValuesServiceTest {

    @Test
    public void getsParametricValues() throws HodErrorException {
        final HodParametricValuesService parametricValuesServiceImpl = new HodParametricValuesService(getParametricValuesService());

        final Set<ResourceIdentifier> indexes = ImmutableSet.<ResourceIdentifier>builder()
                .add(ResourceIdentifier.WIKI_ENG)
                .add(ResourceIdentifier.PATENTS)
                .build();

        final Set<String> fieldNames = ImmutableSet.<String>builder()
                .add("grassy field")
                .add("wasteland")
                .add("football field")
                .build();

        final HodParametricRequest testRequest = new HodParametricRequest.Builder()
                .setDatabases(indexes)
                .setFieldNames(fieldNames)
                .build();

        final Set<QueryTagInfo> fieldNamesSet = parametricValuesServiceImpl.getAllParametricValues(testRequest);

        final Map<String, QueryTagInfo> fieldNamesMap = new HashMap<>();

        for (final QueryTagInfo parametricFieldName : fieldNamesSet) {
            fieldNamesMap.put(parametricFieldName.getName(), parametricFieldName);
        }

        assertThat(fieldNamesMap, hasKey("grassy field"));
        assertThat(fieldNamesMap, hasKey("wasteland"));
        assertThat(fieldNamesMap, hasKey("football field"));

        assertThat(fieldNamesMap, not(hasKey("empty field")));

        final QueryTagInfo grassyField = fieldNamesMap.get("grassy field");

        assertThat(grassyField.getValues(), hasItem(new QueryTagCountInfo("snakes", 33)));
    }

    @Test
    public void emptyFieldNamesReturnEmptyParametricValues() throws HodErrorException {
        final HodParametricValuesService hodParametricValuesService = new HodParametricValuesService(getParametricValuesService());

        final Set<ResourceIdentifier> indexes = ImmutableSet.<ResourceIdentifier>builder()
                .add(ResourceIdentifier.PATENTS)
                .build();

        final HodParametricRequest testRequest = new HodParametricRequest.Builder()
                .setDatabases(indexes)
                .setFieldNames(Collections.<String>emptySet())
                .build();

        final Set<QueryTagInfo> fieldNamesSet = hodParametricValuesService.getAllParametricValues(testRequest);

        assertThat(fieldNamesSet, is(empty()));
    }

    private GetParametricValuesService getParametricValuesService() throws HodErrorException {
        final GetParametricValuesService getParametricValuesService = mock(GetParametricValuesService.class);

        final Map<String, Integer> fieldsOfFootball = new LinkedHashMap<>();
        fieldsOfFootball.put("worms", 100);
        fieldsOfFootball.put("slugs", 50);

        final Map<String, Integer> fieldsOfGrass = new LinkedHashMap<>();
        fieldsOfGrass.put("birds", 65);
        fieldsOfGrass.put("snakes", 33);

        final Map<String, Integer> fieldsOfWaste = new LinkedHashMap<>();
        fieldsOfWaste.put("humans", 153);
        fieldsOfWaste.put("mutants", 45);

        final FieldNames everythingFieldNames = new FieldNames.Builder()
                .addParametricValue("football field", fieldsOfFootball)
                .addParametricValue("grassy field", fieldsOfGrass)
                .addParametricValue("wasteland", fieldsOfWaste)
                .addParametricValue("empty field", new LinkedHashMap<>())
                .build();

        //noinspection unchecked
        when(getParametricValuesService.getParametricValues(
                argThat(any(Collection.class)),
                argThat(any(Collection.class)),
                argThat(any(GetParametricValuesRequestBuilder.class))
        )).thenReturn(everythingFieldNames);

        return getParametricValuesService;
    }

}
