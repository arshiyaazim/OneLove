package com.kilagee.onelove.domain.util

/**
 * A generic class that holds a value or an error.
 * @param T Type of the wrapped value
 */
sealed class Result<out T> {
    /**
     * Success state with the resulting value
     * @param data The resulting data
     */
    data class Success<out T>(val data: T) : Result<T>()
    
    /**
     * Error state with the exception or error message
     * @param exception The exception that caused the error
     * @param message Optional error message
     * @param code Optional error code (e.g., HTTP status code)
     */
    data class Error(
        val exception: Throwable? = null,
        val message: String? = null,
        val code: Int? = null
    ) : Result<Nothing>()
    
    /**
     * Loading state for operations that take time to complete
     */
    object Loading : Result<Nothing>()
    
    /**
     * Execute a block if the result is successful
     * @param block Function to execute with the success data
     * @return The original Result for chaining
     */
    inline fun onSuccess(block: (T) -> Unit): Result<T> {
        if (this is Success) {
            block(data)
        }
        return this
    }
    
    /**
     * Execute a block if the result is an error
     * @param block Function to execute with the error
     * @return The original Result for chaining
     */
    inline fun onError(block: (Error) -> Unit): Result<T> {
        if (this is Error) {
            block(this)
        }
        return this
    }
    
    /**
     * Execute a block if the result is loading
     * @param block Function to execute
     * @return The original Result for chaining
     */
    inline fun onLoading(block: () -> Unit): Result<T> {
        if (this is Loading) {
            block()
        }
        return this
    }
    
    /**
     * Map successful result to a new result with a different type
     * @param transform Function to transform the success data
     * @return New Result with transformed data or the original error/loading state
     */
    inline fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            is Loading -> Loading
        }
    }
    
    /**
     * Get the value or null if the result is not successful
     * @return The wrapped value or null
     */
    fun getOrNull(): T? {
        return when (this) {
            is Success -> data
            else -> null
        }
    }
    
    /**
     * Get the value or a default value if the result is not successful
     * @param defaultValue The default value to return if the result is not successful
     * @return The wrapped value or the default value
     */
    fun getOrDefault(defaultValue: T): T {
        return when (this) {
            is Success -> data
            else -> defaultValue
        }
    }
    
    /**
     * Get the value or throw the exception if the result is an error
     * @return The wrapped value
     * @throws Throwable The exception in the error state
     */
    fun getOrThrow(): T {
        when (this) {
            is Success -> return data
            is Error -> throw exception ?: IllegalStateException(message ?: "Unknown error")
            is Loading -> throw IllegalStateException("Result is still loading")
        }
    }
    
    companion object {
        /**
         * Create a success result
         * @param data The data to wrap
         * @return A Success Result
         */
        fun <T> success(data: T): Result<T> = Success(data)
        
        /**
         * Create an error result
         * @param exception The exception that caused the error
         * @param message Optional error message
         * @param code Optional error code
         * @return An Error Result
         */
        fun <T> error(
            exception: Throwable? = null,
            message: String? = null,
            code: Int? = null
        ): Result<T> = Error(exception, message, code)
        
        /**
         * Create a loading result
         * @return A Loading Result
         */
        fun <T> loading(): Result<T> = Loading
        
        /**
         * Wrap a block of code in a try-catch and return a Result
         * @param block The code to execute and wrap in a Result
         * @return Success if the block completes without exceptions, Error otherwise
         */
        inline fun <T> runCatching(block: () -> T): Result<T> {
            return try {
                Success(block())
            } catch (e: Throwable) {
                Error(e, e.message)
            }
        }
    }
}