import com.codahale.metrics.health.HealthCheck
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import config.MainConfiguration
import io.dropwizard.Application
import io.dropwizard.jackson.Jackson
import io.dropwizard.jetty.HttpConnectorFactory
import io.dropwizard.jetty.HttpsConnectorFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.github.classgraph.ClassGraph
import java.lang.reflect.Constructor


class Main : Application<MainConfiguration>() {
    override fun initialize(bootstrap: Bootstrap<MainConfiguration>) {
        bootstrap.objectMapper = createCustomObjectMapper()
    }

    override fun run(configuration: MainConfiguration, environment: Environment) {
        environment.objectMapper.apply {
            registerModule(KotlinModule.Builder().build())
            registerModule(JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }

        registerToHttpConnection(environment)
        registerServices()
        registerControllers(environment)
        healthCheck(environment)
    }

    private fun registerControllers(environment: Environment) {
        val controllers = findAndInstantiateControllers()
        controllers.forEach { controller ->
            println("Registering controller: ${controller::class.simpleName}")
            environment.jersey().register(controller)
        }
    }


    //Finds and instantiates all controllers dynamically.
    private fun findAndInstantiateControllers(): List<Any> {
        val controllers = mutableListOf<Any>()

        // Scan the "Controller" package to find all classes ending with "Controller"
        ClassGraph().enableClassInfo().whitelistPackages("controllers").scan().use { scanResult ->
            scanResult.allClasses
                .filter { it.name.endsWith("Controller") }
                .forEach { classInfo ->
                    val clazz = Class.forName(classInfo.name)
                    val instance = instantiateWithDependencies(clazz)
                    instance?.let { controllers.add(it) }
                }
        }

        return controllers
    }


    // Instantiates a class dynamically by resolving its dependencies.
    private fun instantiateWithDependencies(clazz: Class<*>): Any? {
        return try {
            val constructor: Constructor<*> = clazz.constructors.firstOrNull() ?: return null
            val dependencies = resolveDependencies(constructor.parameterTypes)
            constructor.newInstance(*dependencies)
        } catch (e: Exception) {
            println("Failed to instantiate ${clazz.name}: ${e.message}")
            null
        }
    }


    // Resolves dependencies dynamically using reflection.
    private fun resolveDependencies(paramTypes: Array<Class<*>>): Array<Any> {
        return paramTypes.mapNotNull { paramType ->
            serviceRegistry[paramType] ?: instantiateWithDependencies(paramType)
        }.toTypedArray()
    }

    private val serviceRegistry: MutableMap<Class<*>, Any> = mutableMapOf()

    // Discovers all service classes and registers them automatically.
    private fun registerServices() {
        ClassGraph().enableClassInfo().whitelistPackages("services").scan().use { scanResult ->
            scanResult.allClasses
                .filter { it.name.endsWith("Service") }
                .forEach { classInfo ->
                    val clazz = Class.forName(classInfo.name)
                    val instance = instantiateWithDependencies(clazz)
                    if (instance != null) {
                        serviceRegistry[clazz] = instance
                        println("Registered service: ${clazz.simpleName}")
                    }
                }
        }
    }

    private fun createCustomObjectMapper(): ObjectMapper {
        return Jackson.newMinimalObjectMapper()
            .registerModule(KotlinModule.Builder().build())
            .registerModule(JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    private fun registerToHttpConnection(environment: Environment) {
        environment.objectMapper.registerSubtypes(
            HttpConnectorFactory::class.java,
            HttpsConnectorFactory::class.java
        )
    }

    private fun healthCheck(environment: Environment) {
        environment.healthChecks().register("basic", object : HealthCheck() {
            override fun check(): Result {
                return Result.healthy("Application is healthy!")
            }
        })
    }
}

fun main(args: Array<String>) {
    Main().run(*args)
}
