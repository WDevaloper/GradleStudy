package com.github.router.extension

class DefaultConfig {
    String applicationId
    Integer minSdkVersion

    String applicationId(String applicationId) {
        this.applicationId = applicationId
    }

    Integer minSdkVersion(Integer minSdkVersion) {
        this.minSdkVersion = minSdkVersion
    }
}