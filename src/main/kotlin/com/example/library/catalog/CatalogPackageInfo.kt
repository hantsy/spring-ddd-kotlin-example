package com.example.library.catalog

import org.springframework.modulith.ApplicationModule
import org.springframework.modulith.PackageInfo

@PackageInfo
@ApplicationModule(allowedDependencies = ["lending :: domain"])
class CatalogPackageInfo
