package org.lili.forfun.shark.testcase.validator;

/**
 * @author lili
 * @description 校验器：校验期望结果和实际结果是否相同
 * @since
 **/
abstract class AbstractValidator<A,E> {

    /**
     * @param actual   实际结果
     * @param expected 期望结果
     */
    public abstract void checkResult(A actual, E expected);
}
