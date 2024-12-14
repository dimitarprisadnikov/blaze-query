/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.api.ProjectsApi;
import com.blazebit.query.connector.jira.cloud.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.model.Project;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectDataFetcher implements DataFetcher<Project>, Serializable {

	public static final ProjectDataFetcher INSTANCE = new ProjectDataFetcher();

	private ProjectDataFetcher() {
	}

	@Override
	public List<Project> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudConnectorConfig.API_CLIENT.getAll( context );
			List<Project> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				ProjectsApi api = new ProjectsApi( apiClient );
				list.addAll( api.getAllProjects( null, null, null ) );
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch project list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Project.class, JiraCloudConventionContext.INSTANCE );
	}
}
