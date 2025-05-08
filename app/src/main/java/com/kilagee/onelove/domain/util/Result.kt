package com.kilagee.onelove.domain.util

/**
 * A sealed class that holds a successful outcome with data of type [T] or a failure with an optional error message.
 * 
 * Used throughout the app to handle success/failure outcomes in a consistent way.
 */
sealed class Result<out T> {
    /**
     * Represents successful operations with data
     */
    data class Success<out T>(val data: T) : Result<T>()
    
    /**
     * Represents failed operations with optional error message and throwable
     */
    data class Error(
        val message: String? = null,
        val exception: Throwable? = null
    ) : Result<Nothing>()
    
    /**
     * Represents operations in progress
     */
    object Loading : Result<Nothing>()
    
    /**
     * Returns true if this result represents a successful outcome
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Returns true if this result represents a failed outcome
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Returns true if this result represents a loading state
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Returns the encapsulated data if this instance represents [Success] or null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Returns the encapsulated data if this instance represents [Success] or calls [defaultValue] function otherwise
     */
    inline fun getOrElse(defaultValue: () -> T): T = when (this) {
        is Success -> data
        else -> defaultValue()
    }
    
    /**
     * Returns this if result is [Success] or executes [transform] function with this error result and returns the result
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Error) -> R,
        onLoading: () -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(this)
        is Loading -> onLoading()
    }
    
    /**
     * Maps the success value of this result using [transform]
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }
    
    /**
     * Maps the error value of this result using [transform]
     */
    inline fun mapError(transform: (Error) -> Error): Result<T> = when (this) {
        is Success -> this
        is Error -> transform(this)
        is Loading -> this
    }
    
    companion object {
        /**
         * Creates a success result with the given data
         */
        fun <T> success(data: T): Result<T> = Success(data)
        
        /**
         * Creates an error result with the given message and exception
         */
        fun error(message: String? = null, exception: Throwable? = null): Result<Nothing> = 
            Error(message, exception)
        
        /**
         * Creates a loading result
         */
        fun <T> loading(): Result<T> = Loading
        
        /**
         * Wraps a block of code that may throw an exception into a Result
         */
        inline fun <T> runCatching(block: () -> T): Result<T> = try {
            Success(block())
        } catch (e: Exception) {
            Error(e.message, e)
        }
    }
}