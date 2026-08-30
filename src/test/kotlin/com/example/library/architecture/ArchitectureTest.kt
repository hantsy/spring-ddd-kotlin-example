package com.example.library.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import org.jmolecules.archunit.JMoleculesArchitectureRules
import org.jmolecules.archunit.JMoleculesDddRules
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArchitectureTest {

    private lateinit var classes: JavaClasses

    @BeforeAll
    fun importClasses() {
        classes = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example.library")
    }

    @Test
    fun `ddd building block rules`() {
        JMoleculesDddRules.all().check(classes)
    }

    @Test
    fun `layered architecture rules`() {
        JMoleculesArchitectureRules.ensureLayering().check(classes)
    }
}
