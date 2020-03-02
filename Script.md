- EagerExecution
  - no eager excecution in runBlocking
  - eager excecution in runBlockingTest
  - eager execution until delay or yield
- AdvanceTime
- TestingTimeoutInSuspendableFunction
- TestingTimeoutInSeparateCoroutine
  - async
- MainDispatcher
- MainDispatcher using extension
- Providing Dispatcher
-------- wenn noch Zeit ist
- ExceptionHandling
  - global scope erwähnen
  - supervisor scope Beispiel
- PauseDispatcher

## runBlockingTest
- provides TestCoroutineScope
- auto advance until idle
- Housekeeping der Jobs -> Exception

## TestCoroutineDispatcher
- eager (immediate) execution
  - lazy using pauseDispatcher
- Time control

## TestCoroutineScope
- uses TestCoroutineDispatcher and -ExceptionHandler by default
- delegates delay control and uncaught captor

## TestCoroutineExceptionHandler
- captures all exceptions
- rethrows first exception on cleanup
