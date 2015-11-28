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

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Radai Rosenblatt
 */
public class Compilib {
    private final static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    public static Map<String, Class<?>> compile(Map<String, String> fqcnToSource) {
        final Map<String, ClassSource> sources = new HashMap<>();
        final Map<String, ClassBlob> blobs = new HashMap<>();
        fqcnToSource.forEach((fqcn, code) -> {
            sources.put(fqcn, new ClassSource(fqcn, code));
            blobs.put(fqcn, new ClassBlob(fqcn));
        });
        FileManager fm = new FileManager(compiler.getStandardFileManager(null, null, null), blobs);
        StringWriter out = new StringWriter();
        JavaCompiler.CompilationTask task = compiler.getTask(out, fm, null, null, null, sources.values());
        if (!task.call()) {
            throw new IllegalStateException("compilation failed: " + out.toString());
        }
        Map<String, Class<?>> results = new HashMap<>(fqcnToSource.size());
        for (String fqcn : fqcnToSource.keySet()) {
            try {
                results.put(fqcn, fm.getCompiledClass(fqcn));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("class " + fqcn + " not found", e);
            }
        }
        return results;
    }
}