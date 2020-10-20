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

package com.hp.autonomy.hod.fields;

import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.fields.FieldType;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsResponse;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;

import java.util.HashSet;
import java.util.Set;

public class IndexFieldsServiceImpl implements IndexFieldsService {

    private final RetrieveIndexFieldsService retrieveIndexFieldsService;

    public IndexFieldsServiceImpl(final RetrieveIndexFieldsService retrieveIndexFieldsService) {
        this.retrieveIndexFieldsService = retrieveIndexFieldsService;
    }

    @Override
    public Set<String> getParametricFields(final ResourceIdentifier index) throws HodErrorException {
        return getParametricFields(null, index);
    }

    @Override
    public Set<String> getParametricFields(final TokenProxy<?, TokenType.Simple> tokenProxy, final ResourceIdentifier index) throws HodErrorException {
        final RetrieveIndexFieldsRequestBuilder fieldsParams = new RetrieveIndexFieldsRequestBuilder()
            .setFieldType(FieldType.parametric);

        final RetrieveIndexFieldsResponse indexFields;

        if (tokenProxy == null) {
            indexFields = retrieveIndexFieldsService.retrieveIndexFields(index, fieldsParams);
        }
        else {
            indexFields = retrieveIndexFieldsService.retrieveIndexFields(tokenProxy, index, fieldsParams);
        }

        return new HashSet<>(indexFields.getAllFields());
    }

}
