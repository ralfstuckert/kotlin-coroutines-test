@UseExperimental(ExperimentalCoroutinesApi::class)

build.gradle:
compileTestKotlin {
    kotlinOptions {
        freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
    }
}



/**
 * See [issue 1609](https://github.com/Kotlin/kotlinx.coroutines/issues/1609)
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
val TestCoroutineScope.testDispatcher
    get() = coroutineContext[ContinuationInterceptor] as TestCoroutineDispatcher


    testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-debug:$coroutines_version"
