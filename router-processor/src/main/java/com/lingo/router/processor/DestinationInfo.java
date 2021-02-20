package com.lingo.router.processor;

public final class DestinationInfo {
    private final String classPath;
    private final String url;
    private final String description;

    public DestinationInfo(String classPath, String url, String description) {
        this.classPath = classPath;
        this.url = url;
        this.description = description;
    }

    public String getClassPath() {
        return classPath;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }
}
