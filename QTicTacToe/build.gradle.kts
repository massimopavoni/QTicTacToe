plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.+"
}

group = "com.github.massimopavoni.qtictactoe"
version = "1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}


application {
    mainModule.set("com.github.massimopavoni.qtictactoe")
    mainClass.set("com.github.massimopavoni.qtictactoe.UI")
}
