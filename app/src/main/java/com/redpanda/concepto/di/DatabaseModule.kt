package com.redpanda.concepto.di

import android.content.Context
import com.redpanda.concepto.domain.interfaces.repository.IHotPointLogRepository
import com.redpanda.concepto.domain.interfaces.repository.IHotPointRepository
import com.redpanda.concepto.infrastructure.local.AppDbContext
import com.redpanda.concepto.infrastructure.local.dao.HotPointDao
import com.redpanda.concepto.infrastructure.local.dao.HotPointLogDao
import com.redpanda.concepto.infrastructure.local.mapper.HotPointLogMapper
import com.redpanda.concepto.infrastructure.local.mapper.HotPointMapper
import com.redpanda.concepto.infrastructure.local.repository.HotPointLogRepository
import com.redpanda.concepto.infrastructure.local.repository.HotPointRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
{
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDbContext
    {
        return AppDbContext.getDatabase(context)
    }

    @Provides
    fun provideHotPointDao(db: AppDbContext) = db.hotPointDao()

    @Provides
    fun provideHotPointLogDao(db: AppDbContext) = db.HotPointLogDao()

    @Provides
    @Singleton
    fun provideHotPointMapper(): HotPointMapper = HotPointMapper()

    @Provides
    @Singleton
    fun provideHotPointRepository(mapper: HotPointMapper, dao: HotPointDao): IHotPointRepository
    {
        return HotPointRepository(mapper, dao)
    }

    @Provides
    @Singleton
    fun provideHotPointLogMapper(): HotPointLogMapper = HotPointLogMapper()

    @Provides
    @Singleton
    fun provideHotPointLogRepository(mapper: HotPointLogMapper, dao: HotPointLogDao): IHotPointLogRepository
    {
        return HotPointLogRepository(mapper, dao)
    }
}