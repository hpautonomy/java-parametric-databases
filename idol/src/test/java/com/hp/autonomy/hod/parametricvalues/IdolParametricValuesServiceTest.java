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

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.idol.parametricvalues.IdolParametricRequest;
import com.hp.autonomy.idol.parametricvalues.IdolParametricValuesService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.types.idol.FlatField;
import com.hp.autonomy.types.idol.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.GetTagNamesResponseData;
import com.hp.autonomy.types.idol.TagValue;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolParametricValuesServiceTest {
    @Mock
    private AciService contentAciService;

    @Mock
    private AciResponseJaxbProcessorFactory aciResponseProcessorFactory;

    @Mock
    private JAXBElement<? extends Serializable> element;

    private IdolParametricValuesService parametricValuesService;

    @Before
    public void setUp() {
        parametricValuesService = new IdolParametricValuesService(contentAciService, aciResponseProcessorFactory);
    }

    @Test
    public void getAllParametricValues() {
        final IdolParametricRequest idolParametricRequest = new IdolParametricRequest.Builder().setDatabases(Collections.<String>emptySet()).setFieldNames(Collections.singleton("Some field")).setQueryText("*").setFieldText("").build();

        final GetQueryTagValuesResponseData responseData = mockQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);
        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getFieldNamesFirst() {
        final IdolParametricRequest idolParametricRequest = new IdolParametricRequest.Builder().setDatabases(Collections.<String>emptySet()).setFieldNames(Collections.<String>emptySet()).setQueryText("*").setFieldText("").build();

        final GetTagNamesResponseData tagNamesResponseData = mockTagNamesResponse();

        final GetQueryTagValuesResponseData responseData = mockQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(tagNamesResponseData).thenReturn(responseData);

        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void parametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = new IdolParametricRequest.Builder().setDatabases(Collections.<String>emptySet()).setFieldNames(Collections.<String>emptySet()).setQueryText("*").setFieldText("").setMinDate(null).setMaxDate(DateTime.now()).build();

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(new GetTagNamesResponseData());

        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(empty()));
    }

    private GetTagNamesResponseData mockTagNamesResponse() {
        final GetTagNamesResponseData responseData = new GetTagNamesResponseData();
        final GetTagNamesResponseData.Name name = new GetTagNamesResponseData.Name();
        name.setValue("DOCUMENT/CATEGORY");
        responseData.getName().add(name);
        return responseData;
    }

    private GetQueryTagValuesResponseData mockQueryResponse() {
        final GetQueryTagValuesResponseData responseData = new GetQueryTagValuesResponseData();
        final FlatField field = new FlatField();
        field.getName().add("Some name");
        when(element.getName()).thenReturn(new QName("", "value"));
        final TagValue tagValue = new TagValue();
        tagValue.setValue("Some field");
        tagValue.setCount(5);
        when(element.getValue()).thenReturn(tagValue);
        field.getValueOrSubvalueOrValues().add(element);
        responseData.getField().add(field);
        return responseData;
    }
}
