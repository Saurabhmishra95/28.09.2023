package com.experianhealth.ciam.forgerock.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to create a ForgeRock search filter expression for searching any ForgeRock IDM Rest API.
 * The methods in this class can be used in combination with each other to construct complex filters.
 *
 * <pre>
 *     Example Usage:
 *
 *     import static com.experianhealth.ciam.forgerock.model.FRQueryFilter.*;
 *     Expression expression =
 *       not(
 *           or(
 *               and( sw("givenName", "M"), eq("sn", "Petras")),
 *               co("mail", "petras@")
 *             )
 *          )
 *      FRQuery.Builder queryBuilder = FRQuery.Builder.create();
 *      queryBuilder.withFilterExpression(expression);
 *   </pre>
 */
public class FRQueryFilter {


    private static String eqOperator = "eq";
    private static String swOperator = "sw";
    private static String coOperator = "co";
    private static String gtOperator = "gt";
    private static String geOperator = "ge";
    private static String ltOperator = "lt";
    private static String leOperator = "le";
    private static String orOperator = "or";
    private static String andOperator = "and";
    private static String inOperator = "in";
    private static String prOperator = "pr";
    private static char notOperator = '!';
    private static char doubleQuote = '"';
    private static char space = ' ';
    private static char leftParen = '(';
    private static char rightParen = ')';

    private static String leftBracket = "'[";
    private static String rightBracket = "]'";


    public static interface Expression {
        /**
         * Render the expression in the proper filter expression language format
         * @return A string representation of the filter expression
         */
        String render();
    }

    /**
     * Create an or expression
     *
     * example: "(attrName eq "value1") or (attrName eq "value2")"
     *
     * @param leftSide
     * @param rightSide
     * @return
     */
    public static Expression or(Expression leftSide, Expression rightSide){
        return new BinaryExpression(leftSide, orOperator, rightSide);
    }
    /**
     * Create an and expression
     *
     * example: "(attr1Name eq "value1") and (attr2Name eq "value2")"
     *
     * @param leftSide
     * @param rightSide
     * @return
     */
    public static Expression and(Expression leftSide, Expression rightSide){
        return new BinaryExpression(leftSide, andOperator, rightSide);
    }
    /**
     * Create a not expression
     *
     * example: "!(attrName eq "value1")"
     *
     * @param expression
     * @return
     */
    public static Expression not(Expression expression){
        return new NotExpression(expression);
    }

    /**
     * Create a presence expression
     *
     * "attrName pr"
     * @param attrName
     * @return
     */
    public static Expression pr(String attrName){
        return new PresenceExpression(attrName);
    }
    public static Expression eq(String attrName, String value){
        return new StringOperatorExpression(attrName, eqOperator, value);
    }
    public static Expression eq(String attrName, Integer value){
        return new NumericOperatorExpression(attrName, eqOperator, value);
    }
    public static Expression eq(String attrName, Boolean value){
        return new BooleanOperatorExpression(attrName, eqOperator, value);
    }
    public static Expression sw(String attrName, String value){
        return new StringOperatorExpression(attrName, swOperator, value);
    }

    /**
     * Create a contains expression
     *
     * Example: attrName co "substring"
     *
     * @param attrName
     * @param value
     * @return
     */
    public static Expression co(String attrName, String value){
        return new StringOperatorExpression(attrName, coOperator, value);
    }
    public static Expression gt(String attrName, Integer value){
        return new NumericOperatorExpression(attrName, gtOperator, value);
    }
    public static Expression gt(String attrName, String value){
        return new StringOperatorExpression(attrName, gtOperator, value);
    }
    public static Expression ge(String attrName, Integer value){
        return new NumericOperatorExpression(attrName, geOperator, value);
    }
    public static Expression ge(String attrName, String value){
        return new StringOperatorExpression(attrName, geOperator, value);
    }
    public static Expression lt(String attrName, Integer value){
        return new NumericOperatorExpression(attrName, ltOperator, value);
    }
    public static Expression lt(String attrName, String value){
        return new StringOperatorExpression(attrName, ltOperator, value);
    }
    public static Expression le(String attrName, Integer value){
        return new NumericOperatorExpression(attrName, leOperator, value);
    }
    public static Expression le(String attrName, String value){
        return new StringOperatorExpression(attrName, leOperator, value);
    }

    public static Expression in(String attrName, Collection<String> values){
        INExpressionBuilder builder = new INExpressionBuilder(attrName);
        for(String value : values) {
            builder.addValue(value);
        }
        return builder.build();
    }
    public static Expression in(String attrName, String... values){
        INExpressionBuilder builder = new INExpressionBuilder(attrName);
        for(String value : values) {
            builder.addValue(value);
        }
        return builder.build();
    }
    public static Expression in(String attrName, Integer... values){
        INExpressionBuilder builder = new INExpressionBuilder(attrName);
        for(Integer value : values) {
            builder.addValue(value);
        }
        return builder.build();
    }
    public static Expression in(String attrName, Set<Integer> values){
        INExpressionBuilder builder = new INExpressionBuilder(attrName);
        for(Integer value : values) {
            builder.addValue(value);
        }
        return builder.build();
    }



    private record PresenceExpression(String attrName) implements Expression {
        public String render() {
            return new StringBuilder().append(attrName).append(space).append(prOperator).toString();
        }
    }
    private record NotExpression(Expression expression) implements Expression {
        public String render() {
            return new StringBuilder().append(notOperator)
                    .append(leftParen).append(expression.render()).append(rightParen).toString();
        }
    }
    private record BinaryExpression(Expression leftSide, String operator, Expression rightSide) implements Expression {
        @Override
        public String render() {
            StringBuilder sb = new StringBuilder();
            sb.append(leftParen).append(leftSide.render()).append(rightParen)
                    .append(space).append(operator).append(space)
                    .append(leftParen).append(rightSide.render()).append(rightParen);
            return sb.toString();
        }
    }

    private record StringOperatorExpression(String attrName, String operator, String value) implements Expression {

        public String render() {
            StringBuilder sb = new StringBuilder();
            return sb.append(attrName).append(space).append(operator).append(space)
                    .append(doubleQuote).append(value).append(doubleQuote)
                    .toString();
        }
    }
    private record NumericOperatorExpression(String attrName, String operator, Integer value) implements Expression {
        public String render() {
            StringBuilder sb = new StringBuilder();
            return sb.append(attrName).append(space).append(operator).append(space)
                    .append(value)
                    .toString();
        }
    }
    private record BooleanOperatorExpression(String attrName, String operator, Boolean value) implements Expression {
        public String render() {
            StringBuilder sb = new StringBuilder();
            return sb.append(attrName).append(space).append(operator).append(space)
                    .append(value)
                    .toString();
        }
    }

    private record INExpression(String attrName, Set<String> values) implements Expression {

        @Override
        public String render() {
            StringBuilder sb = new StringBuilder();
            sb.append(attrName).append(space).append(inOperator).append(space).append(leftBracket);
            sb.append(String.join(",", values)).append(rightBracket);
            return sb.toString();
        }
    }
    private static class INExpressionBuilder {
        String attrName;
        Set<String> values = new HashSet<String>();
        INExpressionBuilder addValue(String value) {
            values.add(doubleQuote + value + doubleQuote);
            return this;
        }
        INExpressionBuilder addValue(Integer value) {
            values.add(value.toString());
            return this;
        }
        INExpressionBuilder(String attrName){
            this.attrName = attrName;
        }
        Expression build() {
            return new INExpression(attrName, values);
        }
    }
}
