package com.example.library.catalog.domain

import org.springframework.data.repository.CrudRepository

interface CopyRepository : CrudRepository<Copy, CopyId>
