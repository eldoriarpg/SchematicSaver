plugins {
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("de.chojo.publishdata") version "1.0.4"
    java
    `maven-publish`
}

group = "de.eldoria"
version = "1.0.0"
val shadebase = "de.eldoria." + rootProject.name + ".libs."

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-proxies/")
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.13.2-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.7-SNAPSHOT")
    compileOnly("org.jetbrains", "annotations", "20.1.0")

    implementation("de.eldoria", "eldo-util", "1.12.8-DEV")

    implementation("de.eldoria", "messageblocker", "1.1.0")
    implementation("net.kyori", "adventure-platform-bukkit", "4.0.0")
    implementation("net.kyori", "adventure-text-minimessage", "4.2.0-SNAPSHOT")
    testImplementation("org.jetbrains", "annotations", "21.0.1")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine")
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_16
}

license {
    header(rootProject.file("HEADER.txt"))
    include("**/*.java")
    exclude("**/ClipboardTransformBaker.java")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar {
        //relocate("de.eldoria.eldoutilities", shadebase + "eldoutilities")
        relocate("net.kyori", shadebase + "kyori")
        mergeServiceFiles()
    }

    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "version" to publishData.getVersion(true)
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    register<Copy>("copyToServer") {
        val path = project.property("targetDir") ?: "";
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }
}

publishData {
    hashLength = 7
    useEldoNexusRepos()
    publishTask("jar")
    publishTask("shadowJar")
    publishTask("sourcesJar")
    publishTask("javadocJar")
}

publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            name = "EldoNexus"
            url = uri(publishData.getRepository())
        }
    }
}
