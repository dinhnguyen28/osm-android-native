package com.example.landmarkremark.di

import com.example.landmarkremark.database.repository.NoteRepository
import com.example.landmarkremark.database.repository.NoteRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun providerNoteRepository(
        database: FirebaseFirestore
    ): NoteRepository {
        return NoteRepositoryImpl(database)
    }

}