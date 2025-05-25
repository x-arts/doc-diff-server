package com.did.docdiffserver.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.compent.exception.BusinessException;
import com.did.docdiffserver.compent.exception.ErrorCode;


import java.rmi.ServerException;
import java.util.Collection;
import java.util.Objects;

/**
 * 断言工具类是扩充
 */
public class AssertUtil  {
    public static void strNotBlank(String str, String message)  {
        if (StrUtil.isBlank(str)) {
            throwException(message);
        }
    }

    public static void notNull(Object o, String message) {
        AssertUtil.isTrue(Objects.nonNull(o), message);
    }


    public static void isNotEmpty(Collection<?> collection, String message) {
        AssertUtil.isTrue(CollectionUtil.isNotEmpty(collection), message);
    }

    public static void isTrue(boolean expression,  String message)  {
        if (!expression) {
            throwException(message);
        }

    }

    private static void throwException(String message) {
        throw new BusinessException(ErrorCode.ILLEGAL_NOT_ASSERT.code, message);
    }




}
