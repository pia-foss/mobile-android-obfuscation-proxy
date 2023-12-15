package com.kape.obfuscator.presenter

import android.content.Context

/*
 *  Copyright (c) 2023 Private Internet Access, Inc.
 *
 *  This file is part of the Private Internet Access Android Client.
 *
 *  The Private Internet Access Android Client is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  The Private Internet Access Android Client is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License along with the Private
 *  Internet Access Android Client.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Builder class responsible for creating an instance of an object conforming to the `ObfuscatorAPI`
 * interface.
 */
public class ObfuscatorBuilder {
    private var context: Context? = null
    private var clientCoroutineContext: kotlin.coroutines.CoroutineContext? = null

    /**
     * It sets the context to be used within the module.
     *
     * @param context `Context`.
     */
    fun setContext(context: Context): ObfuscatorBuilder = apply {
        this.context = context
    }

    /**
     * Sets the coroutine context to use when invoking the API callbacks.
     *
     * @param clientCoroutineContext `CoroutineContext`.
     */
    fun setClientCoroutineContext(
        clientCoroutineContext: kotlin.coroutines.CoroutineContext
    ): ObfuscatorBuilder = apply {
        this.clientCoroutineContext = clientCoroutineContext
    }

    /**
     * @return `ObfuscatorAPI`.
     */
    fun build(): ObfuscatorAPI {
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
    ): ObfuscatorAPI {
        return Obfuscator()
    }
    // endregion
}
