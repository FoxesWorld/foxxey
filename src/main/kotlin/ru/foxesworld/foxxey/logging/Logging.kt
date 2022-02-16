package ru.foxesworld.foxxey.logging

import mu.KLogger
import kotlin.system.measureTimeMillis

/**
 * @author vie10
 **/

fun KLogger.debugExceptionAndInfoMessage(exception: Throwable, msg: () -> Any) {
    info(msg)
    debug(exception, msg)
}

fun <T> KLogger.logging(
    target: Any,
    verb: String,
    startEnd: String = "ing",
    failureEnd: String = "ing",
    successEnd: String = "ed",
): LoggingBlock<T>.() -> Unit = {
    onStart = {
        info { "${verb.replaceFirstChar { it.uppercase() }}$startEnd $target.." }
    }
    onFailure = {
        debugExceptionAndInfoMessage(it) { "${verb}$failureEnd $target failed." }
    }
    onSuccess = { measuredMillis, _ ->
        info {
            "${
                target.toString().replaceFirstChar { it.uppercase() }
            } ${verb}$successEnd in $measuredMillis millis."
        }
    }
}

suspend fun KLogger.wrappedRunWithoutResult(
    target: Any,
    verb: String,
    startEnd: String = "ing",
    failureEnd: String = "ing",
    successEnd: String = "ed",
    block: suspend () -> Unit
) {
    wrappedRunWithoutResult(
        logging(target, verb, startEnd, failureEnd, successEnd),
        block
    )
}

suspend fun <T> KLogger.wrappedRun(
    target: Any,
    verb: String,
    startEnd: String = "ing",
    failureEnd: String = "ing",
    successEnd: String = "ed",
    block: suspend () -> T
): Result<T> {
    return wrappedRun(
        logging<T>(target, verb, startEnd, failureEnd, successEnd),
        block
    )
}

suspend fun wrappedRunWithoutResult(
    logging: (LoggingBlock<Unit>.() -> Unit)? = null,
    block: suspend () -> Unit
) {
    val loggingBlock = if (logging != null) LoggingBlock<Unit>().apply(logging) else null
    loggingBlock?.onStart?.invoke()
    val result: Result<Unit>
    val measuredMillis = measureTimeMillis {
        result = runCatching {
            block()
        }
    }

    result.onSuccess {
        loggingBlock?.onSuccess?.invoke(measuredMillis, it)
    }.onFailure {
        loggingBlock?.onFailure?.invoke(it)
    }
}

suspend fun <T> wrappedRun(
    logging: (LoggingBlock<T>.() -> Unit)? = null,
    block: suspend () -> T
): Result<T> {
    val loggingBlock = if (logging != null) LoggingBlock<T>().apply(logging) else null
    loggingBlock?.onStart?.invoke()
    val result: Result<T>
    val measuredMillis = measureTimeMillis {
        result = runCatching {
            block()
        }
    }

    result.onSuccess {
        loggingBlock?.onSuccess?.invoke(measuredMillis, it)
    }.onFailure {
        loggingBlock?.onFailure?.invoke(it)
    }

    return result
}

class LoggingBlock<T>(
    var onStart: (() -> Unit)? = null,
    var onSuccess: ((measuredMillis: Long, result: T) -> Unit)? = null,
    var onFailure: ((exception: Throwable) -> Unit)? = null
)