package au.com.test.weather_app.data.domain.mappers

import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.source.remote.owm.models.WeatherResponse

object WeatherMapper {
    fun mapToDomainEntities(response: WeatherResponse): WeatherData? =
        if (response.isSuccess()) {
            WeatherData(
                id = 0,
                cityId = if (response.id > 0) response.id else null,
                cityName = if (response.name.isNotBlank()) response.name else null,
                countryCode = response.sys.country,
                zipCode = null,
                latitude = response.coord.lat,
                longitude = response.coord.lon,
                weatherConditionId = response.weather[0].id,
                weather = response.weather[0].main,
                weatherDescription = response.weather[0].description,
                weatherIcon = response.weather[0].icon,
                temperature = response.main.temp,
                temperatureMin = response.main.temp_min,
                temperatureMax = response.main.temp_max,
                humidity = response.main.humidity,
                windSpeed = response.wind.speed,
                windDegree = response.wind.deg,
                lastUpdate = System.currentTimeMillis()
            )
        } else {
            null
        }
}