package com.hj.config.uuid;

import java.security.SecureRandom;
import java.util.UUID;

public class UUIDUtils {

    private static final SecureRandom random = new SecureRandom();

    /**
     * 生成一个新的UUID字符串
     *
     * @return UUID字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成一个新的UUID字符串，并去除其中的连字符
     *
     * @return 去除连字符的UUID字符串
     */
    public static String generateUUIDWithoutHyphens() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成一个纯数字的唯一标识符
     *
     * @return 纯数字的唯一标识符
     */
    public static String generateNumericUUID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10)); // 生成0-9之间的随机数
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("Generated UUID: " + generateUUID());
        System.out.println("Generated UUID without hyphens: " + generateUUIDWithoutHyphens());
        System.out.println("Generated Numeric UUID: " + generateNumericUUID());
    }
}
