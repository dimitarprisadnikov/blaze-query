/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import com.blazebit.query.connector.base.ConventionContext;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

/**
 * A method filter to exclude internal and cyclic methods from the Github models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GithubConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new GithubConventionContext();

	private GithubConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		Method method = (Method) member;
		if ( method.getExceptionTypes().length != 0 ) {
			return null;
		}
		switch ( member.getName() ) {
			case "getHooks":
			case "getRoot":
				return null;
			case "getOwner":
				return concreteClass == GHAsset.class || concreteClass == GHRelease.class || concreteClass == GHContent.class
						? null : this;
			case "getParents":
				return concreteClass == GHCommit.class ? null : this;
			case "getFollowers":
				return concreteClass == GHUser.class ? NestedUserConventionContext.INSTANCE : this;
			case "getOrganizations":
				return concreteClass == GHUser.class ? NestedOrganizationConventionContext.INSTANCE : this;
			case "getParent":
				return concreteClass == GHRepository.class || concreteClass == GHIssueComment.class
						? NestedRepositoryConventionContext.INSTANCE : this;
			default:
				return this;
		}
	}

	private static class NestedUserConventionContext implements ConventionContext {

		public static final NestedUserConventionContext INSTANCE = new NestedUserConventionContext();

		private NestedUserConventionContext() {
		}

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static class NestedOrganizationConventionContext implements ConventionContext {

		public static final NestedOrganizationConventionContext INSTANCE = new NestedOrganizationConventionContext();

		private NestedOrganizationConventionContext() {
		}

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static class NestedRepositoryConventionContext implements ConventionContext {

		public static final NestedRepositoryConventionContext INSTANCE = new NestedRepositoryConventionContext();

		private NestedRepositoryConventionContext() {
		}

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}
}
