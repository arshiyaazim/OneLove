package com.kilagee.onelove.domain.util

/**
 * A generic class that holds a value or an error status.
 * 
 * @param T The type of the value.
 * @property data The value if the result is successful.
 * @property message The error message if the result is not successful.
 * @property error The error type.
 */
sealed class Result<out T> {
    /**
     * Success result with data
     */
    data class Success<out T>(val data: T) : Result<T>()
    
    /**
     * Error result with message
     */
    data class Error(
        val message: String,
        val error: Exception? = null
    ) : Result<Nothing>()
    
    /**
     * Loading state
     */
    object Loading : Result<Nothing>()
}

/**
 * Returns true if this result is [Result.Success].
 */
val <T> Result<T>.isSuccess: Boolean
    get() = this is Result.Success

/**
 * Returns true if this result is [Result.Error].
 */
val <T> Result<T>.isError: Boolean
    get() = this is Result.Error

/**
 * Returns true if this result is [Result.Loading].
 */
val <T> Result<T>.isLoading: Boolean
    get() = this is Result.Loading

/**
 * Returns the data if this is [Result.Success] or null otherwise.
 */
fun <T> Result<T>.getOrNull(): T? {
    return if (this is Result.Success) {
        data
    } else {
        null
    }
}

/**
 * Returns the data if this is [Result.Success] or throws otherwise.
 */
fun <T> Result<T>.getOrThrow(): T {
    when (this) {
        is Result.Success -> return data
        is Result.Error -> throw error ?: Exception(message)
        is Result.Loading -> throw IllegalStateException("Result is in Loading state")
    }
}

/**
 * Maps the current result to a new result using the provided transform function.
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(message, error)
        is Result.Loading -> Result.Loading
    }
}

/**
 * Returns the result of the given [transform] function applied to the encapsulated value
 * if this instance is [Result.Success]. Otherwise, returns this instance.
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return when (this) {
        is Result.Success -> transform(data)
        is Result.Error -> Result.Error(message, error)
        is Result.Loading -> Result.Loading
    }
}

/**
 * Executes the given [action] if this is a [Result.Success].
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

/**
 * Executes the given [action] if this is a [Result.Error].
 */
inline fun <T> Result<T>.onError(action: (String, Exception?) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(message, error)
    }
    return this
}

/**
 * Executes the given [action] if this is a [Result.Loading].
 */
inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) {
        action()
    }
    return this
}