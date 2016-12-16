package com.eden.orchid.compiler.impl;

import com.eden.orchid.AutoRegister;
import com.eden.orchid.compiler.AssetCompiler;

@AutoRegister
public class CompilePosts implements AssetCompiler {

    @Override
    public String getKey() {
        return "posts";
    }

    @Override
    public String[] getSourceExtensions() {
        return new String[]{"md", "txt"};
    }

    @Override
    public String getDestExtension() {
        return "html";
    }

    @Override
    public String getSourceDir() {
        return "assets/posts";
    }

    @Override
    public String getDestDir() {
        return "posts";
    }

    @Override
    public String compile(String extension, String input) {
        return input;
    }
}
