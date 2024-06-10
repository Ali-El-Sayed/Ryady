plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.apollographql.apollo3").version("4.0.0-beta.6")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    kotlin("kapt")
}
apollo {
    service("service") {
        packageName.set("com.example")
    }
}

android {
    namespace = "com.example.ryady"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ryady"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.paymob.sdk:Paymob-SDK:1.1.0")

    //FireBase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-database:20.0.5")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.2.0")


    // Flotation Action Button
    implementation("com.getbase:floatingactionbutton:1.10.1")

    // Apollo
    implementation("com.apollographql.apollo3:apollo-runtime:4.0.0-beta.6")
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Gson
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.firebase.database.ktx)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    // Settings Preference
    implementation("androidx.preference:preference-ktx:1.2.0")
    // Navigation Components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.play.services.location)
    kapt("androidx.room:room-compiler:2.6.1")
    // work manager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    // Encrypted Shared Preferences
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    // Lottie
    implementation("com.airbnb.android:lottie:6.4.0")
    // Coil
    implementation("io.coil-kt:coil:2.4.0")
    // Image Slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")


    // data store
    implementation("androidx.datastore:datastore-preferences:1.1.1")

//shopify payment
    implementation ("com.shopify:checkout-sheet-kit:3.0.1")
    implementation ("com.google.android.material:material:1.12.0")

    // === TESTING ===
    // hamcrest
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    // Junit
    testImplementation("junit:junit:4.13.2")
    // Coroutines test dependencies
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    // Robolectric
    testImplementation("org.robolectric:robolectric:4.12")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}
