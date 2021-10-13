object Releases {
    const val versionCode = 2
    const val versionName = "1.0"
}

object AppConfig {
    const val buildToolsVersion = "30.0.3"
    const val appId = "com.fxlibs.ceksubs"
    const val minSdk = 21
    const val targetSdk = 30
    const val compileSdk = 30
}

object Resources {
    const val subsidyApiUrl = "https://pelanggan.pln.co.id"
    const val docUA = "https://cek-subsidi.web.app/subsidy_user_agreement.html"
    const val docDisclaimerStart = "https://cek-subsidi.web.app/subsidy_disclaimer_start.html"
    const val docDisclaimerEnd   = "https://cek-subsidi.web.app/subsidy_disclaimer_end.html"

    const val adsAppId         = "ca-app-pub-3007919778406514~1846261763"
    const val adsDebugOpenApp  = "ca-app-pub-3940256099942544/3419835294"
    const val adsDebugBanner1  = "ca-app-pub-3940256099942544/6300978111"
    const val adsDebugBanner2  = "ca-app-pub-3940256099942544/6300978111"
    const val adsDebugReward   = "ca-app-pub-3940256099942544/5224354917"
    const val adsReleaseOpenApp  = "ca-app-pub-3007919778406514/8775466347"
    const val adsReleaseBanner1  = "ca-app-pub-3007919778406514/2976344750"
    const val adsReleaseBanner2  = "ca-app-pub-3007919778406514/5972035572"
    const val adsReleaseReward   = "ca-app-pub-3007919778406514/1443771239"
}


object Versions {

    const val GRADLE_TOOLS_VERSION = "4.2.1"
    const val DI_KOIN_VERSION = "3.1.2"

    const val KOTLIN_VERSION = "1.6.0"
    const val COROUTINES_VERSION = "1.5.0"

    const val FRAGMENT_VERSION = "1.3.6"
    const val LIFECYCLE_VERSION = "2.3.1"
    const val JETPACK_NAVIGATION_VERSION = "2.3.5"

    const val RETROFIT_VERSION = "2.9.0"
    const val LOGGING_INTERCEPTOR_VERSION = "4.9.0"

    const val ANDROIDX_CORE_VERSION = "1.3.2"
    const val ANDROIDX_APP_COMPAT_VERSION = "1.3.1"
    const val GOOGLE_MATERIAL_VERSION = "1.4.0"
    const val ANDROIDX_CONSTRAINT_LAYOUT_VERSION = "2.0.4"

    const val ANDROIDX_ROOM = "2.3.0"
    const val ADSMOB = "20.4.0"

}

object Libs {

    const val gradleTools = "com.android.tools.build:gradle:${Versions.GRADLE_TOOLS_VERSION}"
    const val adsMob = "com.google.android.gms:play-services-ads:${Versions.ADSMOB}"

    // Kotlin
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN_VERSION}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN_VERSION}"

    // Coroutines
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES_VERSION}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES_VERSION}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES_VERSION}"

    const val androidxCore = "androidx.core:core-ktx:${Versions.ANDROIDX_CORE_VERSION}"
    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.ANDROIDX_APP_COMPAT_VERSION}"
    const val googleMaterial = "com.google.android.material:material:${Versions.GOOGLE_MATERIAL_VERSION}"
    const val androidXConstraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.ANDROIDX_CONSTRAINT_LAYOUT_VERSION}"

    // Retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT_VERSION}"
    const val moshi = "com.squareup.retrofit2:converter-moshi:${Versions.RETROFIT_VERSION}"
    const val interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.LOGGING_INTERCEPTOR_VERSION}"
    //Room
    const val roomRuntime = "androidx.room:room-ktx:${Versions.ANDROIDX_ROOM}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.ANDROIDX_ROOM}"

    // MockWebServer

    // Timber

    // Coil

    // DI Koin
    const val koinAndroidx  = "io.insert-koin:koin-android:${Versions.DI_KOIN_VERSION}"
    const val koinJvmTest = "io.insert-koin:koin-test-junit4:${Versions.DI_KOIN_VERSION}"

    // Fragments
    const val fragments = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT_VERSION}"
    const val viewModels = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE_VERSION}"

    // SwipeRefresh layout

    //Navigation
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.JETPACK_NAVIGATION_VERSION}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.JETPACK_NAVIGATION_VERSION}"
    const val navigationSafeArgsPlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.JETPACK_NAVIGATION_VERSION}"

    // ||
    // TESTING
    // ||

    // Mockk

    // Junit 4
    const val junitExt = "androidx.test.ext:junit:1.1.2"

    // Leak Canary

    // Espresso
    const val espresso = "androidx.test.espresso:espresso-core:3.3.0"

    // Fragments


}