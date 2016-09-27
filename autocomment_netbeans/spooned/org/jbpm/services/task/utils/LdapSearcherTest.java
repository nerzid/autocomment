/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.services.task.utils;

import org.assertj.core.api.Assertions;
import javax.naming.AuthenticationException;
import org.junit.Before;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.directory.InvalidSearchFilterException;
import org.jbpm.services.task.identity.LDAPBaseTest;
import java.util.List;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import java.util.Properties;
import javax.naming.directory.SearchResult;
import org.junit.Test;

public class LdapSearcherTest extends LDAPBaseTest {
    private static final String CONTEXT = "ou=People,dc=jbpm,dc=org";

    private static final String FILTER = "(uid=*)";

    private Properties config;

    @Before
    public void prepareDefaultConfiguration() {
        config = new Properties();
        config.setProperty(Context.PROVIDER_URL, SERVER_URL);
        config.setProperty(Context.SECURITY_PRINCIPAL, USER_DN);
        config.setProperty(Context.SECURITY_CREDENTIALS, PASSWORD);
    }

    private void testInvalidSearch(Class<? extends Exception> exceptionClass) {
        testInvalidSearch(exceptionClass, LdapSearcherTest.CONTEXT, LdapSearcherTest.FILTER);
    }

    private void testInvalidSearch(Class<? extends Exception> exceptionClass, String context, String filter) {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        try {
            ldapSearcher.search(context, filter);
            Assertions.fail(((exceptionClass.getName()) + " should have been thrown"));
        } catch (RuntimeException ex) {
            assertThat(ex).hasCauseInstanceOf(exceptionClass);
        }
    }

    @Test
    public void testSearchInvalidUrl() {
        config.setProperty(Context.PROVIDER_URL, "ldap://localhost:1389");
        testInvalidSearch(CommunicationException.class);
    }

    @Test
    public void testSearchInvalidUsername() {
        config.setProperty(Context.SECURITY_PRINCIPAL, "admin");
        testInvalidSearch(InvalidNameException.class);
    }

    @Test
    public void testSearchWrongPassword() {
        config.setProperty(Context.SECURITY_CREDENTIALS, "password");
        testInvalidSearch(AuthenticationException.class);
    }

    @Test
    public void testSearchNotExistingContext() {
        testInvalidSearch(NameNotFoundException.class, "ou=Animals,dc=jbpm,dc=org", LdapSearcherTest.FILTER);
    }

    @Test
    public void testSearchEmptyFilter() {
        testInvalidSearch(InvalidSearchFilterException.class, LdapSearcherTest.CONTEXT, "");
    }

    @Test
    public void testSearchWithFilterArgumentWithoutValue() {
        testInvalidSearch(InvalidSearchFilterException.class, LdapSearcherTest.CONTEXT, "(uid={0})");
    }

    @Test
    public void testSearchWithoutFilterArguments() {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        List<SearchResult> searchResults = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=john)").getSearchResults();
        assertThat(searchResults).isNotEmpty().hasSize(1);
    }

    @Test
    public void testSearchWithOneFilterArgument() {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        List<SearchResult> searchResults = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid={0})", "john").getSearchResults();
        assertThat(searchResults).isNotEmpty().hasSize(1);
    }

    @Test
    public void testSearchWithTwoFilterArguments() {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        List<SearchResult> searchResults = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(|(uid={0})(uid={1}))", "john", "mary").getSearchResults();
        assertThat(searchResults).isNotEmpty().hasSize(2);
    }

    @Test
    public void testGetSingleSearchResult() throws NamingException {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        SearchResult searchResult = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=john)").getSingleSearchResult();
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.getAttributes().get("uid").get()).isEqualTo("john");
    }

    @Test
    public void testGetSingleSearchResultEmpty() {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        SearchResult searchResult = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=peter)").getSingleSearchResult();
        assertThat(searchResult).isNull();
    }

    @Test
    public void testGetSingleSearchResultFromMultiple() throws NamingException {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        SearchResult searchResult = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=*)").getSingleSearchResult();
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.getAttributes().get("uid").get()).isEqualTo("john");
    }

    private void testGetSearchResults(SearchScope searchScope, String... expectedUsers) {
        if (searchScope != null) {
            config.setProperty(LdapSearcher.SEARCH_SCOPE, searchScope.name());
        } 
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        List<SearchResult> searchResults = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=*)").getSearchResults();
        assertThat(searchResults).extracting(( searchResult) -> {
            try {
                return searchResult.getAttributes().get("uid").get();
            } catch ( ex) {
                throw new RuntimeException(ex);
            }
        }).containsOnly(expectedUsers);
    }

    @Test
    public void testGetSearchResultsObjectScope() throws NamingException {
        testGetSearchResults(SearchScope.OBJECT_SCOPE);
    }

    @Test
    public void testGetSearchResultsDefaultScope() throws NamingException {
        testGetSearchResults(null, "john", "mary");
    }

    @Test
    public void testGetSearchResultsOneLevelScope() throws NamingException {
        testGetSearchResults(SearchScope.ONELEVEL_SCOPE, "john", "mary");
    }

    @Test
    public void testGetSearchResultsSubtreeScope() {
        testGetSearchResults(SearchScope.SUBTREE_SCOPE, "john", "mary", "peter", "mike");
    }

    @Test
    public void testGetSingleAttributeResult() {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        String attributeResult = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=john)").getSingleAttributeResult("uid");
        assertThat(attributeResult).isNotNull().isEqualTo("john");
    }

    @Test
    public void testGetSingleAttributeResultEmpty() {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        String attributeResult = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=peter)").getSingleAttributeResult("uid");
        assertThat(attributeResult).isNull();
    }

    @Test
    public void testGetSingleAttributeFromMultiple() {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        String attributeResult = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=*)").getSingleAttributeResult("uid");
        assertThat(attributeResult).isNotNull().isEqualTo("john");
    }

    @Test
    public void testGetSingleAttributeResultNotExistingAttribute() {
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        String attributeResult = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=john)").getSingleAttributeResult("xyz");
        assertThat(attributeResult).isNull();
    }

    private void testGetAttributeResults(SearchScope searchScope, String... expectedUsers) {
        if (searchScope != null) {
            config.setProperty(LdapSearcher.SEARCH_SCOPE, searchScope.name());
        } 
        LdapSearcher.LdapSearcher ldapSearcher = new LdapSearcher.LdapSearcher(config);
        List<String> attributeResults = ldapSearcher.search(LdapSearcherTest.CONTEXT, "(uid=*)").getAttributeResults("uid");
        assertThat(attributeResults).containsOnly(expectedUsers);
    }

    @Test
    public void testGetAttributeResultsObjectScope() {
        testGetAttributeResults(SearchScope.OBJECT_SCOPE);
    }

    @Test
    public void testGetAttributeResultsDefaultScope() {
        testGetAttributeResults(null, "john", "mary");
    }

    @Test
    public void testGetAttributeResultsOneLevelScope() {
        testGetAttributeResults(SearchScope.ONELEVEL_SCOPE, "john", "mary");
    }

    @Test
    public void testGetAttributeResultsSubtreeScope() {
        testGetAttributeResults(SearchScope.SUBTREE_SCOPE, "john", "mary", "peter", "mike");
    }
}

