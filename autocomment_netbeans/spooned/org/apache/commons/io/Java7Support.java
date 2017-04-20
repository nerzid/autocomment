/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.commons.io;

import java.lang.reflect.Array;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Java7 feature detection and reflection based feature access.
 * <p/>
 * Taken from maven-shared-utils, only for private usage until we go full java7
 */
class Java7Support {
    private static final boolean IS_JAVA7;

    private static Method isSymbolicLink;

    private static Method delete;

    private static Method toPath;

    private static Method exists;

    private static Method toFile;

    private static Method readSymlink;

    private static Method createSymlink;

    private static Object emptyLinkOpts;

    private static Object emptyFileAttributes;

    static {
        boolean isJava7x = true;
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> files = cl.loadClass("java.nio.file.Files");
            Class<?> path = cl.loadClass("java.nio.file.Path");
            Class<?> fa = cl.loadClass("java.nio.file.attribute.FileAttribute");
            Class<?> linkOption = cl.loadClass("java.nio.file.LinkOption");
            Java7Support.isSymbolicLink = files.getMethod("isSymbolicLink", path);
            Java7Support.delete = files.getMethod("delete", path);
            Java7Support.readSymlink = files.getMethod("readSymbolicLink", path);
            Java7Support.emptyFileAttributes = Array.newInstance(fa, 0);
            Java7Support.createSymlink = files.getMethod("createSymbolicLink", path, path, Java7Support.emptyFileAttributes.getClass());
            Java7Support.emptyLinkOpts = Array.newInstance(linkOption, 0);
            Java7Support.exists = files.getMethod("exists", path, Java7Support.emptyLinkOpts.getClass());
            Java7Support.toPath = File.class.getMethod("toPath");
            Java7Support.toFile = path.getMethod("toFile");
        } catch (ClassNotFoundException e) {
            isJava7x = false;
        } catch (NoSuchMethodException e) {
            isJava7x = false;
        }
        IS_JAVA7 = isJava7x;
    }

    /**
     * Invokes java7 isSymbolicLink
     * @param file The file to check
     * @return true if the file is a symbolic link
     */
    public static boolean isSymLink(File file) {
        try {
            Object path = Java7Support.toPath.invoke(file);
            Boolean result = ((Boolean) (Java7Support.isSymbolicLink.invoke(null, path)));
            return result.booleanValue();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the target of a symbolic link
     * @param symlink The symlink to read
     * @return The location the symlink is pointing to
     * @throws IOException Upon failure
     */
    public static File readSymbolicLink(File symlink) throws IOException {
        try {
            Object path = Java7Support.toPath.invoke(symlink);
            Object resultPath = Java7Support.readSymlink.invoke(null, path);
            return ((File) (Java7Support.toFile.invoke(resultPath)));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Indicates if a symlunk target exists
     * @param file The symlink file
     * @return true if the target exists
     * @throws IOException upon error
     */
    private static boolean exists(File file) throws IOException {
        try {
            Object path = Java7Support.toPath.invoke(file);
            final Boolean result = ((Boolean) (Java7Support.exists.invoke(null, path, Java7Support.emptyLinkOpts)));
            return result.booleanValue();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw ((RuntimeException) (e.getTargetException()));
        }
    }

    /**
     * Creates a symbolic link
     * @param symlink The symlink to create
     * @param target Where it should point
     * @return The newly created symlink
     * @throws IOException upon error
     */
    public static File createSymbolicLink(File symlink, File target) throws IOException {
        try {
            if (!(Java7Support.exists(symlink))) {
                Object link = Java7Support.toPath.invoke(symlink);
                Object path = Java7Support.createSymlink.invoke(null, link, Java7Support.toPath.invoke(target), Java7Support.emptyFileAttributes);
                return ((File) (Java7Support.toFile.invoke(path)));
            }
            return symlink;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            final Throwable targetException = e.getTargetException();
            throw ((IOException) (targetException));
        }
    }

    /**
     * Performs a nio delete
     *
     * @param file the file to delete
     * @throws IOException Upon error
     */
    public static void delete(File file) throws IOException {
        try {
            Object path = Java7Support.toPath.invoke(file);
            Java7Support.delete.invoke(null, path);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw ((IOException) (e.getTargetException()));
        }
    }

    /**
     * Indicates if the current vm has java7 lubrary support
     * @return true if java7 library support
     */
    public static boolean isAtLeastJava7() {
        return Java7Support.IS_JAVA7;
    }
}

