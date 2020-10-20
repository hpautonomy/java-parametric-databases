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

import java.util.Set;

/**
 * Maps resource names into databases
 */
public interface ResourceMapper {

    /**
     * Maps the given resource names in the given domain into instances of {@link Database}
     * @param tokenProxy The token proxy to use. May be null.
     * @param resources The resources to map.
     * @param domain The domain in which the resources reside.
     * @return A set of Databases corresponding to the resource names
     * @throws HodErrorException
     */
    Set<Database> map(TokenProxy<?, TokenType.Simple> tokenProxy, Set<Resource> resources, String domain) throws HodErrorException;

}
