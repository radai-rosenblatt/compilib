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

import java.util.Map;

/**
 * Created by Radai Rosenblatt
 */
public class BlobLoader extends ClassLoader {
    private final Map<String, ClassBlob> blobs;
    private final Map<String, ClassBlob> surpriseBlobs;

    public BlobLoader(ClassLoader parent, Map<String, ClassBlob> blobs, Map<String, ClassBlob> surpriseBlobs) {
        super(parent);
        this.blobs = blobs;
        this.surpriseBlobs = surpriseBlobs;
    }

    @Override
    protected Class<?> findClass(String fqcn) throws ClassNotFoundException {
        ClassBlob blob = blobs.get(fqcn);
        if (blob == null) {
            blob = surpriseBlobs.get(fqcn);
        }
        if (blob != null) {
            byte[] byteCode = blob.getByteCode();
            return defineClass(fqcn, byteCode, 0, byteCode.length);
        }
        return super.findClass(fqcn);
    }
}