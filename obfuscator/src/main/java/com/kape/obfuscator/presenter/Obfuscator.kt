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

import com.kape.obfuscator.data.externals.CoroutineContext
import com.kape.obfuscator.domain.controllers.StartProcessController
import com.kape.obfuscator.domain.controllers.StopProcessController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class Obfuscator(
    private val startProcessController: StartProcessController,
    private val stopProcessController: StopProcessController,
    private val coroutineContext: CoroutineContext
) : ObfuscatorAPI {

    private val moduleCoroutineContext: kotlin.coroutines.CoroutineContext =
        coroutineContext.getModuleCoroutineContext().getOrThrow()
    private val clientCoroutineContext: kotlin.coroutines.CoroutineContext =
        coroutineContext.getClientCoroutineContext().getOrThrow()

    // region ShadowsocksAPI
    override fun start(
        commandLineParams: List<String>,
        obfuscatorProcessEventHandler: ObfuscatorProcessEventHandler,
        callback: ObfuscatorCallback
    ) {
        CoroutineScope(moduleCoroutineContext).launch {
            val result = startProcessController(
                commandLineParams = commandLineParams,
                obfuscatorProcessEventHandler = obfuscatorProcessEventHandler
            )
            launch(clientCoroutineContext) {
                callback(result)
            }
        }
    }

    override fun stop(callback: ObfuscatorCallback) {
        CoroutineScope(moduleCoroutineContext).launch {
            val result = stopProcessController()
            launch(clientCoroutineContext) {
                callback(result)
            }
        }
    }
    // endregion
}
