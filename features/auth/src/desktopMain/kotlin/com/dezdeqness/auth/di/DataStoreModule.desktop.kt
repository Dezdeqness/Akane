package com.dezdeqness.auth.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dezdeqness.auth.data.datastore.getDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun dataStoreModule(): Module = module {
    single<DataStore<Preferences>> { getDataStore() }
}
