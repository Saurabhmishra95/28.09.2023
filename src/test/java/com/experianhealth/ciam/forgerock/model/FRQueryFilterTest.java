package com.experianhealth.ciam.forgerock.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.experianhealth.ciam.forgerock.model.FRQueryFilter.*;

public class FRQueryFilterTest {

    @Test
    public void testInExpressionMultipleValues() {
        FRQueryFilter.Expression expression = FRQueryFilter.in("attrName", "value1", "value2");
        String actualRender = expression.render();
        String expected = "attrName in '[\"value1\",\"value2\"]'";
        assertEquals(expected, actualRender);
    }
    @Test
    public void testInExpressionSingleValue() {
        Expression expression = in("attrName", "value1");
        String actualRender = expression.render();
        String expected = "attrName in '[\"value1\"]'";
        assertEquals(expected, actualRender);
    }
    @Test
    public void testStringEqualsExpression() {
        FRQueryFilter.Expression expression = FRQueryFilter.eq("attrName", "value1");
        String actualRender = expression.render();
        String expected = "attrName eq \"value1\"";
        assertEquals(expected, actualRender);
    }
    @Test
    public void testNumericEqualsExpression() {
        FRQueryFilter.Expression expression = FRQueryFilter.eq("attrName", 5);
        String actualRender = expression.render();
        String expected = "attrName eq 5";
        assertEquals(expected, actualRender);
    }
    @Test
    public void testBooleanEqualsTrueExpression() {
        FRQueryFilter.Expression expression = FRQueryFilter.eq("attrName", true);
        String actualRender = expression.render();
        String expected = "attrName eq true";
        assertEquals(expected, actualRender);
    }
    @Test
    public void testBooleanEqualsFalseExpression() {
        FRQueryFilter.Expression expression = FRQueryFilter.eq("attrName", false);
        String actualRender = expression.render();
        String expected = "attrName eq false";
        assertEquals(expected, actualRender);
    }

    @Test
    public void testStringGreaterThanExpression() {
        FRQueryFilter.Expression expression = FRQueryFilter.gt("attrName", "value1");
        String actualRender = expression.render();
        String expected = "attrName gt \"value1\"";
        assertEquals(expected, actualRender);
    }
    @Test
    public void testNumericGreaterThanExpression() {
        FRQueryFilter.Expression expression = FRQueryFilter.gt("attrName", 5);
        String actualRender = expression.render();
        String expected = "attrName gt 5";
        assertEquals(expected, actualRender);
    }

    @Test
    public void testStringPresenceExpression() {
        FRQueryFilter.Expression expression = FRQueryFilter.pr("attrName");
        String actualRender = expression.render();
        String expected = "attrName pr";
        assertEquals(expected, actualRender);
    }

    @Test
    public void testOrExpression() {
        Expression leftSide = eq("leftAttrName", "leftValue");
        Expression rightSide = eq("rightAttrName", "rightValue");
        Expression orExpression = or(leftSide, rightSide);
        String expected = "(leftAttrName eq \"leftValue\") or (rightAttrName eq \"rightValue\")";
        String actual = orExpression.render();
        assertEquals(expected, actual);
    }
    @Test
    public void testNotExpression() {
        Expression rightSide = eq("rightAttrName", "rightValue");
        Expression notExpression = not(rightSide);
        String expected = "!(rightAttrName eq \"rightValue\")";
        String actual = notExpression.render();
        assertEquals(expected, actual);
    }

    @Test
    public void testComplexExpression() {
        String actual =
            not(
                or(
                    and( sw("givenName", "M"), eq("sn", "Petras")),
                    co("mail", "petras@")
                )
            ).render();

        String expected = "!(((givenName sw \"M\") and (sn eq \"Petras\")) or (mail co \"petras@\"))";
        assertEquals(expected, actual);
    }

}
