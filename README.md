# Testing Kotlin Coroutines
This repository provides examples that let you explore the capabilities
of the [kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test) 
module. 

## Building Blocks
The [kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test) 
module consists of four ingredients: The [TestCoroutineDispatcher](#TestCoroutineDispatcher),
the [TestCoroutineExceptionHandler](#TestCoroutineExceptionHandler), 
a [TestCoroutineScope](#TestCoroutineScope) and finally the
[runBlockingTest](#runBlockingTest) function. 
Let's take them step by step.

### TestCoroutineDispatcher
In contrast to other dispatchers, this one executes new coroutines eagerly,
which means they are run immediately like they where started in mode 
`UNDISPATCHED` (TODO link). This eases the handling in tests a bit. But
you can change this behaviour, either by providing a dedicated start mode,
or by pausing the dispatchers (we will come to that later on).

Another important point, is that it is up to a dispatcher to implement
the concept of time on which scheduling and delay is based on. The 
`TestCoroutineDispatcher` (TODO link) implements a virtual time and gives
you fine grained control on it. This allows to write robust time based tests.

### TestCoroutineExceptionHandler
A `CoroutineContext` may contain a `CoroutineExceptionHandler` which is
comparable to the uncaught exception handler on threads, and is intended
to handle all exceptions that arise in a coroutine. This implementations
captures and collects all exceptions, so they can be inspected in tests,
and rethrows the first one on cleanup. 

### TestCoroutineScope
This scope provides a `TestCoroutineDispatcher` and `TestCoroutineExceptionHandler`
by default if none is already given in the context.  
It also provides access to the the time controlling functions like 
`advanceTime...` and the `uncaughtExceptions` by delegating them to the 
`TestCoroutineDispatcher` resp. `TestCoroutineExceptionHandler`.

### runBlockingTest
This variant of `runBlocking()` ties everything up and provides you
a `TestCoroutineScope` and therefore a `TestCoroutineDispatcher` and 
`TestCoroutineExceptionHandler`. It advances time of the test dispatcher
unitl idle, which effectively makes sure that everything in the test block
is run. It also checks for misusage by counting the active jobs before 
and after the test.

# Examples
The examples are implemented as unit tests, where each test class 
demonstrates certain features:

## [EagerExecution](src/test/kotlin/EagerExecution.kt)
This test class shows that `runBlockingTest()` actually executes launched 
coroutines eagerly, which is effectively like starting it in mode `UNDISPATCHED`.

## [AdvanceTime](src/test/kotlin/AdvanceTime.kt)
The `TestCoroutineDispatcher` implements a virtual time. This test class
demonstrates how to use `advanceTimeBy()` and the lot to control situations
based on timing.

## [TestingTimeoutInSuspendableFunction](src/test/kotlin/TestingTimeoutInSuspendableFunction.kt)
Due to the time control provided by the `TestCoroutineDispatcher` it is 
quite easy to test timeouts.

## [TestingTimeoutInSeparateCoroutine](src/test/kotlin/TestingTimeoutInSeparateCoroutine.kt)
In this slight variation of the former test class, some examples are provided
for testing timeout in new coroutines started via launch or async. 

## [MainDispatcher](src/test/kotlin/MainDispatcher.kt)
UI code like e.g. Android, Swing, JavaFX is executed by a dedicated UI-Thread.
When using coroutines you have to use the `Main dispatcher` for that.
In order to use the Test-Dispatcher the coroutines test package provides a
special function `Dispatchers.setMain()` which usage is shown in this test class.

## [MainDispatcherUsingExtension](src/test/kotlin/MainDispatcherUsingExtension.kt)
For an introduction on using the main dispatcher have a look at
[MainDispatcher](#MainDispatcher). This class here shows usage of a (custom)
[JUnit 5 extension](https://junit.org/junit5/docs/current/user-guide/#extensions)
providing and maintaining a test main dispatcher for you.

## [ProvidingDispatchers](src/test/kotlin/ProvidingDispatchers.kt)
As an alternative to using the `Dispatchers` directly, you may use a 
dispatcher provider interface which abstracts the concrete implementation, 
so you may use a `TestCoroutineDispatcher` as a replacement. These examples 
use a [neat lil library](https://github.com/RBusarow/Dispatch) which 
implements all this in an easy to use manner.

## [DetectUnrelatedJobs](src/test/kotlin/DetectUnrelatedJobs.kt)
The function `runBlockingTest()` counts active jobs before and after 
execution of the test block, and raises an exception in case of a mismatch. 
The problem is usually based in an unintential use of a non-test dispatcher.

## [ExceptionHandling](src/test/kotlin/ExceptionHandling.kt)
Exceptions are not handled by default in a coroutine. The runBlockingTest()`
function provides a `TestCoroutineExceptionHandler` which allows you to 
test exceptional situations.

## [PauseDispatcher](src/test/kotlin/PauseDispatcher.kt)
The `DelayController` interface implemented by the `TestCoroutineDispatcher`
also provides for pausing and resuming the dispatcher. These examples show
you how this switches the eager execution into a lazy one under your control.

## [TestCoroutineScopeWithoutRunBlocking](src/test/kotlin/TestCoroutineScopeWithoutRunBlocking.kt)
Most of the examples provided here use [runBlockingTest] in order to
benefit of all test functionality. But you may also use every building
block on its own, like e.g. here the `TestCoroutineScope`.

# Utitlity Functions
The coroutines package provides some utility functions used by various tests
* [TestCoroutinesExt](src/test/kotlin/coroutines/TestCoroutinesExt.kt) provides
some extension functions that provide access to objects in the coroutine context
like e.g. the `testDispatcher`

* [Asserts](src/test/kotlin/coroutines/Asserts.kt): Suspendable functions are 
[colored functions](https://medium.com/@elizarov/how-do-you-color-your-functions-a6bb423d936d).
This class provides some suspendable _variants_ of existing functions like
e.g. `coAssertThrows()`

* [SilentTestCoroutineExceptionHandler](src/test/kotlin/coroutines/SilentTestCoroutineExceptionHandler.kt)
A variant of the original `TestCoroutineExceptionHandler` that also captures all
exceptions, but do not rethrow them.

* [MainDispatcherExtension](src/test/kotlin/coroutines/MainDispatcherExtension.kt)
This is a [JUnit 5 extension](https://junit.org/junit5/docs/current/user-guide/#extensions)
that creates a `TestCoroutineDispatcher` for each test method, sets it as the 
`Main dispatcher` and resets it after the test. The dispatcher can be resolved 
as a [parameter](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dependency-injection) 
in the test.


# Used Libraries
* For mocking the [MockK](https://mockk.io/) library is used. Besides its
support for multiplatform development, it does a great job dealing with
coroutines and provides lots of other features. If you do not use it yet, 
give it a try.

* The [ProvidingDispatchers](#ProvidingDispatchers) example uses this
[Dispatcher Provider](https://github.com/RBusarow/Dispatch) library. The
`DispatcherProvider` is passed implicitly in the coroutine context, which lets
you easily migrate from direct usage of e.g. `Dispatchers.Main` without 
the need to change any signatures for injection. 

