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

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by Radai Rosenblatt
 */
public class Compilib {
    private final static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    public static Class<?> compile(String source) {
        return compile(new HashSet<>(Collections.singletonList(source))).values().iterator().next();
    }

    public static Map<String, Class<?>> compile(Iterable<String> sources) {
        final List<ClassSource> sourceObjects = new ArrayList<>();
        final Map<String, ClassBlob> blobs = new HashMap<>();
        sources.forEach(code -> {
            JavaType parsedType = Roaster.parse(JavaType.class, code);
            String fqcn = parsedType.getCanonicalName();
            sourceObjects.add(new ClassSource(fqcn, code));
            blobs.put(fqcn, new ClassBlob(fqcn));
        });
        FileManager fm = new FileManager(compiler.getStandardFileManager(null, null, null), blobs);
        StringWriter out = new StringWriter();
        JavaCompiler.CompilationTask task = compiler.getTask(out, fm, null, null, null, sourceObjects);
        if (!task.call()) {
            throw new IllegalStateException("compilation failed: " + out.toString());
        }
        Map<String, Class<?>> results = new HashMap<>(blobs.size());
        for (String fqcn : blobs.keySet()) {
            try {
                results.put(fqcn, fm.getCompiledClass(fqcn));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("class " + fqcn + " not found", e);
            }
        }
        return results;
    }
}