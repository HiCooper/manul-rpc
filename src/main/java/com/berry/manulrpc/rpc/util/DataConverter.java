package com.berry.manulrpc.rpc.util;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Array;
import java.util.*;

public class DataConverter {

    /**
     * 数据类型转换
     *
     * @param msg        输入数据
     * @param returnType 目标类型
     * @return 转换后的对象
     */
    public static Object parseResultToTargetReturnType(Object msg, Class<?> returnType) {
        if (msg == null) {
            return null;
        }

        // 如果msg已经是目标类型，直接返回
        if (returnType.isInstance(msg)) {
            return msg;
        }

        // 处理基本数据类型及其包装类
        if (returnType.isPrimitive() || isWrapperType(returnType)) {
            return convertToPrimitiveType(msg, returnType);
        }

        // 处理字符串类型
        if (returnType == String.class) {
            return msg.toString();
        }

        // 处理集合类型
        if (Collection.class.isAssignableFrom(returnType)) {
            return convertToCollection(msg, returnType);
        }

        // 处理数组类型
        if (returnType.isArray()) {
            return convertToArray(msg, returnType.getComponentType());
        }

        // 当做自定义对象类型处理
        return JSON.parseObject((String) msg, returnType);
    }

    private static boolean isWrapperType(Class<?> clazz) {
        return clazz == Boolean.class || clazz == Byte.class || clazz == Character.class ||
                clazz == Double.class || clazz == Float.class || clazz == Integer.class ||
                clazz == Long.class || clazz == Short.class;
    }

    private static Object convertToPrimitiveType(Object msg, Class<?> returnType) {
        if (returnType == int.class || returnType == Integer.class) {
            return Integer.parseInt(msg.toString());
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return Boolean.parseBoolean(msg.toString());
        } else if (returnType == byte.class || returnType == Byte.class) {
            return Byte.parseByte(msg.toString());
        } else if (returnType == char.class || returnType == Character.class) {
            return msg.toString().charAt(0);
        } else if (returnType == double.class || returnType == Double.class) {
            return Double.parseDouble(msg.toString());
        } else if (returnType == float.class || returnType == Float.class) {
            return Float.parseFloat(msg.toString());
        } else if (returnType == long.class || returnType == Long.class) {
            return Long.parseLong(msg.toString());
        } else if (returnType == short.class || returnType == Short.class) {
            return Short.parseShort(msg.toString());
        } else {
            throw new IllegalArgumentException("Unsupported primitive type: " + returnType.getName());
        }
    }

    private static Object convertToCollection(Object msg, Class<?> returnType) {
        if (msg instanceof Collection) {
            return msg;
        }

        // 假设msg是一个逗号分隔的字符串
        String[] elements = msg.toString().split(",");
        Collection<Object> collection;
        if (returnType == List.class) {
            collection = new ArrayList<>();
        } else if (returnType == Set.class) {
            collection = new HashSet<>();
        } else {
            throw new IllegalArgumentException("Unsupported collection type: " + returnType.getName());
        }

        collection.addAll(Arrays.asList(elements));
        return collection;
    }

    private static Object convertToArray(Object msg, Class<?> componentType) {
        if (msg instanceof Collection) {
            Collection<?> collection = (Collection<?>) msg;
            Object array = Array.newInstance(componentType, collection.size());
            int i = 0;
            for (Object element : collection) {
                Array.set(array, i++, element);
            }
            return array;
        }

        // 假设msg是一个逗号分隔的字符串
        String[] elements = msg.toString().split(",");
        Object array = Array.newInstance(componentType, elements.length);
        for (int i = 0; i < elements.length; i++) {
            Array.set(array, i, convertToPrimitiveType(elements[i], componentType));
        }
        return array;
    }

}
