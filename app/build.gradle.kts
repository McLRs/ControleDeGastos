plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mcl.controledegastos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mcl.controledegastos"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Habilita o ViewBinding: para cada layout XML, o Android gera uma
    // classe Kotlin (ex.: ActivityListaGastosBinding) com referências
    // diretas às Views, sem precisar de findViewById.
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Extensões Kotlin para APIs do Android (ex.: IntentCompat).
    implementation("androidx.core:core-ktx:1.13.1")
    // Compatibilidade de temas Material e AppCompatActivity em versões antigas do Android.
    implementation("androidx.appcompat:appcompat:1.7.0")
    // Componentes visuais e temas Material Design (Theme.MaterialComponents).
    implementation("com.google.android.material:material:1.12.0")
    // Lista eficiente e rolável (RecyclerView) usada na tela de gastos.
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}
