package au.com.test.weather_app.home

import android.content.Context
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.util.Logger
import io.reactivex.disposables.CompositeDisposable
import java.util.regex.Pattern
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
    private val context: Context,
    private val view: MainActivityView,
    private val weatherRepository: WeatherRepository,
    private val logger: Logger
) {
    companion object {
        private val TAG = MainActivityPresenter::class.java.simpleName

        private const val REGEX_COUNTRY_CODE = "[ ,]{1}\\w{2}"
    }

    private val disposables = CompositeDisposable()

    fun fetch(input: String) {
        val countryCode: String? = getCountryCode(input)

        val keyWord = countryCode?.let {
            input.replace(it, "")
        } ?: input

        disposables.add(
            with(weatherRepository) {
                (keyWord.toLongOrNull()?.let { queryWeatherByZipCode(it, countryCode) }
                    ?: queryWeatherByCityName(keyWord, countryCode))
                    .subscribe({
                        logger.d(TAG, "queryWeather by $keyWord, $countryCode: $it")
                        view.onCurrentWeatherUpdate(it)
                    }, {
                        logger.w(TAG, "queryWeather onError: ${it.localizedMessage}")
                    })
            }
        )
    }

    fun fetch(lat: Double, lon: Double) {
        disposables.add(
            weatherRepository.queryWeatherByCoordinate(lat, lon).subscribe({
                logger.d(TAG, "queryWeatherByCoordinate ($lat, $lon) -> weather: $it")
                view.onCurrentWeatherUpdate(it)
            }, {
                logger.w(TAG, "queryWeatherByCoordinate onError: ${it.localizedMessage}")
            })
        )
    }

    private fun getCountryCode(input: String): String? =
        with(Pattern.compile(REGEX_COUNTRY_CODE).matcher(input)) {
            if (find()) {
                group(0)
            } else null
        }

    fun clear() {
        disposables.clear()
    }
}