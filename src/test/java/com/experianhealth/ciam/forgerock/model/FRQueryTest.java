package com.experianhealth.ciam.forgerock.model;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.experianhealth.ciam.forgerock.model.FRQueryFilter.*;
import static com.experianhealth.ciam.forgerock.model.FRQuery.*;


public class FRQueryTest {
    @Test
    public void testQueryURIWithAllParts() {
        FRQuery.Builder queryBuilder = FRQuery.Builder.create();
        queryBuilder.withFilterExpression(eq("sn","Petras"));
        queryBuilder.withReturnFields("userName", "effectiveRoles", "sn", "givenName");
        queryBuilder.withSortKeys("userName", "givenName");
        queryBuilder.withPageSize(2);
        FRQuery query = queryBuilder.build();

        URI queryURI = query.buildURI("/managed/user");
        String actual = queryURI.toString();
        String expected = "/managed/user?_queryFilter=sn+eq+%22Petras%22&_fields=userName%2CeffectiveRoles%2Csn%2CgivenName&_sortKeys=userName%2CgivenName&_pageSize=2";
        assertEquals(expected, actual);
    }

    @Test
    /**
     * Test that if no filterExpression is set that it uses _queryId=query-all
     */
    public void testQueryAllWithOtherOptions() {
        FRQuery.Builder queryBuilder = FRQuery.Builder.create();
        queryBuilder.withReturnFields("userName", "effectiveRoles", "sn", "givenName");
        queryBuilder.withSortKeys("userName", "givenName");
        queryBuilder.withPageSize(2);
        FRQuery query = queryBuilder.build();

        URI queryURI = query.buildURI("/managed/user");
        String actual = queryURI.toString();
        String expected = "/managed/user?_queryId=query-all&_fields=userName%2CeffectiveRoles%2Csn%2CgivenName&_sortKeys=userName%2CgivenName&_pageSize=2";
        assertEquals(expected, actual);
    }

    @Test
    /**
     * Test that if no filterExpression is set that it uses _queryId=query-all
     */
    public void testQueryShortcut() {

        FRQuery query = FRQuery.queryAll();

        URI queryURI = query.buildURI("/managed/user");
        String actual = queryURI.toString();
        String expected = "/managed/user?_queryId=query-all";
        assertEquals(expected, actual);
    }

    @Test
    /**
     * Test that if no filterExpression is set that it uses _queryId=query-all
     */
    public void testQueryAllNoOptions() {
        FRQuery.Builder queryBuilder = FRQuery.Builder.create();
        FRQuery query = queryBuilder.build();

        URI queryURI = query.buildURI("/managed/user");
        String actual = queryURI.toString();
        String expected = "/managed/user?_queryId=query-all";
        assertEquals(expected, actual);
    }
}
