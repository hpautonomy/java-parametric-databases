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

import com.hp.autonomy.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldNames;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.ParametricSort;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HodParametricValuesService implements ParametricValuesService<HodParametricRequest, ResourceIdentifier, HodErrorException> {

    private final GetParametricValuesService getParametricValuesService;

    public HodParametricValuesService(final GetParametricValuesService getParametricValuesService) {
        this.getParametricValuesService = getParametricValuesService;
    }

    @Override
    public Set<QueryTagInfo> getAllParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
        final GetParametricValuesRequestBuilder parametricParams = new GetParametricValuesRequestBuilder()
                .setQueryProfile(parametricRequest.getQueryProfile())
                .setSort(ParametricSort.document_count)
                .setText(parametricRequest.getQueryText())
                .setFieldText(parametricRequest.getFieldText())
                .setMaxValues(5);

        if (parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptySet();
        }

        final FieldNames fieldNames = getParametricValuesService.getParametricValues(parametricRequest.getFieldNames(),
                new ArrayList<>(parametricRequest.getDatabases()), parametricParams);
        final Set<String> fieldNamesSet = fieldNames.getFieldNames();
        final Set<QueryTagInfo> parametricFieldNames = new HashSet<>();

        for (final String name : fieldNamesSet) {
            final Set<QueryTagCountInfo> values = new HashSet<>(fieldNames.getValuesAndCountsForFieldName(name));
            if (!values.isEmpty()) {
                parametricFieldNames.add(new QueryTagInfo(name, values));
            }
        }

        return parametricFieldNames;
    }

}
