/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public class OrganizationDataFetcher implements DataFetcher<GitHubOrganization>, Serializable {

	public static final OrganizationDataFetcher INSTANCE = new OrganizationDataFetcher();

	private OrganizationDataFetcher() {
	}

	@Override
	public List<GitHubOrganization> fetch(DataFetchContext context) {
		try {
			List<GitHubGraphQlClient> githubClient = GitHubConnectorConfig.GITHUB_GRAPHQL_CLIENT.getAll(context);
			List<GitHubOrganization> organizationList = new ArrayList<>();

			for ( GitHubGraphQlClient client : githubClient) {
				organizationList.addAll(client.fetchOrganizationsWithDetails());
			}

			return organizationList;
		} catch (RuntimeException e) {
			throw new DataFetcherException("Could not fetch organizations list from Github GraphQL API", e);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( GitHubOrganization.class, GitHubConventionContext.INSTANCE);
	}
}
