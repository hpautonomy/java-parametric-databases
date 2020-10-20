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

package com.hp.autonomy.idol.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.core.parametricvalues.ParametricRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collections;
import java.util.Set;

@SuppressWarnings("FieldMayBeFinal")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = IdolParametricRequest.Builder.class)
public class IdolParametricRequest implements ParametricRequest<String> {
    private static final long serialVersionUID = 3450911770365743948L;

    private Set<String> databases;
    private Set<String> fieldNames = Collections.emptySet();
    private String queryText;
    private String fieldText;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) private DateTime minDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) private DateTime maxDate;

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private Set<String> databases;
        private Set<String> fieldNames = Collections.emptySet();
        private String queryText;
        private String fieldText;
        private DateTime minDate;
        private DateTime maxDate;

        public Builder(final ParametricRequest<String> parametricRequest) {
            databases = parametricRequest.getDatabases();
            fieldNames = parametricRequest.getFieldNames();
            queryText = parametricRequest.getQueryText();
            fieldText = parametricRequest.getFieldText();
        }

        public IdolParametricRequest build() {
            return new IdolParametricRequest(databases, fieldNames, queryText, fieldText, minDate, maxDate);
        }
    }
}
