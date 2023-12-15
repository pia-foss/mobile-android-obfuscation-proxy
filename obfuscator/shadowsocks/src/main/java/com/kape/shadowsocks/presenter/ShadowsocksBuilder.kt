package com.kape.shadowsocks.presenter

import android.content.Context

/**
 * Builder class responsible for creating an instance of an object conforming to the `ShadowsocksAPI`
 * interface.
 */
class ShadowsocksBuilder {
    private var context: Context? = null
    private var clientCoroutineContext: kotlin.coroutines.CoroutineContext? = null

    /**
     * It sets the context to be used within the module.
     *
     * @param context `Context`.
     */
    fun setContext(context: Context): ShadowsocksBuilder = apply {
        this.context = context
    }

    /**
     * Sets the coroutine context to use when invoking the API callbacks.
     *
     * @param clientCoroutineContext `CoroutineContext`.
     */
    fun setClientCoroutineContext(
        clientCoroutineContext: kotlin.coroutines.CoroutineContext
    ): ShadowsocksBuilder = apply {
        this.clientCoroutineContext = clientCoroutineContext
    }

    /**
     * @return `ShadowsocksAPI`.
     */
    fun build(): ShadowsocksAPI {
        val context = this.context
            ?: throw Exception("Context dependency missing.")
        val clientCoroutineContext = this.clientCoroutineContext
            ?: throw Exception("Client CoroutineContext missing.")

        return initializeModule(
            context = context,
            clientCoroutineContext = clientCoroutineContext
        )
    }

    // region private
    private fun initializeModule(
        context: Context,
        clientCoroutineContext: kotlin.coroutines.CoroutineContext
    ): ShadowsocksAPI {
        return Shadowsocks()
    }
    // endregion
}
