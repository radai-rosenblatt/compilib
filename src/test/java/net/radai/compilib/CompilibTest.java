/*
 * This file is part of compilib
 * Copyright (c) Radai Rosenblatt, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package net.radai.compilib;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Radai Rosenblatt
 */
public class CompilibTest {

    @Test
    public void testSingleClass() throws Exception {
        String source =
                "package a.b;\n" +
                "public interface Consts {\n" +
                "    int A = 10;\n" +
                "}";
        Class<?> clazz = Compilib.compile(source);
        Assert.assertNotNull(clazz);
        Assert.assertEquals(10, ReflectionTestUtils.getField(clazz, "A"));
    }

    @Test
    public void test2Classes() throws Exception {
        Set<String> sources = new HashSet<>();
        sources.add(
                "package a.b;\n" +
                "public interface Consts {\n" +
                "    int A = 10;\n" +
                "}");
        sources.add(
                "package a.c;\n" +
                "import a.b.Consts;\n" +
                "public enum SomeEnum {\n" +
                "    X(0), Y(Consts.A);\n" +
                "    private final int value;\n" +
                "    SomeEnum(int value) {\n" +
                "        this.value = value;\n" +
                "    }\n" +
                "}");
        Map<String, Class<?>> compiledClasses = Compilib.compile(sources);
        Assert.assertEquals(2, compiledClasses.size());
        Class<?> enumClass = compiledClasses.get("a.c.SomeEnum");
        Assert.assertNotNull(enumClass);
        Object y = ReflectionTestUtils.getField(enumClass, "Y");
        Assert.assertNotNull(y);
        Assert.assertEquals(10, ReflectionTestUtils.getField(y, "value"));
    }

    @Test
    public void testInnerClass() throws Exception {
        String source =
                "package a;\n" +
                "public class SomeClass$ {\n" +
                "    public int a() {\n" +
                "        return 1;\n" +
                "    }\n" +
                "    private class SomeInnerClass {\n" +
                "        public class SomeInnerInnerClass {\n" +
                "            public int a() {\n" +
                "                return 4;\n" +
                "            } \n" +
                "        }\n" +
                "        public int a() {\n" +
                "            return 2;\n" +
                "        }\n" +
                "    }\n" +
                "    public static class SomeStaticInnerClass {\n" +
                "        public int a() {\n" +
                "            return 3;\n" +
                "        }\n" +
                "    }\n" +
                "}";
        Map<String, Class<?>> classes = Compilib.compile(Collections.singletonList(source));
        Assert.assertEquals(4, classes.size());
    }

    @Test
    public void testSurpriseInnerClass() throws Exception {
        //see http://stackoverflow.com/questions/17006585/why-does-classname1-class-generate-in-this-situation
        String source =
                "public final class Test {\n" +
                "     static final class TestHolder {\n" +
                "         private static final Test INSTANCE = new Test();\n" +
                "     }     \n" +
                "     private Test() {}\n" +
                "     public static Test getInstance() {\n" +
                "         return TestHolder.INSTANCE;\n" +
                "     }\n" +
                "}";
        Map<String, Class<?>> classes = Compilib.compile(Collections.singletonList(source));
        Assert.assertEquals(3, classes.size());
        Assert.assertTrue(classes.containsKey("Test$1"));
    }
}