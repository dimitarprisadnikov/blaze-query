/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.fluent.models.TenantIdDescriptionInner;
import com.azure.resourcemanager.resources.models.Tenant;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class TenantDataFetcher implements DataFetcher<TenantIdDescriptionInner>, Serializable {

    public static final TenantDataFetcher INSTANCE = new TenantDataFetcher();

    private TenantDataFetcher() {
    }

    @Override
    public List<TenantIdDescriptionInner> fetch(DataFetchContext context) {
        try {
            List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll( context);
            List<TenantIdDescriptionInner> list = new ArrayList<>();
            for (AzureResourceManager resourceManager : resourceManagers) {
                for (Tenant tenant : resourceManager.tenants().list()) {
                    list.add(tenant.innerModel());
                }
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch subscription list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.componentMethodConvention(TenantIdDescriptionInner.class, AzureResourceManagerConventionContext.INSTANCE);
    }
}
