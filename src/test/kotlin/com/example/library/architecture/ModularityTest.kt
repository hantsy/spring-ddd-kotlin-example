package com.example.library.architecture

import com.example.library.LibraryApplication
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ModularityTest {

    @Test
    fun verifyModularStructure() {
        ApplicationModules.of(LibraryApplication::class.java).verify()
    }
}
