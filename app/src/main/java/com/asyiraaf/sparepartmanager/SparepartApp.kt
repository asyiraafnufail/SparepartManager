package com.asyiraaf.sparepartmanager

import android.app.Application
import com.asyiraaf.sparepartmanager.data.SparepartDatabase
import com.asyiraaf.sparepartmanager.data.SparepartRepository

class SparepartApp : Application() {
    val database by lazy { SparepartDatabase.getDatabase(this) }
    val repository by lazy { SparepartRepository(database.dao()) }
}