/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.databases;

import com.hp.autonomy.fields.IndexFieldsService;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class ParallelResourceMapper extends AbstractResourceMapper {

    private final ExecutorService executorService;

    public ParallelResourceMapper(final IndexFieldsService indexFieldsService) {
        this(indexFieldsService, Executors.newFixedThreadPool(16));
    }

    public ParallelResourceMapper(final IndexFieldsService indexFieldsService, final ExecutorService executorService) {
        super(indexFieldsService);

        this.executorService = executorService;
    }

    public void destroy() {
        executorService.shutdown();
    }

    @Override
    public Set<Database> map(final TokenProxy<?, TokenType.Simple> tokenProxy, final Set<String> resources, final String domain) throws HodErrorException {
        final List<Future<Database>> databaseFutures = new ArrayList<>();

        for (final String resource : resources) {
            databaseFutures.add(executorService.submit(new DatabaseCallable(tokenProxy, resource, domain)));
        }

        final Set<Database> databases = new HashSet<>();

        for (final Future<Database> databaseFuture : databaseFutures) {
            try {
                databases.add(databaseFuture.get());
            } catch (final InterruptedException e) {
                // preserve interrupted status
                Thread.currentThread().interrupt();
                // anything we return may be incomplete
                throw new IllegalStateException("Interrupted while waiting for parametric fields", e);
            } catch (final ExecutionException e) {
                if (e.getCause() instanceof HodErrorException) {
                    throw (HodErrorException) e.getCause();
                }
                else {
                    throw new IllegalStateException("Error occurred executing task", e.getCause());
                }
            }
        }

        return databases;
    }

    private class DatabaseCallable implements Callable<Database> {

        private final TokenProxy<?, TokenType.Simple> tokenProxy;
        private final String resourceName;
        private final String domain;

        private DatabaseCallable(final TokenProxy<?, TokenType.Simple> tokenProxy, final String resourceName, final String domain) {
            this.tokenProxy = tokenProxy;
            this.resourceName = resourceName;
            this.domain = domain;
        }

        @Override
        public Database call() throws HodErrorException {
            return databaseForResource(tokenProxy, resourceName, domain);
        }
    }

}
