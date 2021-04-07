package co.example.openweathermapintegration

import MainWeather
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Ussama Iftikhar on 07-Apr-2021.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */

const val BASE_URL = "http://api.openweathermap.org/"
const val API_KEY = "a2ae7e5a17634c9913f38aa855dbb769"

//api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
//http://api.openweathermap.org/data/2.5/weather?q=new%20york&appid=a2ae7e5a17634c9913f38aa855dbb769
public interface IWeather {
    @GET("data/2.5/weather?appid=${API_KEY}&units=imperial")
    fun getWeather(@Query("lon") lon: Double, @Query("lat") lat: Double): Call<MainWeather>
}

object WeatherService {
    val weatherInstance: IWeather

    init{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherInstance = retrofit.create(IWeather::class.java)
    }

}
