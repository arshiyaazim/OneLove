package com.kilagee.onelove.domain.model

/**
 * A generic class that holds a value with its loading status.
 * @param <T> Type of the resource data
 */
sealed class Resource<out T> {
    /**
     * Status: loading with no data yet
     */
    object Loading : Resource<Nothing>()
    
    /**
     * Status: success with data
     */
    data class Success<out T>(val data: T) : Resource<T>()
    
    /**
     * Status: error with message
     */
    data class Error(val message: String) : Resource<Nothing>()
    
    companion object {
        /**
         * Helper method to create error Resource
         */
        fun error(message: String): Error = Error(message)
        
        /**
         * Helper method to create success Resource
         */
        fun <T> success(data: T): Success<T> = Success(data)
    }
}