/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.connector.utils.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.blazebit.query.connector.utils.DateUtils.parseIsoOffsetDateTime;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.5
 */
public record GitHubRepository(
		String id,
		String name,
		String description,
		boolean isArchived,
		boolean isDisabled,
		boolean isInOrganization,
		boolean isEmpty,
		boolean isPrivate,
		Visibility visibility,
		OffsetDateTime createdAt,
		DefaultBranch defaultBranchRef,
		List<GitHubRuleset> rulesets,
		List<GitHubBranchProtectionRule> branchProtectionRules
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubRepository fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubRepository(
					json.get("id").asText(),
					json.get("name").asText(),
					json.path("description").asText(null),
					json.path("isArchived").asBoolean(false),
					json.path("isDisabled").asBoolean(false),
					json.path("isInOrganization").asBoolean(false),
					json.path("isEmpty").asBoolean(false),
					json.path("isPrivate").asBoolean(false),
					Visibility.valueOf(json.path("visibility").asText().toUpperCase()),
					parseIsoOffsetDateTime(json.path("createdAt").asText()),
					parseDefaultBranch(json.path("defaultBranchRef")),
					parseRulesets(json.path("rulesets")),
					parseBranchProtectionRules(json.path("branchProtectionRules"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GraphQlRepository", e);
		}
	}

	private static DefaultBranch parseDefaultBranch(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}
		return new DefaultBranch(
				json.path("id").asText(),
				json.path("name").asText()
		);
	}

	private static List<GitHubRuleset> parseRulesets(JsonNode json) {
		JsonNode nodesArray = json.path("nodes");
		if (nodesArray.isMissingNode() || !nodesArray.isArray() || nodesArray.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(nodesArray.spliterator(), false)
				.map(node -> GitHubRuleset.fromJson(node.toString()))
				.collect(Collectors.toList());
	}

	private static List<GitHubBranchProtectionRule> parseBranchProtectionRules(JsonNode json) {
		JsonNode nodesArray = json.path("nodes");
		if (nodesArray.isMissingNode() || !nodesArray.isArray() || nodesArray.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(nodesArray.spliterator(), false)
				.map(node -> GitHubBranchProtectionRule.fromJson(node.toString()))
				.collect(Collectors.toList());
	}

	public record DefaultBranch(String id, String name) {}

	public enum Visibility {
		PRIVATE,
		PUBLIC,
		INTERNAL,
	}
}
