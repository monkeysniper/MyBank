package com.example.mybank.di

// NetworkModule предоставляет зависимости для работы с сетью
// 1. HttpLoggingInterceptor — для логирования HTTP-запросов
// 2. OkHttpClient — клиент с логгером
// 3. Retrofit — библиотека для работы с REST API
// 4. AccountApi — интерфейс API, реализуемый через Retrofit

import com.example.mybank.data.api.AccountApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    //  HttpLoggingInterceptor — перехватчик, логирующий HTTP-запросы и ответы.
    // Уровень логирования установлен на BODY, чтобы видеть полный запрос/ответ.
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    //  OkHttpClient с добавленным перехватчиком логов
    // Этот клиент будет использоваться Retrofit для сетевых запросов
    @Provides
    fun provideHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    //  экземпляр Retrofit.
    // Устанавливается базовый URL, клиент и конвертер JSON
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://681e0ef2c1c291fa6632dc4f.mockapi.io/api/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // это позволяет вызывать HTTP-запросы к API через методы интерфейса
    @Provides
    fun provideAccountApi(retrofit: Retrofit): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }
}