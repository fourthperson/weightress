package iak.wrc

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import iak.wrc.data.repository.WeightRepoImpl
import iak.wrc.data.source.LocalDataSource
import iak.wrc.data.source.LocalDataSourceImpl
import iak.wrc.data.source.db.PastWeightDao
import iak.wrc.data.source.db.WeightressDb
import iak.wrc.domain.repository.WeightRepo
import iak.wrc.domain.use_case.GetAllWeightsUseCase
import iak.wrc.domain.use_case.RecordWeightUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun db(app: Application): WeightressDb {
        return WeightressDb.getInstance(app)
    }

    @Provides
    @Singleton
    fun pastWeightDao(db: WeightressDb): PastWeightDao {
        return db.pasWeightDao()
    }

    @Provides
    @Singleton
    fun localDataSource(pastWeightDao: PastWeightDao): LocalDataSource {
        return LocalDataSourceImpl(pastWeightDao)
    }

    @Provides
    @Singleton
    fun weightRepo(localDataSource: LocalDataSource): WeightRepo {
        return WeightRepoImpl(localDataSource)
    }

    @Provides
    @Singleton
    fun getWeightsUseCase(weightRepo: WeightRepo): GetAllWeightsUseCase {
        return GetAllWeightsUseCase(weightRepo)
    }

    @Provides
    @Singleton
    fun getRecordWeightUseCase(weightRepo: WeightRepo): RecordWeightUseCase {
        return RecordWeightUseCase(weightRepo)
    }
}