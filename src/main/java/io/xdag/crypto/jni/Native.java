/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2030 The XdagJ Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.xdag.crypto.jni;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.SystemUtils;

import io.xdag.config.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Native {
    private static final String LIB_FILE_PATH = "/native/";

    /** Initializes the native libraries */
    public static void init() {
        String libPath = LIB_FILE_PATH;
        if (SystemUtils.IS_OS_WINDOWS) {
            loadLibrary("/libdfs.dll", libPath + "libdfs.dll");
        } else if (SystemUtils.IS_OS_MAC) {
            loadLibrary("/libdfs.dylib", libPath + "libdfs.dylib");
        } else if (SystemUtils.IS_OS_LINUX) {
            loadLibrary("/libdfs.so", libPath + "libdfs.so");
        }
    }

    /**
     * Loads a library file from bundled resource.
     *
     * @param name
     * @return
     */
    protected static boolean loadLibrary1(String name) {
        try {
            String absolutePath = Native.class.getClassLoader().getResource(name).getPath();
            System.load(absolutePath);
            return true;
        } catch (Exception | UnsatisfiedLinkError e) {
            log.warn("Failed to load native library: {}", name, e);
            return false;
        }
    }

    protected static boolean loadLibrary(String copyName, String name) {
        try {

            String absolutePath = loadlib(Config.root, copyName, name);
            System.load("/" + absolutePath);
            return true;
        } catch (Exception | UnsatisfiedLinkError e) {
            log.warn("Failed to load native library: {}", name, e);
            return false;
        }
    }

    public static String loadlib(String root, String fileName, String sourcePath) throws Exception {
        InputStream in = Native.class.getResourceAsStream(sourcePath);
        byte[] buffer = new byte[1024];
        File temp = new File(root);
        if (!temp.exists()) {
            if (!temp.mkdirs()) {
                log.debug("Create Dir Failed..");
                throw new Exception();
            }
        }
        temp = new File(root + fileName);
        if (!temp.exists()) {
            if (!temp.createNewFile()) {
                log.debug("Create File Failed..");
                throw new Exception();
            }
        }
        FileOutputStream fos = new FileOutputStream(temp);
        int read = -1;
        while ((read = in.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        in.close();
        String abPath = temp.getAbsolutePath();
        return abPath;
    }

    public static String loadlib(String fileName, String sourcePath) throws IOException {
        InputStream in = Native.class.getResourceAsStream(sourcePath);
        byte[] buffer = new byte[1024];
        File temp = new File(fileName);
        FileOutputStream fos = new FileOutputStream(temp);
        int read = -1;
        while ((read = in.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        in.close();
        String abPath = temp.getAbsolutePath();
        return abPath;
    }

    public static native int crypt_start();

    public static native int dnet_crypt_init();

    public static native int load_dnet_keys(byte[] keybytes, int length);

    public static native byte[] dfslib_encrypt_byte_sector(byte[] raw, int length, long sectorNo);

    public static native byte[] dfslib_uncrypt_byte_sector(
            byte[] encrypted, int length, long sectorNo);

    // public static native byte[] dfslib_uncrypt_array();

    /** 这个是矿工之间的解密函数 /* 参数1 加密的数据 参数2 一共有多少个字段 参数3 是第几个发送的 这里的sectorNo 是每一个字段加一次 */
    public static native byte[] dfslib_uncrypt_array(byte[] encrypted, int nfiled, long sectorNo);

    /** 这个矿工的加密函数 */
    public static native byte[] dfslib_encrypt_array(byte[] uncrypted, int nfiled, long sectorNo);

    public static native long get_user_dnet_crypt();

    public static native long get_dnet_keys();

    public static native int set_user_dnet_crypt(String password);

    public static native void set_user_random(String randomKey);

    public static native byte[] make_dnet_keys(int keylen);

    /** encrypt_wallet_key */
    public static native byte[] encrypt_wallet_key(byte[] privkey, int n);

    public static native byte[] uncrypt_wallet_key(byte[] privkey, int n);

    public static native byte[] generate_random_array(byte[] array, long size);

    public static native byte[] generate_random_bytes(byte[] array, long size);

    public static native void dfslib_random_init();

    public static native void crc_init();

    public static native int verify_dnet_key(String password, byte[] data);

    public static native byte[] general_dnet_key(String password, String random);
}
