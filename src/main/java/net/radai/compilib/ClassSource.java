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

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * Created by Radai Rosenblatt
 */
public class ClassSource extends SimpleJavaFileObject {
    private final String fqcn;
    private final String code;

    public ClassSource(String fqcn, String code) {
        super(from(fqcn), Kind.SOURCE);
        this.fqcn = fqcn;
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }

    private static URI from(String fqcn) {
        return URI.create(fqcn.replaceAll("\\.", "/") + ".java");
    }
}