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

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Radai Rosenblatt
 */
public class FileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Map<String, ClassBlob> blobs;
    private final BlobLoader blobLoader;

    public FileManager(JavaFileManager fileManager, Map<String, ClassBlob> blobs) {
        super(fileManager);
        this.blobs = blobs;
        this.blobLoader = new BlobLoader(Thread.currentThread().getContextClassLoader(), blobs);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        ClassBlob blob = blobs.get(className);
        if (blob != null) {
            return blob;
        }
        throw new IllegalStateException("request unknown class " + className + " for output. known classes: " + blobs.keySet());
    }

    public Class<?> getCompiledClass(String fqcn) throws ClassNotFoundException {
        return blobLoader.loadClass(fqcn);
    }
}