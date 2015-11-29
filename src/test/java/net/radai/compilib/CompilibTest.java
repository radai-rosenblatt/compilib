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
}