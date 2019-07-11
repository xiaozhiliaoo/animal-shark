package org.lili.forfun.shark.testcase.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author lili
 * @description 期望结果和实际结果校验
 * @since
 **/
public class Validator extends AbstractValidator<String, List<String>> {

    @Override
    public void checkResult(String actual, List<String> expectedList) {
        if (StringUtils.isEmpty(actual)) {
            fail("actual result is null");
        }
        for (String expected : expectedList) {
            assertTrue(actual.contains(expected), "expected result is not find in the actual result"
                    + "\nexpected result is 【 " + expectedList + " 】\nactual result is " + actual + "\n");
        }
    }
}

