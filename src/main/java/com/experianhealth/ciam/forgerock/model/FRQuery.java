package com.experianhealth.ciam.forgerock.model;

import com.experianhealth.ciam.exception.CIAMRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

/**
 * A query class for providing search options FR IDM APIS to control the search filter, sort keys, return fields and page size.
 * <p>
 * To create a query, use FRQuery.Builder.create() to obtain a builder for this class.
 * Example:
 * To create a query to return the username, sn, givenName for all users.
 * FRQuery.Builder.create().withReturnFields("username", "sn", "givenName").build();
 */
public class FRQuery {
    static final String RETURN_FIELDS = "_fields";
    static final String PAGE_SIZE = "_pageSize";
    static final String SORT_KEYS = "_sortKeys";
    static final String QUERY_ALL = "query-all";
    static final String QUERY_FILTER = "_queryFilter";
    static final String QUERY_ID = "_queryId";

    List<String> returnFields;
    Integer pageSize;
    List<String> sortKeys;

    FRQueryFilter.Expression filterExpression;
    String queryId;

    private FRQuery() {

    }

    private FRQuery(FRQuery copy) {
        if (!CollectionUtils.isEmpty(copy.sortKeys)) {
            this.sortKeys = copy.sortKeys;
        }
        if (!CollectionUtils.isEmpty(copy.returnFields)) {
            this.returnFields = copy.returnFields;
        }

        this.pageSize = copy.pageSize;
        this.filterExpression = copy.filterExpression;
    }
    public URI buildURI(String path) {
        try {
            URIBuilder builder = new URIBuilder(path);
            if (this.filterExpression != null) {
                builder.addParameter(QUERY_FILTER, this.filterExpression.render());
            } else if (this.queryId != null) {
                builder.addParameter(QUERY_ID, this.queryId);
            } else {
                builder.addParameter(QUERY_ID, QUERY_ALL);
            }
            if (!CollectionUtils.isEmpty(this.returnFields)) {
                builder.addParameter(RETURN_FIELDS, String.join(",", this.returnFields));
            }
            if (!CollectionUtils.isEmpty(this.sortKeys)) {
                builder.addParameter(SORT_KEYS, String.join(",", this.sortKeys));
            }
            if (this.pageSize != null) {
                builder.addParameter(PAGE_SIZE, this.pageSize.toString());
            }
            return builder.build();
        } catch (URISyntaxException e) {
            //should not happen, but if it does let top of stack deal with it
            throw new CIAMRuntimeException("Unexpected Problem building URI for ForgeRock API", e);
        }
    }

    /**
     * Create a query to return all results with all fields in the default sort order
     *
     * @return
     */
    public static final FRQuery queryAll() {
        return Builder.create().withQueryAll().build();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FRQuery.class.getSimpleName() + "[", "]")
                .add("returnFields=" + returnFields)
                .add("pageSize=" + pageSize)
                .add("sortKeys=" + sortKeys)
                .add("filter=" + filterExpression.render())
                .add("queryId" + queryId)
                .toString();
    }

    /**
     * A Builder for building an immutable FRQuery
     * Defaults: query-all search, return all fields, default sort-keys as determined by IDM, default page size as determined by IDM
     */
    public static final class Builder {
        FRQuery query = new FRQuery();

        public static Builder create() {
            return new Builder();
        }

        public static FRQuery queryAll() {
            return Builder.create().withQueryAll().build();
        }

        public FRQuery build() {
            return new FRQuery(query);
        }

        public Builder withPageSize(int pageSize) {
            this.query.pageSize = pageSize;
            return this;
        }

        public Builder withQueryId(String queryId) {
            this.query.queryId = queryId;
            return this;
        }

        public Builder withQueryAll() {
            return this.withQueryId(QUERY_ALL);
        }

        public Builder withReturnFields(String... fields) {
            query.returnFields = asUniqueList(fields);
            return this;
        }

        public Builder withReturnFields(Collection<String> fields) {
            query.returnFields = asUniqueList(fields);
            return this;
        }

        public Builder withSortKeys(String... keys) {
            query.sortKeys = asUniqueList(keys);
            return this;
        }

        public Builder withSortKeys(Set<String> keys) {
            query.sortKeys = asUniqueList(keys);
            return this;
        }

        public Builder withFilterExpression(FRQueryFilter.Expression expression){
            query.filterExpression = expression;
            return this;
        }
        private List<String> asUniqueList(String... items){
            List<String> list = new ArrayList<>();
            for(String item : items){
                if(!list.contains(item)){
                    list.add(item);
                }
            }
            return Collections.unmodifiableList(list);
        }
        private List<String> asUniqueList(Collection<String> items){
            List<String> list = new ArrayList<>();
            for(String item : items){
                if(!list.contains(item)){
                    list.add(item);
                }
            }
            return Collections.unmodifiableList(list);
        }
    }
}
