/*
 * Copyright (c) "2023" Private Internet Access, Inc.
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
 *
 */

package com.kape.obfuscator.presenter

import android.content.Context
import com.kape.obfuscator.data.externals.Cache
import com.kape.obfuscator.data.externals.CacheImpl
import com.kape.obfuscator.data.externals.CoroutineContext
import com.kape.obfuscator.data.externals.CoroutineContextImpl
import com.kape.obfuscator.data.externals.FilePath
import com.kape.obfuscator.data.externals.FilePathImpl
import com.kape.obfuscator.data.externals.ShadowsocksProcess
import com.kape.obfuscator.data.externals.ShadowsocksProcessImpl
import com.kape.obfuscator.domain.controllers.StartProcessController
import com.kape.obfuscator.domain.controllers.StartProcessControllerImpl
import com.kape.obfuscator.domain.controllers.StopProcessController
import com.kape.obfuscator.domain.controllers.StopProcessControllerImpl
import com.kape.obfuscator.domain.usecases.ClearCache
import com.kape.obfuscator.domain.usecases.ClearCacheImpl
import com.kape.obfuscator.domain.usecases.CreateProcessListeningOnDeferrable
import com.kape.obfuscator.domain.usecases.CreateProcessListeningOnDeferrableImpl
import com.kape.obfuscator.domain.usecases.HandleProcessErrorOutput
import com.kape.obfuscator.domain.usecases.HandleProcessErrorOutputImpl
import com.kape.obfuscator.domain.usecases.HandleProcessSuccessOutput
import com.kape.obfuscator.domain.usecases.HandleProcessSuccessOutputImpl
import com.kape.obfuscator.domain.usecases.IsProcessRunning
import com.kape.obfuscator.domain.usecases.IsProcessRunningImpl
import com.kape.obfuscator.domain.usecases.IsProcessStopped
import com.kape.obfuscator.domain.usecases.IsProcessStoppedImpl
import com.kape.obfuscator.domain.usecases.ProcessOutputHandler
import com.kape.obfuscator.domain.usecases.StartProcess
import com.kape.obfuscator.domain.usecases.StartProcessImpl
import com.kape.obfuscator.domain.usecases.StartProcessOutputHandler
import com.kape.obfuscator.domain.usecases.StartProcessOutputHandlerImpl
import com.kape.obfuscator.domain.usecases.StartProcessOutputReader
import com.kape.obfuscator.domain.usecases.StartProcessOutputReaderImpl
import com.kape.obfuscator.domain.usecases.StopProcess
import com.kape.obfuscator.domain.usecases.StopProcessImpl
import com.kape.obfuscator.domain.usecases.WaitForProcessListeningOnDeferrable
import com.kape.obfuscator.domain.usecases.WaitForProcessListeningOnDeferrableImpl

/**
 * Builder class responsible for creating an instance of an object conforming to the `ShadowsocksAPI`
 * interface.
 */
class ObfuscatorBuilder {
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
     * @return `ShadowsocksAPI`.
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
        return initializeExternals(
            context = context,
            clientCoroutineContext = clientCoroutineContext
        )
    }

    private fun initializeExternals(
        context: Context,
        clientCoroutineContext: kotlin.coroutines.CoroutineContext
    ): ObfuscatorAPI {
        val cache: Cache = CacheImpl()
        val coroutineContext: CoroutineContext = CoroutineContextImpl(
            clientCoroutineContext = clientCoroutineContext
        )
        val filePath: FilePath = FilePathImpl(
            context = context
        )
        val shadowsocksProcess: ShadowsocksProcess = ShadowsocksProcessImpl(
            filePath = filePath
        )
        return initializeUseCases(
            cache = cache,
            shadowsocksProcess = shadowsocksProcess,
            coroutineContext = coroutineContext
        )
    }

    private fun initializeUseCases(
        cache: Cache,
        shadowsocksProcess: ShadowsocksProcess,
        coroutineContext: CoroutineContext
    ): ObfuscatorAPI {
        val clearCache: ClearCache = ClearCacheImpl(
            cache = cache
        )
        val isProcessRunning: IsProcessRunning = IsProcessRunningImpl(
            cache = cache
        )
        val isProcessStopped: IsProcessStopped = IsProcessStoppedImpl(
            cache = cache
        )
        val createProcessListeningOnDeferrable: CreateProcessListeningOnDeferrable =
            CreateProcessListeningOnDeferrableImpl(
                cache = cache
            )
        val handleProcessErrorOutput: HandleProcessErrorOutput = HandleProcessErrorOutputImpl()
        val handleProcessSuccessOutput: HandleProcessSuccessOutput = HandleProcessSuccessOutputImpl(
            cache = cache
        )
        val startProcessOutputHandler: StartProcessOutputHandler = StartProcessOutputHandlerImpl(
            handleProcessErrorOutput = handleProcessErrorOutput,
            handleProcessSuccessOutput = handleProcessSuccessOutput
        )
        val processOutputHandler: ProcessOutputHandler = StartProcessOutputHandlerImpl(
            handleProcessErrorOutput = handleProcessErrorOutput,
            handleProcessSuccessOutput = handleProcessSuccessOutput
        )
        val startProcess: StartProcess = StartProcessImpl(
            cache = cache,
            shadowsocksProcess = shadowsocksProcess
        )
        val startProcessOutputReader: StartProcessOutputReader = StartProcessOutputReaderImpl(
            cache = cache,
            coroutineContext = coroutineContext
        )
        val stopProcess: StopProcess = StopProcessImpl(
            cache = cache,
            shadowsocksProcess = shadowsocksProcess
        )
        val waitForProcessListeningOnDeferrable: WaitForProcessListeningOnDeferrable =
            WaitForProcessListeningOnDeferrableImpl(
                cache = cache
            )
        return initializeControllers(
            coroutineContext = coroutineContext,
            clearCache = clearCache,
            isProcessRunning = isProcessRunning,
            isProcessStopped = isProcessStopped,
            createProcessListeningOnDeferrable = createProcessListeningOnDeferrable,
            startProcessOutputHandler = startProcessOutputHandler,
            processOutputHandler = processOutputHandler,
            startProcess = startProcess,
            startProcessOutputReader = startProcessOutputReader,
            stopProcess = stopProcess,
            waitForProcessListeningOnDeferrable = waitForProcessListeningOnDeferrable
        )
    }

    private fun initializeControllers(
        coroutineContext: CoroutineContext,
        clearCache: ClearCache,
        isProcessRunning: IsProcessRunning,
        isProcessStopped: IsProcessStopped,
        createProcessListeningOnDeferrable: CreateProcessListeningOnDeferrable,
        startProcessOutputHandler: StartProcessOutputHandler,
        processOutputHandler: ProcessOutputHandler,
        startProcess: StartProcess,
        startProcessOutputReader: StartProcessOutputReader,
        stopProcess: StopProcess,
        waitForProcessListeningOnDeferrable: WaitForProcessListeningOnDeferrable
    ): ObfuscatorAPI {
        val startProcessController: StartProcessController = StartProcessControllerImpl(
            isProcessStopped = isProcessStopped,
            createProcessListeningOnDeferrable = createProcessListeningOnDeferrable,
            processOutputHandler = processOutputHandler,
            startProcessOutputHandler = startProcessOutputHandler,
            startProcess = startProcess,
            startProcessOutputReader = startProcessOutputReader,
            waitForProcessListeningOnDeferrable = waitForProcessListeningOnDeferrable,
            clearCache = clearCache
        )
        val stopProcessController: StopProcessController = StopProcessControllerImpl(
            isProcessRunning = isProcessRunning,
            stopProcess = stopProcess,
            clearCache = clearCache
        )
        return Obfuscator(
            startProcessController = startProcessController,
            stopProcessController = stopProcessController,
            coroutineContext = coroutineContext
        )
    }
    // endregion
}
